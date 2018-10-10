package com.dappley.android.sdk.chain;

import com.dappley.android.sdk.protobuf.RpcProto;
import com.dappley.android.sdk.protobuf.TransactionProto;
import com.google.protobuf.ByteString;

import java.util.List;

/**
 * Utils to handle transaction datas.
 */
public class TransactionManager {

    public static TransactionProto.Transaction.Builder newTransactionBuilder(String address, int amount){
        List<RpcProto.UTXO> spendableList = UtxoManager.getSpendableUtxos(address, amount);
        return newTransactionBuilder(address, amount);
    }

    public static TransactionProto.Transaction.Builder newTransactionBuilder(List<RpcProto.UTXO> utxos, int amount) {
        TransactionProto.Transaction.Builder builder = TransactionProto.Transaction.newBuilder();
        // TODO generate Id

        // add vin list and return the total amount of vin values
        int totalAmount = buildVin(builder, utxos);

        // add vout list. If there is change is this transaction, vout list wound have two elements, or have just one to coin receiver.
        buildVout(builder, amount, totalAmount);

        // TODO calculate tip count
        builder.setTip(0);
        return builder;
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
}
