package cliente.clienteUDP;

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

import rsa.RSAUtils;

public class Teste {
	
	  private static String bytesToHex(byte[] bytes) {
	        StringBuilder result = new StringBuilder();
	        for (byte b : bytes) {
	            result.append(String.format("%02X ", b));
	        }
	        return result.toString();
	    }
	
	public static void main(String[] args) throws Exception {
		PublicKey publicKey;
		PrivateKey privateKey;
		
		KeyPair keys = RSAUtils.gerarChaves();
		publicKey = keys.getPublic();
		privateKey = keys.getPrivate();
		
		FileInputStream fileInputStream = new FileInputStream("teste1.txt");
	    
		Cipher cipher = RSAUtils.getCipherEncryptInstance(publicKey);
		
		Cipher cipherde = RSAUtils.getCipherDecryptInstance(privateKey);
		
		String publicKeyStr = RSAUtils.encodeKeyToBase64(publicKey);
		System.out.println(publicKeyStr);
		System.out.println("Chave Privada: \n" + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
		
		byte[] buffer = new byte[234];
        int bytesRead;
        bytesRead = fileInputStream.read(buffer);
        
        /*
        String msgCompleta = new String(buffer);
        
        String msgEncriptada = RSAUtils.encryptToString(msgCompleta, publicKey);

        System.out.println(msgEncriptada);
        */
        
      /*
        
        byte[] encryptedBuffer = cipher.doFinal(buffer, 0, bytesRead);
        String base = Base64.getEncoder().encodeToString(encryptedBuffer);
        
        System.out.println("\n"+ base + "\n");
        System.out.println("O que foi decri");
        
        byte[] encryptedBytes = Base64.getDecoder().decode(base);
        byte[] decryptedBytes = cipherde.doFinal(encryptedBytes);
        String saida = new String(decryptedBytes);
		
		System.out.println(saida);
		System.out.println("-----------------");*/
        
        byte[] encryptedData = cipher.doFinal(buffer);

        byte[] decryptedData = cipherde.doFinal(encryptedData);

        // Exibir dados descriptografados (opcional)
        System.out.println("Dados descriptografados: " + new String(decryptedData));
    }
		
}
	
	

