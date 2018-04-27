package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

import de.paraplu.cryptocurrency.domain.TransferMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

@Data
@AllArgsConstructor
public class EnrichedTransferMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Setter(AccessLevel.NONE)
    @NonNull
    private String            txnHash;
    private TransferMessage   transferMessage;
    private TokenInfo         tokenInfo;
    private Transaction       transactionDetails;
    private Block             block;

}
