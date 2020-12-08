package com.reedelk.aws.s3.component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.reedelk.aws.s3.internal.S3ClientFactory;
import com.reedelk.aws.s3.internal.exception.DeleteObjectsException;
import com.reedelk.aws.s3.internal.type.ListOfDeletedObjects;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

import static com.reedelk.aws.s3.internal.commons.Messages.DeleteObjects.*;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Input;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("S3 Delete Objects")
@ComponentOutput(
        attributes = MessageAttributes.class,
        payload = ListOfDeletedObjects.class,
        description = "A list of delete objects")
@ComponentInput(
        payload = Object.class,
        description = "The input payload is used to evaluate the bucket name and the list of keys of the objects to be deleted.")
@Description("The S3 Delete Objects Component deletes multiple objects in a single bucket from S3. " +
        "The bucket name and the list of keys pointing to the files to be deleted can be expressed as dynamic values.")
@Component(service = DeleteObjects.class, scope = PROTOTYPE)
public class DeleteObjects implements ProcessorSync {

    @DialogTitle("AWS S3 Configuration")
    @Property("Configuration")
    @Mandatory
    private AwsConfiguration configuration;

    @Property("Bucket Name")
    @Hint("my-bucket")
    @Example("my-bucket")
    @Mandatory
    @Description("The name of the Amazon S3 bucket containing the objects to delete.")
    private DynamicString bucket;

    @Property("Keys List")
    @Example("#[['key1', 'key2']]")
    @InitValue("#[['key1', 'key2']]")
    @Description("A list of keys of the objects to delete.")
    private DynamicObject keys;

    @Reference
    ScriptEngineService scriptService;

    private AmazonS3 s3;

    @Override
    public void initialize() {
        requireNotNull(DeleteObject.class, configuration, "S3 configuration is missing. Aws configuration must be provided.");
        requireNotNullOrBlank(DeleteObject.class, bucket, "S3 bucket name is missing. The bucket name is mandatory.");
        requireNotNullOrBlank(DeleteObject.class, keys, "S3 keys are missing. The keys are mandatory.");

        s3 = S3ClientFactory.from(configuration, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String evaluatedBucket = scriptService.evaluate(this.bucket, flowContext, message)
                .orElseThrow(() -> new DeleteObjectsException(BUCKET_NAME_EMPTY.format(bucket.value())));

        Object keysList = scriptService.evaluate(this.keys, flowContext, message)
                .orElseThrow(() -> new DeleteObjectsException(KEYS_EMPTY.format(keys.value())));

        Input.requireTypeMatches(DeleteObjects.class, keysList, List.class);

        String[] allKeys = asStringArray((List<Object>) keysList);

        DeleteObjectsRequest request = new DeleteObjectsRequest(evaluatedBucket)
                .withKeys(allKeys);

        DeleteObjectsResult result;
        try {
            result = s3.deleteObjects(request);
        } catch (SdkClientException exception) {
            String error = DELETE_ERROR.format(evaluatedBucket, allKeys, exception.getMessage());
            throw new DeleteObjectsException(error, exception);
        }

        List<DeleteObjectsResult.DeletedObject> deletedObjects = result.getDeletedObjects();
        ListOfDeletedObjects listOfDeletedObjects = new ListOfDeletedObjects(deletedObjects);
        return MessageBuilder.get(DeleteObject.class)
                .withJavaObject(listOfDeletedObjects)
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

    public void setKeys(DynamicObject keys) {
        this.keys = keys;
    }

    private String[] asStringArray(List<Object> keysList) {
        String[] allKeys = new String[keysList.size()];
        for (int i = 0; i < keysList.size(); i++) {
            Object key = keysList.get(i);
            // We must make sure that each key is a string type.
            Input.requireTypeMatches(DeleteObjects.class, key, String.class);
            allKeys[i] = (String) key;
        }
        return allKeys;
    }
}
