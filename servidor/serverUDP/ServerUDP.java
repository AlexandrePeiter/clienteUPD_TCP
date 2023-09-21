package servidor.serverUDP;

import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;


public class ServerUDP {
	
	private int	porta;
	private	HashMap<String, InetAddress> ipClientes;
	private	HashMap<String, Integer> portaClientes;
	private List<String> nomeCliente;
	DatagramSocket aSocket;
	public static void main(String[]	args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

		String horaSistema = LocalDateTime.now().format(formatter);
        System.out.println("Hora do sistema (LocalDateTime): " + horaSistema);

		new ServerUDP(6789).executa();
	}
	
	public ServerUDP (int porta) {
		this.porta = porta;
		this.ipClientes = new	HashMap<>();
		this.portaClientes = new	HashMap<>();
		this.nomeCliente = new ArrayList<>();
	}
	
	public void executa() {
		aSocket = null;
		try {
			aSocket = new DatagramSocket(porta);
			byte[] buffer = new byte[1024];
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				System.out.println(aSocket.getReceiveBufferSize());
				aSocket.receive(request);
				String mensagem = new String(request.getData(), 0, request.getLength());
				System.out.println("recebido " + mensagem);
				if(mensagem.startsWith("NC: ")) {
					trataNovoCliente(request, mensagem);
				} else if (mensagem.startsWith("MG: ")) {
					trataNovaMensagem(request, mensagem);
				} else if(mensagem.startsWith("FL: ")) {
					trataArquivo(request, mensagem);
				}
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}

	private void trataArquivo(DatagramPacket request, String mensagem) throws IOException {
		mensagem = mensagem.substring(4);
		String dados[] = mensagem.split(";", 3);
		InetAddress ipCliete = ipClientes.get(dados[0]);
		int portaCliente = portaClientes.get(dados[0]);
		
		String response = "FL: " + dados[1] + ";"+ dados[2];
		byte [] m = response.getBytes();
		DatagramPacket responsePacket = new DatagramPacket(m, m.length, ipCliete, portaCliente);
		aSocket.send(responsePacket);
		byte[] buffer = new byte[1024];
		
		while (true) {
            DatagramPacket chegou = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(chegou);
            
            if (chegou.getLength() == 0) {
            	byte[] fim = new byte[0];
                DatagramPacket pacoteFim = new DatagramPacket(fim, fim.length, ipCliete, portaCliente);
                aSocket.send(pacoteFim);
                System.out.println("Servidor recebeu fim de arquivo");
                break; // Fim do arquivo
            }
            DatagramPacket enviar = new DatagramPacket(chegou.getData(), chegou.getLength(), ipCliete, portaCliente);
            
            aSocket.send(enviar);
        }
	}

	private void trataNovaMensagem(DatagramPacket request, String mensagem) throws IOException {
		mensagem = mensagem.substring(4);
		String dados[] = mensagem.split(";", 2);
		InetAddress ipCliete = ipClientes.get(dados[0]);
		int portaCliente = portaClientes.get(dados[0]);
		
		byte[] enviarMensagem =  dados[1].getBytes();

		DatagramPacket sendData = new DatagramPacket
									 (enviarMensagem, enviarMensagem.length, ipCliete, portaCliente);
		aSocket.send(sendData);
	}

	private void trataNovoCliente(DatagramPacket request, String mensagem) throws IOException {
		mensagem = mensagem.substring(4);
		String nome = mensagem;
		
		InetAddress ipSender = request.getAddress();
		int portaSender = request.getPort();
		
		ipClientes.put(nome, ipSender);
		portaClientes.put(nome, portaSender);
		String enviar = "NC: " + nome;
		byte[] novoCliente =  enviar.getBytes();
		DatagramPacket novoClienteReplay = new DatagramPacket(novoCliente, novoCliente.length);
		
		for (String nomeClien : nomeCliente) {
			byte [] clienteAntigo = ("NC: " + nomeClien).getBytes();
			
			InetAddress ipCliete = ipClientes.get(nomeClien);
			int portaCliente = portaClientes.get(nomeClien);
			DatagramPacket atualizaNovo = new DatagramPacket
										 (clienteAntigo, clienteAntigo.length, ipSender, portaSender);
			
			novoClienteReplay.setAddress(ipCliete);
			novoClienteReplay.setPort(portaCliente);
			System.out.println("Enviando " + nomeClien + " para " + nome );
			aSocket.send(novoClienteReplay);
			aSocket.send(atualizaNovo);
		}
		nomeCliente.add(nome);		
	}
	
	
}
