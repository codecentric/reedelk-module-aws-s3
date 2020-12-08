package com.reedelk.aws.s3.internal.attribute;

import com.amazonaws.services.s3.model.CopyObjectResult;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Date;

@Type
@TypeProperty(name = CopyObjectAttributes.ETAG, type = String.class)
@TypeProperty(name = CopyObjectAttributes.VERSION_ID, type = String.class)
@TypeProperty(name = CopyObjectAttributes.LAST_MODIFIED, type = Date.class)
@TypeProperty(name = CopyObjectAttributes.EXPIRATION_TIME, type = Date.class)
public class CopyObjectAttributes extends MessageAttributes {

    static final String ETAG = "eTag";
    static final String VERSION_ID = "versionId";
    static final String LAST_MODIFIED = "lastModified";
    static final String EXPIRATION_TIME = "expirationTime";

    public CopyObjectAttributes(CopyObjectResult result) {
        put(ETAG, result.getETag());
        put(VERSION_ID, result.getVersionId());
        put(LAST_MODIFIED, result.getLastModifiedDate());
        put(EXPIRATION_TIME, result.getExpirationTime());
    }
}