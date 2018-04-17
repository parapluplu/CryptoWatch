package de.paraplu.cryptocurrency.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import de.paraplu.cryptocurrency.sync.service.SyncServiceException;
import de.paraplu.cryptocurrency.sync.service.SyncServiceManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class SyncLifecycle implements SmartLifecycle {

    @Autowired
    private SyncServiceManager syncServiceManager;
    private boolean            isRunning;

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void start() {
        log.info("Starting sync all");
        try {
            syncServiceManager.syncAll();
            isRunning = true;
        } catch (SyncServiceException e) {
            log.error("Error while syncing", e);
            isRunning = false;
        }
        log.info("Started sync all");
    }

    @Override
    public void stop() {
        syncServiceManager.stop();
        isRunning = false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

}
