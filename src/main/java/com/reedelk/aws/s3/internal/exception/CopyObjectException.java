package com.reedelk.aws.s3.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class CopyObjectException extends PlatformException {

    public CopyObjectException(String message) {
        super(message);
    }

    public CopyObjectException(String message, Throwable exception) {
        super(message, exception);
    }
}
