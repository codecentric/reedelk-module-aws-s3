package com.reedelk.aws.s3.internal.type;

import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;
import java.util.List;

@Type(listItemType = DeletedObject.class)
public class ListOfDeletedObjects extends ArrayList<DeletedObject> {

    public ListOfDeletedObjects(List<DeleteObjectsResult.DeletedObject> deletedObjects) {
        if (deletedObjects != null) {
            deletedObjects.stream().map(DeletedObject::new).forEach(this::add);
        }
    }
}
