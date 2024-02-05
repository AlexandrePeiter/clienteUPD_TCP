package servidor.serverUDP;

import java.net.*;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;

public class ServerUDP {

	private int porta;
	private HashMap<String, InetAddress> ipClientes;
	private HashMap<String, Integer> portaClientes;
	private HashMap<String, String> chavesClientes;
	private List<String> nomeCliente;
	DatagramSocket aSocket;

	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

		String horaSistema = LocalDateTime.now().format(formatter);
		System.out.println("Hora do sistema (LocalDateTime): " + horaSistema);

		new ServerUDP(6789).executa();
	}

	public ServerUDP(int porta) {
		this.porta = porta;
		this.ipClientes = new HashMap<>();
		this.portaClientes = new HashMap<>();
		this.nomeCliente = new ArrayList<>();
		this.chavesClientes = new HashMap<>();
	}

	public void executa() {
		aSocket = null;
		try {
			//Abre um servidor na porta especificada pelo usuario
			aSocket = new DatagramSocket(porta);
			byte[] buffer = new byte[1024];
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				System.out.println(aSocket.getReceiveBufferSize());
				aSocket.receive(request);
				String mensagem = new String(request.getData(), 0, request.getLength());
				System.out.println("recebido " + mensagem);
				//Depende do conteudo da mensagem, realiza uma ação
				if (mensagem.startsWith("NC: ")) {
					//Novo cliente
					trataNovoCliente(request, mensagem);
				} else if (mensagem.startsWith("MG: ")) {
					//Mensagem comum
					trataNovaMensagem(request, mensagem);
				} else if (mensagem.startsWith("FL: ")) {
					//Novo arquivo
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
		// Essa função, primeiro, cria um novo arquivo com os dados recebido, em seguida
		// encaminha o arquivo
		// para os recebedore, e por último exclui o arquivo localmente

		// Verifica o nome do arquivo
		mensagem = mensagem.substring(4);
		String dados[] = mensagem.split(";", 3);
		byte[] buffer = new byte[1024];
		// Criar um novo arquivo
		File file = new File("Servidor_" + dados[2]);
		FileOutputStream fileOutput = new FileOutputStream(file);

		// Recebe os dados referentes ao arquivo até a chegada do pacote "final" com
		// tamanho 0
		while (true) {
			// Recebe
			DatagramPacket chegou = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(chegou);
			
			// pacote final
			if (chegou.getLength() == 0) {
				System.out.println("Servidor recebeu fim de arquivo");
				// Chamada para enviar os arquivos
				String sender = dados[1].substring(19);
				if(!dados[0].equals("broadcast")) {
					TransmitirArquivo(file, mensagem, dados[0]);
				} else {
					for (String nomeReciever : nomeCliente) 
						if (!nomeReciever.equals(sender)) 
							TransmitirArquivo(file, mensagem, nomeReciever);	
				}
				break;
			}
			// Escreve no arquivo o conteudo do pacote
			fileOutput.write(chegou.getData(), 0, chegou.getLength());

			// Envia um ack para quem está enviando, informando que o pacote chegou
			InetAddress ipSender = chegou.getAddress();
			int portaSender = chegou.getPort();
			String idPacote = "ACK: 1";
			byte[] info_pacote = idPacote.getBytes();
			DatagramPacket enviar = new DatagramPacket(info_pacote, info_pacote.length, ipSender, portaSender);
			aSocket.send(enviar);
		}
		fileOutput.close();
		file.delete();
	}

	private void TransmitirArquivo(File file, String mensagem, String reciver) throws IOException {
		// Mensagem inicial para o recebedor se preparar para receber um arquivo
		String dados[] = mensagem.split(";", 3);
		String response = "FL: " + dados[1] + ";" + dados[2];
		byte[] m = response.getBytes();

		// Ip e porta do recebedor
		InetAddress ipCliete = ipClientes.get(reciver);
		int portaCliente = portaClientes.get(reciver);
		DatagramPacket responsePacket = new DatagramPacket(m, m.length, ipCliete, portaCliente);
		// Envia mensagem infomando que vai começar a enviar um arquivo
		
		aSocket.send(responsePacket);

		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int bytesRead;
		byte[] buff_Recebeu = new byte[1024];
		int bytes_Recebeu = 1024;
		
		int n = 0;
		while ((bytesRead = fileInputStream.read(buffer)) != -1) {
			String mensagemRecebida;
			DatagramPacket pacoteEnviar = new DatagramPacket(buffer, bytesRead, ipCliete, portaCliente);
			DatagramPacket pacoteReceber = new DatagramPacket(buff_Recebeu, bytes_Recebeu);
			do {
				
				aSocket.send(pacoteEnviar);
				System.out.println("Servidor Envia pacote " + n);
				aSocket.receive(pacoteReceber);
				System.out.println("Servidor recebe pacote " + n);
				n++;
				mensagemRecebida = new String(pacoteReceber.getData(), 0, pacoteReceber.getLength());
			} while (!mensagemRecebida.equals("ACK: 1"));
		}
		fileInputStream.close();
		byte[] fim = new byte[0];
		DatagramPacket pacoteFim = new DatagramPacket(fim, fim.length, ipCliete, portaCliente);
		
		aSocket.send(pacoteFim);
		System.out.println("Servidor Termina de enviar");
	}

	private void trataNovaMensagem(DatagramPacket request, String mensagem) throws IOException {
		mensagem = mensagem.substring(4);
		String dados[] = mensagem.split(";", 3);
		String sender = dados[1].substring(19);
		System.out.println("Servidor " + mensagem + " sender " + sender);

		String mensagemCompleta = dados[1] + dados[2];
		byte[] enviarMensagem = mensagemCompleta.getBytes();

		DatagramPacket sendData = new DatagramPacket(enviarMensagem, enviarMensagem.length);

		if (dados[0].equals("broadcast")) {
			for (String nomeReciever : nomeCliente) {
				if (!nomeReciever.equals(sender)) {
					InetAddress ipCliete = ipClientes.get(nomeReciever);
					int portaCliente = portaClientes.get(nomeReciever);
					sendData.setAddress(ipCliete);
					sendData.setPort(portaCliente);
					aSocket.send(sendData);
				}
			}
		} else {
			InetAddress ipCliete = ipClientes.get(dados[0]);
			int portaCliente = portaClientes.get(dados[0]);
			System.out.println(portaCliente);
			sendData.setAddress(ipCliete);
			sendData.setPort(portaCliente);
			aSocket.send(sendData);
		}

	}

	private void trataNovoCliente(DatagramPacket request, String mensagem) throws IOException {
		mensagem = mensagem.substring(4);
		String[] splitMsg = mensagem.split(";");
		String nome = splitMsg[0];
		String publicKeySender = splitMsg[1];

		InetAddress ipSender = request.getAddress();
		int portaSender = request.getPort();

		ipClientes.put(nome, ipSender);
		portaClientes.put(nome, portaSender);
		chavesClientes.put(nome, publicKeySender);
		String enviar = "NC: " + nome + ";" + publicKeySender;
		System.out.println(enviar);
		byte[] novoCliente = enviar.getBytes();
		DatagramPacket novoClienteReplay = new DatagramPacket(novoCliente, novoCliente.length);

		for (String nomeClien : nomeCliente) {
			String publicKeyClienteNovo = chavesClientes.get(nomeClien);
			byte[] clienteAntigo = ("NC: " + nomeClien + ";" + publicKeyClienteNovo).getBytes();

			InetAddress ipCliete = ipClientes.get(nomeClien);
			int portaCliente = portaClientes.get(nomeClien);
			DatagramPacket atualizaNovo = new DatagramPacket(clienteAntigo, clienteAntigo.length, ipSender,
					portaSender);

			novoClienteReplay.setAddress(ipCliete);
			novoClienteReplay.setPort(portaCliente);
			System.out.println("Enviando " + nomeClien + " para " + nome);
			aSocket.send(novoClienteReplay);
			aSocket.send(atualizaNovo);
		}
		nomeCliente.add(nome);
	}

}
