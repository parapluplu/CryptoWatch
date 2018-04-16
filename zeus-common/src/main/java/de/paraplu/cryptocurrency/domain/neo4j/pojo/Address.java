package de.paraplu.cryptocurrency.domain.neo4j.pojo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Id;

@NodeEntity
public class Address {
    @Id
    private String         address;

    @Relationship(type = "Transfer", direction = Relationship.OUTGOING)
    private List<Transfer> transfers;

    public Address() {
    }

    public Address(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    public Transfer transfer(Address to, String tokenAddress, BigInteger amount, String transaction, BigInteger block) {
        Transfer transfer = new Transfer(this, to, tokenAddress, amount, transaction, block);
        if (transfers == null) {
            transfers = new ArrayList<>();
        }
        this.transfers.add(transfer);
        return transfer;
    }
}
