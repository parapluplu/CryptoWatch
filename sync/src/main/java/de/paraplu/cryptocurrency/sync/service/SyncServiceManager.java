package de.paraplu.cryptocurrency.sync.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetCode;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.sync.util.Web3Util;

@Component
public class SyncServiceManager {

    @Autowired
    private Web3j                    web3;

    @Autowired
    private SyncStatusInfoRepository syncStatusInfoRepository;

    @Autowired
    private SyncService              syncService;

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

}
