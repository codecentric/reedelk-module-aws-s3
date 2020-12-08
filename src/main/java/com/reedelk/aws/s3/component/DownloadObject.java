package com.reedelk.aws.s3.component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.reedelk.aws.s3.internal.S3ClientFactory;
import com.reedelk.aws.s3.internal.attribute.DownloadObjectAttributes;
import com.reedelk.aws.s3.internal.exception.DownloadObjectException;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.reedelk.aws.s3.internal.commons.Messages.DownloadObject.*;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("S3 Download Object")
@ComponentOutput(
        attributes = DownloadObjectAttributes.class,
        payload = byte[].class,
        description = "A byte array containing the data of the object downloaded from AWS S3.")
@ComponentInput(
        payload = Object.class,
        description = "The component input is used to evaluate the dynamic " +
                "values provided for the bucket name and the key of the desired object to be downloaded.")
@Description("Gets the object stored in Amazon S3 under the specified bucket and key. " +
        "To get an object from Amazon S3, the caller must have Permission.Read access to the object.")
@Component(service = DownloadObject.class, scope = PROTOTYPE)
public class DownloadObject implements ProcessorSync {

    @DialogTitle("AWS S3 Configuration")
    @Property("Configuration")
    @Mandatory
    private AwsConfiguration configuration;

    @Property("Bucket Name")
    @Hint("my-bucket")
    @Example("my-bucket")
    @Mandatory
    @Description("The name of the bucket containing the desired object.")
    private DynamicString bucket;

    @Property("Key")
    @Hint("my-key")
    @Example("my-key")
    @Description("The key under which the desired object is stored.")
    private DynamicString key;

    @Reference
    ScriptEngineService scriptService;

    private AmazonS3 s3;

    @Override
    public void initialize() {
        requireNotNull(DownloadObject.class, configuration, "S3 configuration is missing. Aws configuration must be provided.");
        requireNotNullOrBlank(DownloadObject.class, bucket, "S3 bucket name is missing. The bucket name is mandatory.");
        requireNotNullOrBlank(DownloadObject.class, key, "S3 key is missing. The key is mandatory.");

        s3 = S3ClientFactory.from(configuration, this);
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String evaluatedBucket = scriptService.evaluate(this.bucket, flowContext, message)
                .orElseThrow(() -> new DownloadObjectException(BUCKET_NAME_EMPTY.format(bucket.value())));

        String evaluatedKey = scriptService.evaluate(this.key, flowContext, message)
                .orElseThrow(() -> new DownloadObjectException(KEY_EMPTY.format(key.value())));

        S3Object object;
        try {
            object = s3.getObject(evaluatedBucket, evaluatedKey);
        } catch (SdkClientException exception) {
            String error = DOWNLOAD_ERROR.format(evaluatedBucket, evaluatedKey, exception.getMessage());
            throw new DownloadObjectException(error, exception);
        }

        byte[] data;
        try (S3ObjectInputStream s3is = object.getObjectContent();
             ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            data = toByteArray(s3is, fos);
        } catch (IOException exception) {
            String error = DOWNLOAD_ERROR.format(evaluatedBucket, evaluatedKey, exception.getMessage());
            throw new DownloadObjectException(error, exception);
        }

        ObjectMetadata objectMetadata = object.getObjectMetadata();
        String contentType = objectMetadata.getContentType();
        MimeType mimeType = MimeType.parse(contentType, MimeType.UNKNOWN);

        MessageAttributes attributes = new DownloadObjectAttributes(objectMetadata);
        return MessageBuilder.get(DownloadObject.class)
                .withBinary(data, mimeType)
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

    public void setBucket(DynamicString bucket) {
        this.bucket = bucket;
    }

    public void setKey(DynamicString key) {
        this.key = key;
    }

    private byte[] toByteArray(S3ObjectInputStream s3is, ByteArrayOutputStream fos) throws IOException {
        byte[] read_buf = new byte[1024];
        int read_len = 0;
        while ((read_len = s3is.read(read_buf)) > 0) {
            fos.write(read_buf, 0, read_len);
        }
        return fos.toByteArray();
    }
}
