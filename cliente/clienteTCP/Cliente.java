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
		cliente	=	new	Socket(this.host,	this.porta);
		System.out.println("O cliente se conectou ao servidor!");
		this.nome = nome;
		r = new Recebedor(cliente.getInputStream(), view);
		new Thread(r).start();
		saida = new	PrintStream(cliente.getOutputStream());	
		saida.println(nome);
		
	}
	public void send(String str) {
		saida.println(str);
	}
	public void sendArquivo(String str, File arquivo) {
		saida.println(str);
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(arquivo);
			// Envie o arquivo byte a byte
			byte[] buffer = new byte[1024];
			int bytesRead;
			System.out.println("Começando a enviar");
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				saida.write(buffer, 0, bytesRead);
			}
			System.out.println("Terminando de enviar");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fechar() throws IOException {
		saida.close();
		cliente.close();
	}
	
}
