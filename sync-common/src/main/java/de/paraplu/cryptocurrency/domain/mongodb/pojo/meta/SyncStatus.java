package de.paraplu.cryptocurrency.domain.mongodb.pojo.meta;

public enum SyncStatus {
    SCHEDULED, STARTED, SYNCING, ABORTED, STOPPED, FINISHED;

    public static final SyncStatus[] NOT_SYNCING_STATES = new SyncStatus[] { ABORTED, STOPPED, FINISHED };
    public static final SyncStatus[] SYNCING_STATES     = new SyncStatus[] { STARTED, SYNCING };
}
