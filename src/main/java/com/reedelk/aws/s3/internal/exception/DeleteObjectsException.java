package com.reedelk.aws.s3.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class DeleteObjectsException extends PlatformException {

    public DeleteObjectsException(String message) {
        super(message);
    }

    public DeleteObjectsException(String message, Throwable exception) {
        super(message, exception);
    }
}
