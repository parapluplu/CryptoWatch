package de.paraplu.cryptocurrency.sync.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TokenInfoRepository;

@Component
public class SyncServiceManager {

    @Autowired
    private Web3j                                   web3;

    @Autowired
    private SyncStatusInfoRepository                syncStatusInfoRepository;

    @Autowired
    private TokenInfoRepository                     tokenInfoRepository;

    @Autowired
    private SyncService                             syncService;

    private List<CompletableFuture<SyncStatusInfo>> syncProcesses = new ArrayList<>();

    public void stop() {
        CompletableFuture.allOf(syncProcesses.toArray(new CompletableFuture[] {})).cancel(true);
    }

    public void syncAll() throws SyncServiceException {
        List<TokenInfo> tokens = tokenInfoRepository.findAll();
        for (TokenInfo token : tokens) {
            CompletableFuture<SyncStatusInfo> syncProcess = syncService.sync(token);
            syncProcesses.add(syncProcess);
        }
    }

}
