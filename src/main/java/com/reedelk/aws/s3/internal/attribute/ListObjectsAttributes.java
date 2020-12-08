package com.reedelk.aws.s3.internal.attribute;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

@Type
@TypeProperty(name = ListObjectsAttributes.TRUNCATED, type = boolean.class)
@TypeProperty(name = ListObjectsAttributes.KEY_COUNT, type = int.class)
@TypeProperty(name = ListObjectsAttributes.NEXT_CONTINUATION_TOKEN, type = String.class)
public class ListObjectsAttributes extends MessageAttributes {

    static final String TRUNCATED = "truncated";
    static final String KEY_COUNT = "keyCount";
    static final String NEXT_CONTINUATION_TOKEN = "nextContinuationToken";

    public ListObjectsAttributes(ListObjectsV2Result result) {
        put(TRUNCATED, result.isTruncated());
        put(KEY_COUNT, result.getKeyCount());
        put(NEXT_CONTINUATION_TOKEN, result.getNextContinuationToken());
    }
}
