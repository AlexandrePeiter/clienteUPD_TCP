package clienteUDP;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import view.ViewClienteUDP;

public class RecebedorUDP implements Runnable {
	
	private ViewClienteUDP view;
	DatagramSocket aSocket;
	
	public RecebedorUDP(DatagramSocket aSocket, ViewClienteUDP view) {
		this.view = view;
		this.aSocket = aSocket;
	}

	@Override
	public void run() {
		while(true) {
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			try {
				aSocket.receive(reply);
				String mensagem = new String(reply.getData(), 0, reply.getLength());
				if(mensagem.startsWith("FL: ")) {
					trataArquivo(mensagem);
				} else {
					view.receberMensagem(mensagem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void trataArquivo(String mensagem) throws Exception {
		mensagem = mensagem.substring(4);
		String dados[] = mensagem.split(";", 2);
		FileOutputStream fileOutputStream = new FileOutputStream(dados[1]);
		byte[] buffer = new byte[1024];
		
		while (true) {
            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(pacote);
            
            // Verificar se é o pacote de fim de arquivo (vazio)
            if (pacote.getLength() == 0 || pacote.getLength() < 1024) {
            	System.out.println("Recebedor recebeu fim de arquivo");
                break; // Fim do arquivo
            }
            
            fileOutputStream.write(pacote.getData(), 0, pacote.getLength());
        }
        
        fileOutputStream.close();
		view.receberMensagem(dados[0] + " enviou o arquivo: " + dados[1]);
	}

}
