package de.paraplu.cryptocurrency.txnprocessor.triggers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Test;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Numeric;

import de.paraplu.cryptocurrency.domain.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;

public class GasPriceTriggerCheckTest {

    @Test
    public void testCheckLess() {
        GasPriceTriggerCheck tc = new GasPriceTriggerCheck();
        Transaction transactionDetails = new Transaction();
        transactionDetails.setGasPrice(Numeric.encodeQuantity(BigInteger.valueOf(1l)));
        EnrichedTransferMessage message = new EnrichedTransferMessage(null, null, transactionDetails, null);
        Optional<TriggerEvent> check = tc.check(message);
        assertFalse(check.isPresent());
    }

    @Test
    public void testCheckMore() {
        GasPriceTriggerCheck tc = new GasPriceTriggerCheck();
        Transaction transactionDetails = new Transaction();
        transactionDetails.setGasPrice(Numeric.encodeQuantity(BigInteger.valueOf(60000000001l)));
        EnrichedTransferMessage message = new EnrichedTransferMessage(null, null, transactionDetails, null);
        Optional<TriggerEvent> check = tc.check(message);
        assertTrue(check.isPresent());
    }

}
