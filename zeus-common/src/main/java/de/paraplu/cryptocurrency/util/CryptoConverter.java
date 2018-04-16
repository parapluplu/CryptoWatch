package de.paraplu.cryptocurrency.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CryptoConverter {
    public static class EthConverter {
        public static BigDecimal weiToGwei(BigInteger wei) {
            return normalize(wei, 9);
        }
    }

    public static BigDecimal normalize(BigInteger amount, BigInteger decimals) {
        return normalize(amount, decimals.intValue());
    }

    public static BigDecimal normalize(BigInteger amount, int decimals) {
        return new BigDecimal(amount).divide(normalizer(decimals));
    }

    public static BigDecimal normalizer(int decimals) {
        return BigDecimal.TEN.pow(decimals);
    }
}
