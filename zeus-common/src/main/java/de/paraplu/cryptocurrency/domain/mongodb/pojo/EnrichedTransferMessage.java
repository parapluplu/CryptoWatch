package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.data.annotation.Id;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

import de.paraplu.cryptocurrency.domain.TransferMessage;
import de.paraplu.cryptocurrency.util.CryptoConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

@Data
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
    private LocalDateTime     blockDate;
    private double            insecureAmount;

    public EnrichedTransferMessage(
            String txnHash,
            TransferMessage transferMessage,
            TokenInfo tokenInfo,
            Transaction transactionDetails,
            Block block) {
        super();
        this.txnHash = txnHash;
        this.transferMessage = transferMessage;
        this.tokenInfo = tokenInfo;
        this.transactionDetails = transactionDetails;
        this.insecureAmount = CryptoConverter
                .normalize(transferMessage.getAmount(), tokenInfo.getDecimals())
                .doubleValue();
        setBlock(block);
    }

    public void setBlock(Block block) {
        this.block = block;
        this.blockDate = LocalDateTime.ofEpochSecond(block.getTimestamp().intValue(), 0, ZoneOffset.UTC);
    }

}
