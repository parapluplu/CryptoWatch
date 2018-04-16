package de.paraplu.cryptocurrency.domain;

import java.io.Serializable;
import java.math.BigInteger;

import de.paraplu.cryptocurrency.domain.neo4j.pojo.Transfer;

public class TransferMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            transaction;
    private String            from;
    private String            to;

    private String            tokenAddress;

    private BigInteger        block;
    private BigInteger        amount;

    TransferMessage() {

    }

    public TransferMessage(Transfer transfer) {
        transaction = transfer.getTransaction();
        from = transfer.getFrom().getAddress();
        to = transfer.getTo().getAddress();
        tokenAddress = transfer.getTokenAddress();
        block = transfer.getBlock();
        amount = transfer.getAmount();
    }

    public BigInteger getAmount() {
        return amount;
    }

    public BigInteger getBlock() {
        return block;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public void setBlock(BigInteger block) {
        this.block = block;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

}
