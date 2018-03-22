package de.paraplu.cryptocurrency.sync;

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
import de.paraplu.cryptocurrency.util.Web3Util;

@Component
public class SyncServiceManager {

    @Autowired
    private Web3j                    web3;

    @Autowired
    private SyncStatusInfoRepository syncStatusInfoRepository;

    @Autowired
    private SyncService              syncService;

    public SyncStatusInfo sync(BigInteger fromBlock, BigInteger toBlock, String address) throws SyncServiceException {
        if (fromBlock.compareTo(BigInteger.ONE) == -1) {
            throw new IllegalArgumentException("From block has to be greater equals 0, but is " + fromBlock);
        } else if (toBlock.compareTo(BigInteger.ONE) == -1) {
            throw new IllegalArgumentException(
                    "To block has to be -1 for infinte sync or minimum 1, but is " + toBlock);
        } else if (toBlock.compareTo(fromBlock) == -1) {
            throw new IllegalArgumentException(
                    "To block has to -1 for infinite sync or greater than the from block, from block is " + fromBlock
                            + " and to block is " + toBlock);
        }
        Optional<SyncStatusInfo> runningSyncJob = syncStatusInfoRepository
                .fromGreaterThanEqualAndToLessThanEqualAndStatusNotIn(
                        fromBlock,
                        toBlock,
                        SyncStatus.NOT_SYNCING_STATES);
        if (runningSyncJob.isPresent()) {
            throw new IllegalArgumentException(
                    "There is already a sync job running in the range of " + fromBlock + " - " + toBlock);
        }
        try {
            EthGetCode code = web3.ethGetCode(address, Web3Util.block(fromBlock)).send();
            if (code == null) {
                throw new SyncServiceException("Address " + address + " cannot be found in the blockchain");
            }
        } catch (IOException e) {
            throw new SyncServiceException("Error while checking for existing of address " + address, e);
        }
        SyncStatusInfo info = new SyncStatusInfo();
        info.setContractAdresses(address);
        info.setFrom(fromBlock);
        if (!toBlock.equals(BigInteger.valueOf(-1))) {
            info.setTo(toBlock);
        }
        info.setStatus(SyncStatus.SCHEDULED);
        info = syncStatusInfoRepository.insert(info);
        syncService.sync(info);
        return info;
    }

}
