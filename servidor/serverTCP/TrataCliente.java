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
			String mensagem = s.nextLine();
			String [] dados = mensagem.split(";", 3);

			if(dados[0].equals("msg"))
				tratarMensagens(dados);
			else if (dados[0].equals("arq"))
				tratarArquivo(dados);
			else if (dados[0].equals("OUT"))
				servidor.removerCliente(dados[1]);
		}
		s.close();
		
	}
	private void tratarArquivo(String[] dados) {
		
		if(dados[1].equals("broadcast")) {
			servidor.distribuirArquivo(nomeSender, dados[2], cliente);
		} else {
			servidor.enviarArquivo(nomeSender, dados[1], dados[2], cliente);
		}
	
	}
	private void tratarMensagens(String[] dados) {
		if(dados[1].equals("broadcast")) {
			servidor.brodcastMessage(nomeSender, dados[2]);
		} else {
			servidor.enviar(nomeSender, dados[1], dados[2]);
		}	
	}
}