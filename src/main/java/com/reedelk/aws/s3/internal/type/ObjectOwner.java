package com.reedelk.aws.s3.internal.type;

import com.amazonaws.services.s3.model.Owner;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;

import java.io.Serializable;
import java.util.HashMap;

@Type
@TypeProperty(name = ObjectOwner.DISPLAY_NAME, type = String.class)
@TypeProperty(name = ObjectOwner.ID, type = String.class)
public class ObjectOwner extends HashMap<String, Serializable> {

    static final String DISPLAY_NAME = "displayName";
    static final String ID = "id";

    public ObjectOwner(Owner owner) {
        put(DISPLAY_NAME, owner.getDisplayName());
        put(ID, owner.getId());
    }
}
