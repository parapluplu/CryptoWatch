package de.paraplu.cryptocurrency.sync.service;

public class SyncServiceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SyncServiceException() {
        super();
    }

    public SyncServiceException(String message) {
        super(message);
    }

    public SyncServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyncServiceException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SyncServiceException(Throwable cause) {
        super(cause);
    }

}
