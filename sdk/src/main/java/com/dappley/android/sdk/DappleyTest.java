package com.dappley.android.sdk;

import android.content.Context;

import com.dappley.android.sdk.config.Configuration;
import com.dappley.android.sdk.crypto.EcCipher;
import com.dappley.android.sdk.crypto.KeyPairTool;
import com.dappley.android.sdk.protobuf.RpcProto;
import com.dappley.android.sdk.protobuf.RpcServiceGrpc;
import com.dappley.android.sdk.util.AddressUtil;
import com.dappley.android.sdk.util.Base64;
import com.dappley.android.sdk.util.ByteUtil;
import com.dappley.android.sdk.util.HashUtil;

import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DappleyTest {
    public static void main(String[] args) throws Exception {
        KeyPair keyPair = KeyPairTool.newKeyPair();

//        System.out.println(Base64.encode(keyPair.getPrivate().getEncoded()));
//        System.out.println(Base58.encode(keyPair.getPrivate().getEncoded()));
//        System.out.println(Sha3Digest.digest2String(keyPair.getPrivate().getEncoded()));
//        System.out.println(keyPair.getPrivate().getAlgorithm());
//        System.out.println(keyPair.getPrivate().getFormat());
//        System.out.println(keyPair.getPrivate().getEncoded());
//        System.out.println(keyPair.getPrivate().getEncoded().length);

//        System.out.println(keyPair.getPublic());
//        PublicKey publicKey = KeyPairTool.getPublicKey(Base64.encode(keyPair.getPublic().getEncoded()));
//        System.out.println(publicKey);
//        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
//        System.out.println(ecPrivateKey.getS());
//        PrivateKey privateKey = KeyPairTool.getPrivateKey(Base64.encode(keyPair.getPrivate().getEncoded()));
//        System.out.println(Hex.toHexString(keyPair.getPrivate().getEncoded()));
//        System.out.println(keyPair.getPrivate().getEncoded().length);
//        System.out.println(Hex.toHexString(keyPair.getPublic().getEncoded()));
//        System.out.println(privateKey.getFormat());
//        BigInteger bigInteger = new BigInteger("bb23d2ff19f5b16955e8a24dca34dd520980fe3bddca2b3e1b56663f0ec1aa7e",16);
//        byte[] newPubBytes = ECKey.publicKeyFromPrivate(bigInteger, true);
//        PublicKey publicKey2 = KeyPairTool.getPublicKey(Base64.encode(newPubBytes));
//        System.out.println(publicKey2);
//        System.out.println(keyPair.getPublic());

//        BigInteger privateKey = new BigInteger("bb23d2ff19f5b16955e8a24dca34dd520980fe3bddca2b3e1b56663f0ec1aa7e", 16);
//        ECKeyPair ecKeyPair = ECKeyPair.create(privateKey);
//        String address = AddressUtil.createAddress(ecKeyPair);
//        System.out.println(address);

//        testRpcConnect(null);
//        DappleyClient.createWalletAddress();

//        BigInteger privateKey = new BigInteger("bb23d2ff19f5b16955e8a24dca34dd520980fe3bddca2b3e1b56663f0ec1aa7e", 16);
//        ECKeyPair ecKeyPair = ECKeyPair.create(privateKey);
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        String test = "this is original";
        byte[] encoded = EcCipher.encrypt(test.getBytes("UTF-8"), publicKey);
        System.out.println("test:" + test);
        System.out.println("encoded:" + encoded);

    }

    public static String testEncrypt() {
        KeyPair keyPair = KeyPairTool.newKeyPair();
//        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
//        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        try {
//            System.out.println(new BigInteger(keyPair.getPrivate().getEncoded()));
            PublicKey publicKey = KeyPairTool.getPublicKey(Base64.encode(keyPair.getPublic().getEncoded()));
            PrivateKey privateKey = KeyPairTool.getPrivateKey(Base64.encode(keyPair.getPrivate().getEncoded()));
            String test = "this is data";
            byte[] encoded = new byte[0];
            encoded = EcCipher.encrypt(test.getBytes("UTF-8"), publicKey);
            String result = "original:" + test;
            byte[] decoded = EcCipher.decrypt(encoded, privateKey);
            result += "\ndecoded:" + new String(decoded, "UTF-8");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void testRpcConnect(Context context) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(Configuration.getInstance(context).getServerIp(), Configuration.getInstance(context).getServerPort())
                .usePlaintext()
                .build();
        RpcServiceGrpc.RpcServiceBlockingStub stub = RpcServiceGrpc.newBlockingStub(channel);
        RpcProto.GetBalanceRequest request = RpcProto.GetBalanceRequest.newBuilder()
                .setAddress("1BpXBb3uunLa9PL8MmkMtKNd3jzb5DHFkG")
                .build();
        RpcProto.GetBalanceResponse response = stub.rpcGetBalance(request);
        String message = response.getMessage();
        System.out.println(message);
    }
}
