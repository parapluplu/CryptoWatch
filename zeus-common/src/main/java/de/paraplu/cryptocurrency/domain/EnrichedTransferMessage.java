package de.paraplu.cryptocurrency.domain;

import java.io.Serializable;

import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnrichedTransferMessage implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private TransferMessage   transferMessage;
    private TokenInfo         tokenInfo;
    private Transaction       transactionDetails;
    private Block             block;

}
