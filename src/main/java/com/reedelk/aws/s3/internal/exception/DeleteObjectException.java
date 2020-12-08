package com.reedelk.aws.s3.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class DeleteObjectException extends PlatformException {

    public DeleteObjectException(String message, Throwable exception) {
        super(message, exception);
    }
}
