package com.reedelk.aws.s3.internal.attribute;


import com.amazonaws.services.s3.model.PutObjectResult;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Date;

@Type
@TypeProperty(name = UploadObjectAttributes.ETAG, type = String.class)
@TypeProperty(name = UploadObjectAttributes.VERSION_ID, type = String.class)
@TypeProperty(name = UploadObjectAttributes.CONTENT_MD5, type = String.class)
@TypeProperty(name = UploadObjectAttributes.EXPIRATION_TIME, type = Date.class)
public class UploadObjectAttributes extends MessageAttributes {

    static final String ETAG = "eTag";
    static final String VERSION_ID = "versionId";
    static final String CONTENT_MD5 = "contentMd5";
    static final String EXPIRATION_TIME = "expirationTime";

    public UploadObjectAttributes(PutObjectResult result) {
        put(ETAG, result.getETag());
        put(VERSION_ID, result.getVersionId());
        put(CONTENT_MD5, result.getContentMd5());
        put(EXPIRATION_TIME, result.getExpirationTime());
    }
}
