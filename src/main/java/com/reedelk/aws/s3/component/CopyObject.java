package com.reedelk.aws.s3.component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.reedelk.aws.s3.internal.S3ClientFactory;
import com.reedelk.aws.s3.internal.attribute.CopyObjectAttributes;
import com.reedelk.aws.s3.internal.exception.CopyObjectException;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.reedelk.aws.s3.internal.commons.Messages.CopyObject.*;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("S3 Copy Object")
@ComponentOutput(
        attributes = CopyObjectAttributes.class,
        payload = ComponentOutput.PreviousComponent.class,
        description = "The S3 Copy Object Component output is the original input message. " +
                "The payload is not changed by this component.")
@ComponentInput(
        payload = Object.class,
        description = "The component input is used to evaluate the dynamic " +
                "values provided for source bucket, source key, destination bucket and destination key.")
@Description("The S3 Copy Object Component copies a source object to a new destination in Amazon S3. " +
        "The source bucket, source key, destination bucket and destination key can be expressed as dynamic values. " +
        "To copy an object, the caller's account must have read access to the source object and write access to the destination bucket.")
@Component(service = CopyObject.class, scope = PROTOTYPE)
public class CopyObject implements ProcessorSync {

    @DialogTitle("AWS S3 Configuration")
    @Property("Configuration")
    @Mandatory
    private AwsConfiguration configuration;

    @Property("Source Bucket")
    @Mandatory
    @Hint("source-bucket")
    @Example("source-bucket")
    @Description("The name of the bucket containing the source object to copy.")
    private DynamicString sourceBucket;

    @Property("Source Key")
    @Hint("source-key")
    @Example("source-key")
    @Description("The key in the source bucket under which the source object is stored.")
    private DynamicString sourceKey;

    @Property("Destination Bucket")
    @Mandatory
    @Hint("destination-bucket")
    @Example("destination-bucket")
    @Description("The name of the bucket in which the new object will be created. This can be the same name as the source bucket's.")
    private DynamicString destinationBucket;

    @Property("Destination Key")
    @Hint("destination-key")
    @Example("destination-key")
    @Description("The key in the destination bucket under which the new object will be created.")
    private DynamicString destinationKey;

    @Reference
    ScriptEngineService scriptService;

    private AmazonS3 s3;

    @Override
    public void initialize() {
        requireNotNull(CopyObject.class, configuration, "S3 configuration is missing. Aws configuration must be provided.");
        requireNotNullOrBlank(CopyObject.class, sourceBucket, "S3 source bucket name is missing. The source bucket name is mandatory.");
        requireNotNullOrBlank(CopyObject.class, sourceKey, "S3 source key is missing. The source key is mandatory.");
        requireNotNullOrBlank(CopyObject.class, destinationBucket, "S3 destination bucket name is missing. The destination bucket name is mandatory.");
        requireNotNullOrBlank(CopyObject.class, destinationKey, "S3 destination key is missing. The destination key is mandatory.");

        s3 = S3ClientFactory.from(configuration, this);
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String evaluatedSourceBucket = scriptService.evaluate(this.sourceBucket, flowContext, message)
                .orElseThrow(() -> new CopyObjectException(SOURCE_BUCKET_EMPTY.format(sourceBucket.value())));

        String evaluatedSourceKey = scriptService.evaluate(this.sourceKey, flowContext, message)
                .orElseThrow(() -> new CopyObjectException(SOURCE_KEY_EMPTY.format(sourceKey.value())));

        String evaluatedDestinationBucket = scriptService.evaluate(this.destinationBucket, flowContext, message)
                .orElseThrow(() -> new CopyObjectException(DESTINATION_BUCKET_EMPTY.format(destinationBucket.value())));

        String evaluatedDestinationKey = scriptService.evaluate(this.destinationKey, flowContext, message)
                .orElseThrow(() -> new CopyObjectException(DESTINATION_KEY_EMPTY.format(destinationKey.value())));

        CopyObjectResult result;
        try {
            result = s3.copyObject(
                    evaluatedSourceBucket, evaluatedSourceKey,
                    evaluatedDestinationBucket, evaluatedDestinationKey);
        } catch (SdkClientException exception) {
            String error = COPY_ERROR.format(
                    evaluatedSourceBucket, evaluatedSourceKey,
                    evaluatedDestinationBucket, evaluatedDestinationKey,
                    exception.getMessage());
            throw new CopyObjectException(error, exception);
        }

        CopyObjectAttributes attributes = new CopyObjectAttributes(result);

        return MessageBuilder.get(CopyObject.class)
                .withTypedContent(message.content())
                .attributes(attributes)
                .build();
    }

    @Override
    public void dispose() {
        S3ClientFactory.release(configuration, this);
    }

    public void setConfiguration(AwsConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setSourceBucket(DynamicString sourceBucket) {
        this.sourceBucket = sourceBucket;
    }

    public void setSourceKey(DynamicString sourceKey) {
        this.sourceKey = sourceKey;
    }

    public void setDestinationBucket(DynamicString destinationBucket) {
        this.destinationBucket = destinationBucket;
    }

    public void setDestinationKey(DynamicString destinationKey) {
        this.destinationKey = destinationKey;
    }
}
