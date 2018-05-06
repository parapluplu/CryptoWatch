package de.paraplu.cryptocurrency.txnprocessor.triggers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Optional;

import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Numeric;

import de.paraplu.cryptocurrency.domain.TransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Transfer;

public class GasPriceTriggerCheckTest extends TriggerCheckTest {

    // @Test
    public void testCheckLess() {
        GasPriceTriggerCheck tc = new GasPriceTriggerCheck();
        tc.setMinValue(BigInteger.valueOf(2));
        Transaction transactionDetails = new Transaction();
        transactionDetails.setGasPrice(Numeric.encodeQuantity(BigInteger.valueOf(1l)));
        TransferMessage transferMessage = new TransferMessage(new Transfer());
        transferMessage.setAmount(BigInteger.TEN);
        EnrichedTransferMessage message = new EnrichedTransferMessage(
                "",
                transferMessage,
                null,
                transactionDetails,
                DEFAULT_BLOCK);
        Optional<TriggerEvent> check = tc.check(message);
        assertFalse(check.isPresent());
    }

    // @Test
    public void testCheckMore() {
        GasPriceTriggerCheck tc = new GasPriceTriggerCheck();
        tc.setMinValue(BigInteger.valueOf(60000000000l));
        Transaction transactionDetails = new Transaction();
        transactionDetails.setGasPrice(Numeric.encodeQuantity(BigInteger.valueOf(60000000001l)));
        TransferMessage transferMessage = new TransferMessage(new Transfer());
        transferMessage.setAmount(BigInteger.TEN);
        EnrichedTransferMessage message = new EnrichedTransferMessage(
                "",
                transferMessage,
                new TokenInfo("", "", BigInteger.TEN),
                transactionDetails,
                DEFAULT_BLOCK);
        Optional<TriggerEvent> check = tc.check(message);
        assertTrue(check.isPresent());
    }

}
