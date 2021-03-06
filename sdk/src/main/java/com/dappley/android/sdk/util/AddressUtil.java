package com.dappley.android.sdk.util;

import com.dappley.android.sdk.crypto.KeyPairTool;

import org.web3j.crypto.ECKeyPair;

import java.security.KeyPair;

/**
 * Wallet address util.
 */
public class AddressUtil {

    /**
     * Create a new wallet address
     * @return String wallet address
     */
    public static String createAddress() {
        KeyPair keyPair = KeyPairTool.newKeyPair();
        ECKeyPair ecKeyPair = KeyPairTool.castToEcKeyPair(keyPair);
        return AddressUtil.createAddress(ecKeyPair);
    }

    /**
     * Recovery a wallet address from KeyPair
     * @param keyPair Key group
     * @return String wallet address
     */
    public static String createAddress(ECKeyPair keyPair) {
        // generate publicKey hash
        byte[] pubKeyHash = HashUtil.getPubKeyHash(keyPair.getPublicKey());
        // prefix 0x00
        byte[] version = new byte[]{0x00};
        // concat version and pubKeyHash to get a new byte array
        byte[] versionedPayload = ByteUtil.concat(version, pubKeyHash);
        // get the checksum of versionedPayload
        byte[] checksum = HashUtil.getAddressChecksum(versionedPayload, Constant.ADDRESS_CHECKSUM_LENGTH);
        // append the checksum to versionedPayload's tail
        byte[] fullPayload = ByteUtil.concat(versionedPayload, checksum);
        // use Base58 encode method to get the encodes byte array
        byte[] address = Base58.encodeBytes(fullPayload);
        // return encoded String as a wallet address
        return new String(address);
    }
}
