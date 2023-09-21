package servidor.serverTCP;


import java.io.InputStream;
import java.util.Scanner;

public class TrataCliente implements Runnable	{
	private	InputStream	cliente;
	private String nomeSender;
	private	Servidor	servidor;
	public	TrataCliente(InputStream	cliente,	Servidor	servidor, String nome) {
		this.cliente	=	cliente;
		this.servidor	=	servidor;
		this.nomeSender = nome;
	}
	public	void	run() {
		//	quando	chegar	uma	msg,	distribui	pra	todos
		Scanner	s	=	new	Scanner(this.cliente);
		while	(s.hasNextLine())	{
			String menssagem = s.nextLine();
			String [] dados = menssagem.split(";", 3);
			
			if(dados[0].equals("msg"))
				tratarMensagens(dados);
			else if (dados[0].equals("arq"))
				tratarArquivo(dados);			
		}
		s.close();
	}
	private void tratarArquivo(String[] dados) {
		
		
		servidor.enviarArquivo(nomeSender, dados[1], dados[2], cliente);
		/*String arquivoNome = dados[2];
		File arquivoRecebido = new File(arquivoNome);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(arquivoRecebido);
			byte[] buffer = new byte[1024];
			int bytesRead;
			System.out.println("Entrando no While");
			while ((bytesRead = cliente.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytesRead);	
				if(bytesRead  !=  1024)
	            	break;
			}
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		servidor.enviarArquivo(nomeSender, dados[1], nomeSender + ";" + dados[2], arquivoRecebido);*/
		
	}
	private void tratarMensagens(String[] dados) {
		if(dados[1].equals("broadcast")) {
			servidor.brodcastMessage(nomeSender, dados[2]);
		} else {
			servidor.enviar(nomeSender, dados[1], dados[2]);
		}	
	}
}