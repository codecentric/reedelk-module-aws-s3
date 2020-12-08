package com.reedelk.aws.s3.internal.type;

import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;

import java.io.Serializable;
import java.util.HashMap;

@Type(mapKeyType = String.class, mapValueType = Serializable.class)
@TypeProperty(name = DeletedObject.KEY, type = String.class)
@TypeProperty(name = DeletedObject.MARKER_VERSION_ID, type = String.class)
@TypeProperty(name = DeletedObject.VERSION_ID, type = String.class)
public class DeletedObject extends HashMap<String, Serializable> {

    static final String KEY = "key";
    static final String MARKER_VERSION_ID = "markerVersionId";
    static final String VERSION_ID = "versionId";

    public DeletedObject(DeleteObjectsResult.DeletedObject deletedObject) {
        put(KEY, deletedObject.getKey());
        put(MARKER_VERSION_ID, deletedObject.getDeleteMarkerVersionId());
        put(VERSION_ID, deletedObject.getVersionId());
    }
}
