package cliente.clienteTCP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import cliente.view.ViewClienteTCP;

public class Cliente	{
	
	private	String	host;
	private String teste;
	private int	porta;
	PrintStream saida;
	Recebedor r;
	Socket	cliente;
	String nome;
	ViewClienteTCP view;
	public	Cliente	(String	host,	int	porta, ViewClienteTCP view) {
		this.host	=	host;
		this.porta	=	porta;
		this.view = view;
	}
	public void executa(String nome) throws	UnknownHostException,	IOException	{
		//Cria uma conex�o com o servidor
		cliente	=	new	Socket(this.host,	this.porta);
		System.out.println("O cliente se conectou ao servidor!");
		this.nome = nome;
		r = new Recebedor(cliente.getInputStream(), view, nome);
		new Thread(r).start();
		saida = new	PrintStream(cliente.getOutputStream());	
		saida.println(nome);
		
	}
	public void send(String str) {
		//Envia uma mensagem para o servidor
		saida.println(str);
	}
	public void sendArquivo(String str, File arquivo) {
		//Envia um arquvio para o servidor
		saida.println(str);
		int i = 0;
		while( i < 100000 ) {
			i++;
		}
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(arquivo);
			// Envie o arquivo byte a byte
			byte[] buffer = new byte[1024];
			int bytesRead;
			System.out.println("Come�ando a enviar");
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				saida.write(buffer, 0, bytesRead);
			}
			System.out.println("Terminando de enviar");
		} catch (IOException e) {
			e.printStackTrace();
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
}
