package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;

public class SyncStatusInfo {

    @Id
    private String     id;
    private String     contractAdress;
    private BigInteger currentBlock;
    private BigInteger from;
    private BigInteger to;
    private SyncStatus status;
    private long       durationInMilliseconds;

    public String getContractAdress() {
        return contractAdress;
    }

    public BigInteger getCurrentBlock() {
        return currentBlock;
    }

    public long getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public BigInteger getFrom() {
        return from;
    }

    public String getId() {
        return id;
    }

    public SyncStatus getStatus() {
        return status;
    }

    public BigInteger getTo() {
        return to;
    }

    public void setContractAdresses(String contractAdress) {
        this.contractAdress = contractAdress;
    }

    public void setCurrentBlock(BigInteger currentBlock) {
        this.currentBlock = currentBlock;
    }

    public void setDurationInMilliseconds(long durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
    }

    public void setFrom(BigInteger from) {
        this.from = from;
    }

    public void setStatus(SyncStatus status) {
        this.status = status;
    }

    public void setTo(BigInteger to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "SyncStatusInfo [id=" + id + ", contractAdress=" + contractAdress + ", currentBlock=" + currentBlock
                + ", from=" + from + ", to=" + to + ", status=" + status + ", durationInMilliseconds="
                + durationInMilliseconds + "]";
    }

}
