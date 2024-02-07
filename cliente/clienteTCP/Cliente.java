package cliente.clienteTCP;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

import cliente.view.ViewClienteTCP;
import rsa.RSAUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Cliente	{
	
	private	String	host;
	private String teste;
	private int	porta;
	PrintStream saida;
	Recebedor r;
	Socket	cliente;
	String nome;
	ViewClienteTCP view;

	PublicKey publicKey;
	PrivateKey privateKey;

	public	Cliente	(String	host,	int	porta, ViewClienteTCP view) {
		this.host	=	host;
		this.porta	=	porta;
		this.view = view;
	}
	public void executa(String nome) throws UnknownHostException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
		//Cria uma conex�o com o servidor
		cliente	=	new	Socket(this.host,	this.porta);
		System.out.println("O cliente se conectou ao servidor!");
		this.nome = nome;

		KeyPair keys = RSAUtils.gerarChaves();
		this.publicKey = keys.getPublic();
		this.privateKey = keys.getPrivate();

		String publicKeyStr = RSAUtils.encodeKeyToBase64(publicKey);
		System.out.println(publicKeyStr);

		r = new Recebedor(cliente.getInputStream(), view, nome, this.privateKey);
		new Thread(r).start();
		saida = new	PrintStream(cliente.getOutputStream());
		saida.println(nome);
		saida.println(publicKeyStr);
	}
	public void send(String str) {
		//Envia uma mensagem para o servidor
		saida.println(str);
	}
	public void sendArquivo(String str, File arquivo, PublicKey publicKeyRecebedor) throws Exception {
		//Envia um arquvio para o servidor
		saida.println(str);
		int i = 0;
		while( i < 1000000 ) {
			i++;
		}

		try(FileInputStream fileInputStream = new FileInputStream(arquivo)) {
			// Envie o arquivo byte a byte
			Cipher cipher = RSAUtils.getCipherEncryptInstance(publicKeyRecebedor);;
			byte[] buffer = new byte[245];
			int bytesRead;
			System.out.println("Começando a enviar");
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				byte[] encryptedBuffer = cipher.doFinal(buffer);
				saida.write(encryptedBuffer, 0, encryptedBuffer.length);
			}
			saida.write(new byte[1], 0, 1);
			fileInputStream.close();
			System.out.println("Terminando de enviar");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
	
	public void fechar() throws IOException {
		//Fecha a conex�o com o servidor
		saida.close();
		cliente.close();
	}
	public void sair() {
		//Avisa ao servidor que vai sair
		saida.println("OUT;" + nome);	
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}
}
