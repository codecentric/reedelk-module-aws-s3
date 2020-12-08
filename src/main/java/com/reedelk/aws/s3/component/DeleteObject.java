package com.reedelk.aws.s3.component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.reedelk.aws.s3.internal.S3ClientFactory;
import com.reedelk.aws.s3.internal.exception.DeleteObjectException;
import com.reedelk.aws.s3.internal.exception.UploadObjectException;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.reedelk.aws.s3.internal.commons.Messages.DeleteObject.*;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("S3 Delete Object")
@ComponentOutput(
        attributes = MessageAttributes.class,
        payload = ComponentOutput.PreviousComponent.class,
        description = "The S3 Delete Object Component output is the original input message. " +
                "The payload is not changed by this component.")
@ComponentInput(
        payload = Object.class,
        description = "The component input is used to evaluate the dynamic " +
                "values provided for the bucket name and the key of the object to be deleted.")
@Description("The S3 Delete Object Component deletes the specified object in the specified bucket. " +
        "The bucket name can be a dynamic expression and it is mandatory. " +
        "The key property which identifies the file to be deleted can be a dynamic expression as well and it is mandatory.")
@Component(service = DeleteObject.class, scope = PROTOTYPE)
public class DeleteObject implements ProcessorSync {

    @DialogTitle("AWS S3 Configuration")
    @Property("Configuration")
    @Mandatory
    private AwsConfiguration configuration;

    @Property("Bucket Name")
    @Hint("my-bucket")
    @Example("my-bucket")
    @Mandatory
    @Description("The name of the Amazon S3 bucket containing the object to delete.")
    private DynamicString bucket;

    @Property("Key")
    @Hint("my-key")
    @Example("my-key")
    @Description("The key of the object to delete.")
    private DynamicString key;

    @Reference
    ScriptEngineService scriptService;

    private AmazonS3 s3;

    @Override
    public void initialize() {
        requireNotNull(DeleteObject.class, configuration, "S3 configuration is missing. Aws configuration must be provided.");
        requireNotNullOrBlank(DeleteObject.class, bucket, "S3 bucket name is missing. The bucket name is mandatory.");
        requireNotNullOrBlank(DeleteObject.class, key, "S3 key is missing. The key is mandatory.");

        s3 = S3ClientFactory.from(configuration, this);
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String evaluatedBucket = scriptService.evaluate(this.bucket, flowContext, message)
                .orElseThrow(() -> new UploadObjectException(BUCKET_NAME_EMPTY.format(bucket.value())));

        String evaluatedKey = scriptService.evaluate(this.key, flowContext, message)
                .orElseThrow(() -> new UploadObjectException(KEY_EMPTY.format(key.value())));

        try {
            s3.deleteObject(evaluatedBucket, evaluatedKey);
        } catch (SdkClientException exception) {
            String error = DELETE_ERROR.format(evaluatedBucket, evaluatedKey, exception.getMessage());
            throw new DeleteObjectException(error, exception);
        }

        return MessageBuilder.get(DeleteObject.class)
                .withTypedContent(message.content())
                .build();
    }

    @Override
    public void dispose() {
        S3ClientFactory.release(configuration, this);
    }

    public void setConfiguration(AwsConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setBucket(DynamicString bucket) {
        this.bucket = bucket;
    }

    public void setKey(DynamicString key) {
        this.key = key;
    }
}
