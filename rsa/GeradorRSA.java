package rsa;

import java.security.*;

public class GeradorRSA {

    public static KeyPair gerarChaves() throws NoSuchAlgorithmException{
        KeyPairGenerator geradorChaves = KeyPairGenerator.getInstance("RSA");
        geradorChaves.initialize(2048);
        return geradorChaves.generateKeyPair();
    }

}
