package de.paraplu.cryptocurrency.domain.neo4j.pojo;

import java.math.BigInteger;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.annotation.Id;

@RelationshipEntity(type = "Transfer")
public class Transfer {
    @Id
    private String     transaction;

    @StartNode
    private Address    from;

    @EndNode
    private Address    to;

    @Property
    private String     tokenAddress;

    @Property
    private BigInteger block;

    @Property
    private BigInteger amount;

    public Transfer() {
    }

    public Transfer(
            Address from,
            Address to,
            String tokenAddress,
            BigInteger amount,
            String transaction,
            BigInteger block) {
        this.from = from;
        this.to = to;
        this.tokenAddress = tokenAddress;
        this.amount = amount;
        this.transaction = transaction;
        this.block = block;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public BigInteger getBlock() {
        return block;
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
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

    public void setFrom(Address from) {
        this.from = from;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

}
