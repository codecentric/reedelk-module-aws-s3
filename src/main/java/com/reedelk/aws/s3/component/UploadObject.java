package com.reedelk.aws.s3.component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.reedelk.aws.s3.internal.S3ClientFactory;
import com.reedelk.aws.s3.internal.attribute.UploadObjectAttributes;
import com.reedelk.aws.s3.internal.exception.UploadObjectException;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.reedelk.aws.s3.internal.commons.Messages.UploadObject.BUCKET_NAME_EMPTY;
import static com.reedelk.aws.s3.internal.commons.Messages.UploadObject.KEY_EMPTY;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Input;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("S3 Upload Object")
@ComponentOutput(attributes = UploadObjectAttributes.class,
        payload = ComponentOutput.PreviousComponent.class,
        description = "The S3 Upload Object Component output is the original input message. " +
                "The payload is not changed by this component.")
@ComponentInput(
        payload = { String.class, byte[].class },
        description = "The expected input is a string or a byte array of the data to be uploaded on the S3 bucket.")
@Description("The S3 Upload Object Component allows to upload an object to a specified AWS S3 bucket. " +
        "The bucket name can be a dynamic expression and it is mandatory. " +
        "The key property which identifies where the file will be stored can be a dynamic expression as well and it is mandatory.")
@Component(service = UploadObject.class, scope = PROTOTYPE)
public class UploadObject implements ProcessorSync {

    @DialogTitle("AWS S3 Configuration")
    @Property("Configuration")
    @Mandatory
    private AwsConfiguration configuration;

    @Property("Bucket Name")
    @Hint("my-bucket")
    @Example("my-bucket")
    @Mandatory
    @Description("The name of an existing bucket, to which you have Permission.Write permission.")
    private DynamicString bucket;

    @Property("Key")
    @Hint("my-key")
    @Example("my-key")
    @Description("The key under which to store the specified file.")
    private DynamicString key;

    @Reference
    ConverterService converterService;
    @Reference
    ScriptEngineService scriptService;

    private AmazonS3 s3;

    @Override
    public void initialize() {
        requireNotNull(UploadObject.class, configuration, "S3 configuration is missing. Aws configuration must be provided.");
        requireNotNullOrBlank(UploadObject.class, bucket, "S3 bucket name is missing. The bucket name is mandatory.");
        requireNotNullOrBlank(UploadObject.class, key, "S3 key is missing. The key is mandatory.");

        s3 = S3ClientFactory.from(configuration, this);
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        Object input = message.payload();

        Input.requireTypeMatchesAny(UploadObject.class, input, String.class, byte[].class, Byte[].class);

        byte[] inputAsByteArray = converterService.convert(input, byte[].class);

        String evaluatedBucket = scriptService.evaluate(this.bucket, flowContext, message)
                .orElseThrow(() -> new UploadObjectException(BUCKET_NAME_EMPTY.format(bucket.value())));

        String evaluatedKey = scriptService.evaluate(this.key, flowContext, message)
                .orElseThrow(() -> new UploadObjectException(KEY_EMPTY.format(key.value())));

        try (InputStream is = new ByteArrayInputStream(inputAsByteArray)) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(message.content().mimeType().toString());
            objectMetadata.setContentLength(inputAsByteArray.length);

            PutObjectResult result = s3.putObject(evaluatedBucket, evaluatedKey, is, objectMetadata);

            UploadObjectAttributes attributes = new UploadObjectAttributes(result);
            return MessageBuilder.get(UploadObject.class)
                    .withTypedContent(message.getContent())
                    .attributes(attributes)
                    .build();

        } catch (IOException | SdkClientException exception) {
            String error = exception.getMessage();
            throw new UploadObjectException(error, exception);
        }
    }

    @Override
    public void dispose() {
        S3ClientFactory.release(configuration, this);
    }

    public AwsConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AwsConfiguration configuration) {
        this.configuration = configuration;
    }

    public DynamicString getBucket() {
        return bucket;
    }

    public void setBucket(DynamicString bucket) {
        this.bucket = bucket;
    }

    public DynamicString getKey() {
        return key;
    }

    public void setKey(DynamicString key) {
        this.key = key;
    }
}
