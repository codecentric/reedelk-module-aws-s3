package com.reedelk.aws.s3.internal.type;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;
import java.util.List;

@Type(listItemType = SummaryObject.class)
public class ListOfSummaryObject extends ArrayList<SummaryObject> {

    public ListOfSummaryObject(List<S3ObjectSummary> objectSummaryList) {
        if (objectSummaryList != null) {
            objectSummaryList.stream().map(SummaryObject::new).forEach(this::add);
        }
    }
}
