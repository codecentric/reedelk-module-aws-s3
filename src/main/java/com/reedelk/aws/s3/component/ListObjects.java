package com.reedelk.aws.s3.component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.reedelk.aws.s3.internal.S3ClientFactory;
import com.reedelk.aws.s3.internal.attribute.ListObjectsAttributes;
import com.reedelk.aws.s3.internal.commons.Messages;
import com.reedelk.aws.s3.internal.exception.ListObjectsException;
import com.reedelk.aws.s3.internal.exception.UploadObjectException;
import com.reedelk.aws.s3.internal.type.ListOfSummaryObject;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

import static com.reedelk.aws.s3.internal.commons.Messages.ListObjects.LIST_ERROR;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("S3 List Objects")
@ComponentOutput(
        attributes = ListObjectsAttributes.class,
        payload = ListOfSummaryObject.class,
        description = "A list of S3 Objects found in the given bucket.")
@ComponentInput(
        payload = Object.class,
        description = "The component input is used to evaluate the dynamic value provided for the bucket name and continuation token.")
@Description("The S3 List Objects Component returns a list of summary information about the objects in the specified bucket. " +
        "Because buckets can contain a virtually unlimited number of keys, the complete results of a list query can be extremely large. " +
        "To manage large result sets, Amazon S3 uses pagination to split them into multiple responses. " +
        "Always check the output attribute 'truncated' to see if the returned listing is complete or additional " +
        "calls are needed to get more results. The attribute named 'nextContinuationToken' provides the next token " +
        "to be used to fetch the next batch of data. The 'Continuation Token' property can be set from the 'Advanced' tab in the component properties.")
@Component(service = ListObjects.class, scope = PROTOTYPE)
public class ListObjects implements ProcessorSync {

    @DialogTitle("AWS S3 Configuration")
    @Property("Configuration")
    @Mandatory
    private AwsConfiguration configuration;

    @Property("Bucket Name")
    @Hint("my-bucket")
    @Example("my-bucket")
    @Mandatory
    @Description("The name of an existing bucket, to which you have Permission.Read permission.")
    private DynamicString bucket;

    @Group("Advanced")
    @Property("Max Keys")
    @Hint("20")
    @Example("50")
    @Description("Sets the optional maxKeys parameter indicating the maximum number of keys to return.")
    private Integer maxKeys;

    @Group("Advanced")
    @Property("Continuation Token")
    @Hint("my-continuation-token")
    @Example("my-continuation-token")
    @Description("Sets the optional continuation token. " +
            "The continuation token is returned as attribute in the first call to this component when paging.")
    private DynamicString continuationToken;

    @Reference
    ScriptEngineService scriptService;

    private AmazonS3 s3;

    @Override
    public void initialize() {
        requireNotNull(UploadObject.class, configuration, "S3 configuration is missing. Aws configuration must be provided.");
        requireNotNullOrBlank(UploadObject.class, bucket, "S3 bucket name is missing. The bucket name is mandatory.");

        s3 = S3ClientFactory.from(configuration, this);
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String evaluatedBucket = scriptService.evaluate(this.bucket, flowContext, message)
                .orElseThrow(() -> new UploadObjectException(Messages.ListObjects.BUCKET_NAME_EMPTY.format(bucket.value())));

        ListObjectsV2Request request = new ListObjectsV2Request();
        request.withBucketName(evaluatedBucket);

        if (isNotNullOrBlank(continuationToken)) {
            scriptService.evaluate(this.continuationToken, flowContext, message)
                    .ifPresent(request::withContinuationToken);
        }

        if (maxKeys != null) request.withMaxKeys(maxKeys);

        ListObjectsV2Result result;
        try {
            result = s3.listObjectsV2(request);
        } catch (SdkClientException exception) {
            String error = LIST_ERROR.format(evaluatedBucket, exception.getMessage());
            throw new ListObjectsException(error, exception);
        }


        List<S3ObjectSummary> objects = result.getObjectSummaries();
        ListOfSummaryObject summaryObjects = new ListOfSummaryObject(objects);
        ListObjectsAttributes attributes = new ListObjectsAttributes(result);

        return MessageBuilder.get(ListObjects.class)
                .withJavaObject(summaryObjects)
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

    public void setContinuationToken(DynamicString continuationToken) {
        this.continuationToken = continuationToken;
    }

    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }
}
