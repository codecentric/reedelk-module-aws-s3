package com.reedelk.aws.s3.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class UploadObjectException extends PlatformException {

    public UploadObjectException(String message) {
        super(message);
    }

    public UploadObjectException(String message, Throwable exception) {
        super(message, exception);
    }
}
