package de.paraplu.cryptocurrency.sync.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TokenInfoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SyncServiceManager {

    @Value("${max-retries:100}")
    private int                                     maxRetries;

    @Autowired
    private Web3j                                   web3;

    @Autowired
    private SyncStatusInfoRepository                syncStatusInfoRepository;

    @Autowired
    private TokenInfoRepository                     tokenInfoRepository;

    @Autowired
    private SyncService                             syncService;

    private List<CompletableFuture<SyncStatusInfo>> syncProcesses = new ArrayList<>();

    public CompletableFuture<SyncStatusInfo> executeActionAsync(TokenInfo token) {
        CompletableFuture<SyncStatusInfo> f = syncService.sync(token);
        for (int i = 0; i < maxRetries; i++) {
            final int currentRetry = i;
            f = f
                    .thenApply(CompletableFuture::completedFuture)
                    .handle((CompletableFuture<SyncStatusInfo> r, Throwable t) -> {
                        SyncStatusInfo result = null;
                        try {
                            result = r.get();
                        } catch (InterruptedException | ExecutionException e) {
                            log.warn("Error while retrieving result from completed future", e);
                        }
                        if (t != null) {
                            log.warn("Thread exited abnormally, retry " + currentRetry, t);
                        } else if (result != null) {
                            log.warn("Syncing actually finished - This is supposed to be a never ending process!");
                        }
                        return syncService.sync(token);
                    })
                    .thenCompose(Function.identity());
        }
        return f;
    }

    public void stop() {
        CompletableFuture.allOf(syncProcesses.toArray(new CompletableFuture[] {})).cancel(true);
    }

    public void syncAll() {
        List<TokenInfo> tokens = tokenInfoRepository.findAll();
        System.out.println("asfasfasgfagfaslkgjasgklasgj");
        System.out.println(tokens);
        for (TokenInfo token : tokens) {
            CompletableFuture<SyncStatusInfo> syncProcess = executeActionAsync(token);
            syncProcesses.add(syncProcess);
        }
    }

}
