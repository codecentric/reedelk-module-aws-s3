package com.reedelk.aws.s3.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class DownloadObjectException extends PlatformException {

    public DownloadObjectException(String message) {
        super(message);
    }

    public DownloadObjectException(String message, Throwable exception) {
        super(message, exception);
    }
}
