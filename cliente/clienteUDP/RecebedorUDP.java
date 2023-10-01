package cliente.clienteUDP;

import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


import cliente.view.ViewClienteUDP;

public class RecebedorUDP implements Runnable {
	
	private ViewClienteUDP view;
	DatagramSocket aSocket;
	private int ack;
	private String nome;
	
	public RecebedorUDP(DatagramSocket aSocket, ViewClienteUDP view, String nome) {
		this.view = view;
		this.aSocket = aSocket;
		this.nome = nome;
	}
	@Override
	public void run() {
		while(true) {
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			try {
				aSocket.receive(reply);
				
				String mensagem = new String(reply.getData(), 0, reply.getLength());
				//System.out.println("Recebedor recebu: " + mensagem);
				if(mensagem.startsWith("FL: ")) {
					trataArquivo(mensagem);
				} else if(mensagem.startsWith("ACK: ")){
					setAck(mensagem);
				} else {
					view.receberMensagem(mensagem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	private void setAck(String mensagem) {
		mensagem = mensagem.substring(5);
		
		this.ack = Integer.parseInt(mensagem);
		//System.out.println("Decodicado: " + this.ack);
	}
	public int  getAck() {
		if(this.ack != -1) {
			int aux = ack;
			ack = -1;
			return aux;
		}
		return ack;
	}
	private void trataArquivo(String mensagem) throws Exception {
		mensagem = mensagem.substring(4);
		String dados[] = mensagem.split(";", 2);
		FileOutputStream fileOutputStream = new FileOutputStream(nome+"_"+dados[1]);
		
		byte[] buffer = new byte[1024];
		while (true) {
            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(pacote);
            
           
            if (pacote.getLength() == 0) {
            	System.out.println("Recebedor recebeu fim de arquivo");
                break; // Fim do arquivo
            }
            
          //Envia um ack para quem está enviando, informando que o pacote chegou
            InetAddress ipSender = pacote.getAddress();
    		int portaSender = pacote.getPort();
            String idPacote = "ACK: 1";
            byte[] info_pacote = idPacote.getBytes();
            DatagramPacket enviar = new DatagramPacket(info_pacote, info_pacote.length, ipSender, portaSender);
            aSocket.send(enviar);
         
            fileOutputStream.write(pacote.getData(), 0, pacote.getLength());
        }
		System.out.println(nome+"_"+dados[1]);
        fileOutputStream.close();
		view.receberMensagem(dados[0] + ": Enviou o arquivo " + dados[1]);
	}

}
