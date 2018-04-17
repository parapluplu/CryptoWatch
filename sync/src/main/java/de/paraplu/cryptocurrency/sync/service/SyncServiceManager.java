package de.paraplu.cryptocurrency.sync.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetCode;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TokenInfoRepository;
import de.paraplu.cryptocurrency.sync.util.Web3Util;

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

    public SyncStatusInfo sync(BigInteger fromBlock, String address) throws SyncServiceException {
        if (fromBlock.compareTo(BigInteger.ONE) == -1) {
            throw new IllegalArgumentException("From block has to be greater equals 0, but is " + fromBlock);
        }
        Optional<SyncStatusInfo> runningSyncJob = syncStatusInfoRepository
                .fromGreaterThanEqualAndStatusNotIn(fromBlock, SyncStatus.NOT_SYNCING_STATES);
        if (runningSyncJob.isPresent()) {
            throw new IllegalArgumentException("There is already a sync job running since block " + fromBlock);
        }
        try {
            EthGetCode code = web3.ethGetCode(address, Web3Util.block(fromBlock)).send();
            if (code == null) {
                throw new SyncServiceException("Address " + address + " cannot be found in the blockchain");
            }
        } catch (IOException e) {
            throw new SyncServiceException("Error while checking for existence of address " + address, e);
        }

        try {
            web3.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            String msg = "No able to retrieve the current synced block number";
            throw new SyncServiceException(msg, e);
        }

        SyncStatusInfo info = SyncStatusInfo
                .builder()
                .contractAdress(address)
                .from(fromBlock)
                .status(SyncStatus.SCHEDULED)
                .build();
        info = syncStatusInfoRepository.insert(info);
        syncService.sync(info);
        return info;
    }

    public void syncAll() throws SyncServiceException {
        List<TokenInfo> tokens = tokenInfoRepository.findAll();
        for (TokenInfo token : tokens) {
            CompletableFuture<SyncStatusInfo> syncProcess = syncService.sync(token);
            syncProcesses.add(syncProcess);
        }
    }

}
