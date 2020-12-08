package com.reedelk.aws.s3.internal.attribute;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Date;

@Type
@TypeProperty(name = DownloadObjectAttributes.ETAG, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.VERSION_ID, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.CONTENT_MD5, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.STORAGE_CLASS, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.CACHE_CONTROL, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.LAST_MODIFIED, type = Date.class)
@TypeProperty(name = DownloadObjectAttributes.EXPIRATION_TIME, type = Date.class)
@TypeProperty(name = DownloadObjectAttributes.CONTENT_TYPE, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.CONTENT_LENGTH, type = long.class)
@TypeProperty(name = DownloadObjectAttributes.CONTENT_ENCODING, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.CONTENT_LANGUAGE, type = String.class)
@TypeProperty(name = DownloadObjectAttributes.CONTENT_DISPOSITION, type = String.class)
public class DownloadObjectAttributes extends MessageAttributes {

    static final String ETAG = "eTag";
    static final String VERSION_ID = "versionId";
    static final String CONTENT_MD5 = "contentMD5";
    static final String STORAGE_CLASS = "storageClass";
    static final String CACHE_CONTROL = "cacheControl";
    static final String LAST_MODIFIED = "lastModified";
    static final String EXPIRATION_TIME = "expirationTime";
    static final String CONTENT_TYPE = "contentType";
    static final String CONTENT_LENGTH = "contentLength";
    static final String CONTENT_ENCODING = "contentEncoding";
    static final String CONTENT_LANGUAGE = "contentLanguage";
    static final String CONTENT_DISPOSITION = "contentDisposition";

    public DownloadObjectAttributes(ObjectMetadata metadata) {
        put(ETAG, metadata.getETag());
        put(VERSION_ID, metadata.getVersionId());
        put(CONTENT_MD5, metadata.getContentMD5());
        put(STORAGE_CLASS, metadata.getStorageClass());
        put(CACHE_CONTROL, metadata.getCacheControl());
        put(LAST_MODIFIED, metadata.getLastModified());
        put(EXPIRATION_TIME, metadata.getExpirationTime());
        put(CONTENT_TYPE, metadata.getContentType());
        put(CONTENT_LENGTH, metadata.getContentLength());
        put(CONTENT_ENCODING, metadata.getContentEncoding());
        put(CONTENT_LANGUAGE, metadata.getContentLanguage());
        put(CONTENT_DISPOSITION, metadata.getContentDisposition());
    }
}
