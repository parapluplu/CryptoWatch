package de.paraplu.cryptocurrency.util;

import java.math.BigInteger;

import org.web3j.protocol.core.DefaultBlockParameter;

public class Web3Util {

    public static DefaultBlockParameter block(BigInteger from) {
        return DefaultBlockParameter.valueOf(from);
    }
}
