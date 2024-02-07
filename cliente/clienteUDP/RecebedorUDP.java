package cliente.clienteUDP;

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.PrivateKey;
import java.util.Arrays;


import cliente.view.ViewClienteUDP;
import rsa.RSAUtils;

import javax.crypto.Cipher;

public class RecebedorUDP implements Runnable {
	
	private ViewClienteUDP view;
	DatagramSocket aSocket;
	private int ack;
	private String nome;

	private PrivateKey privateKey;
	
	public RecebedorUDP(DatagramSocket aSocket, ViewClienteUDP view, String nome, PrivateKey privateKey) {
		this.view = view;
		this.aSocket = aSocket;
		this.nome = nome;
		this.privateKey = privateKey;
	}
	@Override
	public void run() {
		while(true) {
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			try {
				aSocket.receive(reply);
				
				String mensagem = new String(reply.getData(), 0, reply.getLength());
				//Dependendo do prefixo da mensamge realiza uma ação
				if(mensagem.startsWith("FL: ")) {
					//Arquivo
					trataArquivo(mensagem);
				} else if(mensagem.startsWith("ACK: ")){
					//Ack de arquivo
					setAck(mensagem);
				} else if(mensagem.startsWith("OUT")) {
					//Saida de um contato
					view.removerCliente(mensagem);
				} else {
					//Mensagem comum
					view.receberMensagem(mensagem, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	private void setAck(String mensagem) {
		//Avisa que recebeu um ack
		mensagem = mensagem.substring(5);
		
		this.ack = Integer.parseInt(mensagem);
	}
	public int  getAck() {
		//Retorna o ack recebido
		if(this.ack != -1) {
			int aux = ack;
			ack = -1;
			return aux;
		}
		return ack;
	}
	private void trataArquivo(String mensagem) throws Exception {
		//Cria um arquivo a partir dos dados recebidos
		mensagem = mensagem.substring(4);
		String[] dados = RSAUtils.decrypt(mensagem, this.privateKey).split(";", 2);
		System.out.println(Arrays.toString(dados));

		String path = "UDP";
		File directory = new File(path);
		if (!directory.exists()) {
			if (directory.mkdirs())
				System.out.println("Diretório criado com sucesso.");
			else
				System.out.println("Falha ao criar o diretório.");
		}

		Cipher cipher = RSAUtils.getCipherDecryptInstance(privateKey);
		FileOutputStream fileOutputStream = new FileOutputStream(path+"//"+nome+"_"+dados[1]);
		
		byte[] buffer = new byte[256];
		while (true) {
            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(pacote);

            if (pacote.getLength() == 0) {
            	System.out.println("Recebedor recebeu fim de arquivo");
                break; // Fim do arquivo
            }


			byte[] decryptedData = cipher.doFinal(pacote.getData(), 0, pacote.getLength());
          //Envia um ack para quem está enviando, informando que o pacote chegou
            InetAddress ipSender = pacote.getAddress();
    		int portaSender = pacote.getPort();
            String idPacote = "ACK: 1";
            byte[] info_pacote = idPacote.getBytes();
            DatagramPacket enviar = new DatagramPacket(info_pacote, info_pacote.length, ipSender, portaSender);
            aSocket.send(enviar);


            fileOutputStream.write(decryptedData, 0, decryptedData.length);
        }
		System.out.println(nome+"_"+dados[1]);
        fileOutputStream.close();
		view.receberMensagem(dados[0] + ": Enviou o arquivo " + dados[1], false);
	}

}
