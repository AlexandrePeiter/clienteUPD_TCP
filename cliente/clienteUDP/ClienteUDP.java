package cliente.clienteUDP;

import java.net.*;

import cliente.view.ViewClienteUDP;
import rsa.RSAUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;


public class ClienteUDP {

	private	String	host;
	private int	porta;
	private ViewClienteUDP view;
	DatagramSocket aSocket;
	RecebedorUDP r;

	private PublicKey publicKey;
	private PrivateKey privateKey;

	public ClienteUDP(String host, int porta, ViewClienteUDP viewClienteUDP) {
		this.host = host;
		this.porta = porta;
		this.view = viewClienteUDP;
	}
	public void executa(String nomeCliente) throws Exception {
		//Avisa ao servidor que irá começar a enviar mensagens
		KeyPair keys = RSAUtils.gerarChaves();
		this.publicKey = keys.getPublic();
		this.privateKey = keys.getPrivate();

		String publicKeyStr = RSAUtils.encodeKeyToBase64(publicKey);
		System.out.println(publicKeyStr);

		System.out.println("Chave Privada: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));

		String nome = nomeCliente;
		nomeCliente = "NC: " + nomeCliente + ";" + publicKeyStr;

		aSocket = new DatagramSocket();
		byte[] m = nomeCliente.getBytes();
		InetAddress aHost = InetAddress.getByName(host);

		DatagramPacket request = new DatagramPacket(m, m.length, aHost, porta);
		aSocket.send(request);

		r = new RecebedorUDP(aSocket, view, nome, privateKey);
		new Thread(r).start();
	}

	public static void main(String[]	args) {
		// args give message contents and server hostname
		DatagramSocket aSocket = null;
		try {
			String dados = "olá";
			aSocket = new DatagramSocket();
			byte[] m = dados.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = 6789;
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			System.out.println("Reply: " + new String(reply.getData()));
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	public void send(String Mensagem) {
		//Envia mensagem
		byte[] m = Mensagem.getBytes();
		InetAddress aHost;
		try {
			aHost = InetAddress.getByName(host);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, porta);
			aSocket.send(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendArquivo(String string, File arquivo, PublicKey chaveRecebedor) throws Exception {
		//Envia arquivo
		byte [] mensagemIncial = string.getBytes();

		FileInputStream fileInputStream = new FileInputStream(arquivo);

		Cipher cipher = RSAUtils.getCipherEncryptInstance(chaveRecebedor);

		//Cipher cipherde = RSAUtils.getCipherDecryptInstance(privateKey);


		byte[] buffer = new byte[234];
		int bytesRead;

		InetAddress aHost;
		aHost = InetAddress.getByName(host);

		DatagramPacket pacoteInicial = new DatagramPacket(mensagemIncial, mensagemIncial.length, aHost, porta);
		aSocket.send(pacoteInicial);



		bytesRead = fileInputStream.read(buffer);

		while (bytesRead != -1) {
			int n_pacote_recebido;			
			byte[] encryptedBuffer = cipher.doFinal(buffer);
			DatagramPacket pacote = new DatagramPacket(encryptedBuffer, encryptedBuffer.length, aHost, porta);
			aSocket.send(pacote);
	        long tempoInicial = System.currentTimeMillis();
	        long segundosPassados;
			do {
				n_pacote_recebido = r.getAck();
				long tempoAtual = System.currentTimeMillis();
				segundosPassados = (tempoAtual - tempoInicial) / 1000;

			} while (n_pacote_recebido == -1 && segundosPassados < 2);
			if(n_pacote_recebido != -1)
				bytesRead = fileInputStream.read(buffer);
		}


		byte[] fim = new byte[0];
		DatagramPacket pacoteFim = new DatagramPacket(fim, fim.length, aHost, porta);
		aSocket.send(pacoteFim);
		System.out.println("Enviando fim de arquivo");

		fileInputStream.close();		
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}
}
