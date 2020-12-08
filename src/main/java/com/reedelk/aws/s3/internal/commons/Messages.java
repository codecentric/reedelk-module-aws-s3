package com.reedelk.aws.s3.internal.commons;

import com.reedelk.runtime.api.commons.FormattedMessage;

public class Messages {

    private Messages() {
    }

    public enum UploadObject implements FormattedMessage {

        BUCKET_NAME_EMPTY("The bucket name is empty. The bucket name must not be empty (DynamicValue=[%s])."),
        KEY_EMPTY("The key is empty. The key must not be empty (DynamicValue=[%s]).");

        private final String message;

        UploadObject(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum CopyObject implements FormattedMessage {

        COPY_ERROR("An error occurred while copying object from source bucket=[%s], key=[%s] to destination bucket=[%s], key=[%s], cause=[%s]"),
        SOURCE_BUCKET_EMPTY("The source bucket name is empty. The source bucket name must not be empty (DynamicValue=[%s])."),
        DESTINATION_BUCKET_EMPTY("The destination bucket name is empty. The destination bucket name must not be empty (DynamicValue=[%s])."),
        SOURCE_KEY_EMPTY("The source key is empty. The source key must not be empty (DynamicValue=[%s])."),
        DESTINATION_KEY_EMPTY("The destination key is empty. The destination key must not be empty (DynamicValue=[%s]).");

        private final String message;

        CopyObject(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum DeleteObject implements FormattedMessage {

        DELETE_ERROR("An error occurred while deleting object from bucket=[%s], with key=[%s], cause=[%s]."),
        BUCKET_NAME_EMPTY("The bucket name is empty. The bucket name must not be empty (DynamicValue=[%s])."),
        KEY_EMPTY("The key is empty. The key must not be empty (DynamicValue=[%s]).");

        private final String message;

        DeleteObject(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }

    }

    public enum DeleteObjects implements FormattedMessage {

        DELETE_ERROR("An error occurred while deleting objects from bucket=[%s], with keys=[%s], cause=[%s]."),
        BUCKET_NAME_EMPTY("The bucket name is empty. The bucket name must not be empty (DynamicValue=[%s])."),
        KEYS_EMPTY("The keys are empty. The keys must not be empty (DynamicValue=[%s]).");

        private final String message;

        DeleteObjects(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }

    }

    public enum DownloadObject implements FormattedMessage {

        DOWNLOAD_ERROR("An error occurred while downloading object from bucket=[%s], key=[%s], cause=[%s]."),
        BUCKET_NAME_EMPTY("The bucket name is empty. The bucket name must not be empty (DynamicValue=[%s])."),
        KEY_EMPTY("The key is empty. The key must not be empty (DynamicValue=[%s]).");

        private final String message;

        DownloadObject(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum ListObjects implements FormattedMessage {

        LIST_ERROR("An error occurred while listing objects from bucket=[%s], cause=[%s]."),
        BUCKET_NAME_EMPTY("The bucket name is empty. The bucket name must not be empty (DynamicValue=[%s]).");

        private final String message;

        ListObjects(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }
}
