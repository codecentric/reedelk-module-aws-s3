package com.reedelk.aws.s3.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class ListObjectsException extends PlatformException {

    public ListObjectsException(String message) {
        super(message);
    }

    public ListObjectsException(String message, Throwable exception) {
        super(message, exception);
    }
}
