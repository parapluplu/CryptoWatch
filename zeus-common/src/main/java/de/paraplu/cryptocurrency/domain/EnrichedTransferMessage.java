package de.paraplu.cryptocurrency.domain;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;

public class EnrichedTransferMessage {
    private TransferMessage transferMessage;
    private TokenInfo       tokenInfo;

    public EnrichedTransferMessage(TransferMessage transferMessage, TokenInfo tokenInfo) {
        super();
        this.transferMessage = transferMessage;
        this.tokenInfo = tokenInfo;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public TransferMessage getTransferMessage() {
        return transferMessage;
    }
}
