package de.paraplu.cryptocurrency.txnprocessor.triggers;

import org.web3j.protocol.core.methods.response.EthBlock.Block;

public abstract class TriggerCheckTest {

    protected final static Block DEFAULT_BLOCK;

    static {
        DEFAULT_BLOCK = new Block();
        DEFAULT_BLOCK.setTimestamp("0x0");
    }

}
