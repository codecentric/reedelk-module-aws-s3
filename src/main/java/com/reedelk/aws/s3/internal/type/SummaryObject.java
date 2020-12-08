package com.reedelk.aws.s3.internal.type;

import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Type
@TypeProperty(name = SummaryObject.KEY, type = String.class)
@TypeProperty(name = SummaryObject.ETAG, type = String.class)
@TypeProperty(name = SummaryObject.SIZE, type = long.class)
@TypeProperty(name = SummaryObject.BUCKET_NAME, type = String.class)
@TypeProperty(name = SummaryObject.STORAGE_CLASS, type = String.class)
@TypeProperty(name = SummaryObject.LAST_MODIFIED, type = Date.class)
@TypeProperty(name = SummaryObject.OWNER, type = ObjectOwner.class)
public class SummaryObject extends HashMap<String, Serializable> {

    static final String KEY = "key";
    static final String ETAG = "eTag";
    static final String SIZE = "size";
    static final String BUCKET_NAME = "bucket";
    static final String STORAGE_CLASS = "storageClass";
    static final String LAST_MODIFIED = "lastModified";
    static final String OWNER = "owner";

    public SummaryObject(S3ObjectSummary summary) {
        put(KEY, summary.getKey());
        put(ETAG, summary.getETag());
        put(SIZE, summary.getSize());
        put(BUCKET_NAME, summary.getBucketName());
        put(STORAGE_CLASS, summary.getStorageClass());
        put(LAST_MODIFIED, summary.getLastModified());

        Owner owner = summary.getOwner();
        if (owner != null) put(OWNER, new ObjectOwner(owner));
    }
}

