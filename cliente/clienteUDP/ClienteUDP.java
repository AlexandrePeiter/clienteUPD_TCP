package cliente.clienteUDP;

import java.net.*;

import cliente.view.ViewClienteUDP;

import java.io.*;

public class ClienteUDP {
	
	private	String	host;
	private int	porta;
	private ViewClienteUDP view;
	DatagramSocket aSocket;
	RecebedorUDP r;
	public ClienteUDP(String host, int porta, ViewClienteUDP viewClienteUDP) {
		this.host = host;
		this.porta = porta;
		this.view = viewClienteUDP;
	}
	public void executa(String nomeCliente) throws Exception {
		//Avisa ao servidor que irá começar a eniviar mensagens
		String nome = nomeCliente;
		nomeCliente = "NC: " + nomeCliente;
		
		aSocket = new DatagramSocket();
		byte[] m = nomeCliente.getBytes();
		InetAddress aHost = InetAddress.getByName(host);
		
		DatagramPacket request = new DatagramPacket(m, m.length, aHost, porta);
		aSocket.send(request);
		
		r = new RecebedorUDP(aSocket, view, nome);
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

	public void sendArquivo(String string, File arquivo) throws Exception {
		//Envia arquivo
		byte [] mensagemIncial = string.getBytes();
		
        FileInputStream fileInputStream = new FileInputStream(arquivo);

		byte[] buffer = new byte[1024];
        int bytesRead;

        InetAddress aHost;
        aHost = InetAddress.getByName(host);
        
        DatagramPacket pacoteInicial = new DatagramPacket(mensagemIncial, mensagemIncial.length, aHost, porta);
        aSocket.send(pacoteInicial);

		while ((bytesRead = fileInputStream.read(buffer)) != -1) {
			int n_pacote_recebido;

			DatagramPacket pacote = new DatagramPacket(buffer, bytesRead, aHost, porta);

			aSocket.send(pacote);

			do {
				n_pacote_recebido = r.getAck();
			} while (n_pacote_recebido == -1);
		}
        byte[] fim = new byte[0];
        DatagramPacket pacoteFim = new DatagramPacket(fim, fim.length, aHost, porta);
        aSocket.send(pacoteFim);
        System.out.println("Enviando fim de arquivo");
        fileInputStream.close();		
	}
}
