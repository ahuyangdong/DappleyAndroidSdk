package com.dappley.android.sdk.chain;

import com.dappley.android.sdk.crypto.ShaDigest;
import com.dappley.android.sdk.protobuf.RpcProto;
import com.dappley.android.sdk.protobuf.TransactionProto;
import com.dappley.android.sdk.util.Base58;
import com.dappley.android.sdk.util.ByteUtil;
import com.google.protobuf.ByteString;

import java.util.List;

/**
 * Utils to handle transaction datas.
 */
public class TransactionManager {

    public static TransactionProto.Transaction newTransaction(String address, int amount) {
        List<RpcProto.UTXO> spendableList = UtxoManager.getSpendableUtxos(address, amount);
        return newTransaction(spendableList, amount);
    }

    public static TransactionProto.Transaction newTransaction(List<RpcProto.UTXO> utxos, int amount) {
        TransactionProto.Transaction.Builder builder = TransactionProto.Transaction.newBuilder();
        // TODO generate Id

        // add vin list and return the total amount of vin values
        int totalAmount = buildVin(builder, utxos);

        // add vout list. If there is change is this transaction, vout list wound have two elements, or have just one to coin receiver.
        buildVout(builder, amount, totalAmount);

        // TODO calculate tip count
        builder.setTip(0);
        return builder.build();
    }

    private static int buildVin(TransactionProto.Transaction.Builder builder, List<RpcProto.UTXO> utxos) {
        TransactionProto.TXInput.Builder txInputBuilder = null;
        // save total amount value of all txInput value
        int totalAmount = 0;
        for (RpcProto.UTXO utxo : utxos) {
            if (utxo == null) {
                continue;
            }
            txInputBuilder = TransactionProto.TXInput.newBuilder();
            txInputBuilder.setTxid(utxo.getTxid());
            txInputBuilder.setVout(utxo.getTxIndex());
            // TODO fill with 'from' pubKeyHash

            totalAmount += utxo.getAmount();
            builder.addVin(txInputBuilder.build());
        }
        return totalAmount;
    }

    private static void buildVout(TransactionProto.Transaction.Builder builder, int amount, int totalAmount) {
        if (totalAmount < amount) {
            return;
        }
        TransactionProto.TXOutput.Builder txOutputBuilder = TransactionProto.TXOutput.newBuilder();
        // TODO fill with 'to' pubKeyHash and value

        builder.addVout(txOutputBuilder.build());

        // if totalAmout is greater than amount, we need to add change value after.
        txOutputBuilder = TransactionProto.TXOutput.newBuilder();
        // TODO fill with 'from' pubKeyHash and change value

        builder.addVout(txOutputBuilder.build());
    }

    /**
     * Generate ID of Transaction object
     * <p>The ID is a ByteString object</p>
     * @param transactionBuilder
     * @return ByteString ID bytes
     */
    public static ByteString newId(TransactionProto.Transaction.Builder transactionBuilder) {
        transactionBuilder.setID(ByteString.copyFrom(new byte[]{}));
        TransactionProto.Transaction transaction = transactionBuilder.build();
        byte[] txBytes = transaction.toByteArray();
        byte[] sha256 = ShaDigest.sha256(txBytes);
        return ByteString.copyFrom(sha256);
    }

    public static ByteString getTransactionPubKeyHash(String address) {
        byte[] addrBytes = Base58.decode(address);
        byte[] pubKeyHash = ByteUtil.slice(addrBytes, 1, addrBytes.length - 4);
        return ByteString.copyFrom(pubKeyHash);
    }
}
