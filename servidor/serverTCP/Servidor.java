package servidor.serverTCP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class Servidor	{
	public	static	void	main(String[]	args)	throws	IOException	{
		new	Servidor(12345).executa();
	}
	
	private int	porta;
	private	HashMap<String, PrintStream> clientes;
	private List<String> nomeCliente;
	
	public Servidor (int porta) {
		this.porta = porta;
		this.clientes = new	HashMap<>();
		this.nomeCliente = new ArrayList<>();
	}
	@SuppressWarnings("resource")
	public void executa () throws IOException {
		ServerSocket servidor = new ServerSocket(this.porta);
		System.out.println("Porta 12345 aberta!");
		while(true){
			Socket cliente = servidor.accept();
			String clientHost = cliente.getInetAddress().getHostAddress();
			System.out.println("Nova conexão com o cliente" + clientHost);
			PrintStream ps = new PrintStream(cliente.getOutputStream());
			
			InputStream is = cliente.getInputStream();
			Scanner	s	=	new	Scanner(is);
			String nome = "";
			if(s.hasNextLine()) {
				nome = s.nextLine();
			}
			System.out.println("Nome Cliente: "+nome);
			
			
			this.clientes.put(nome, ps);
			this.nomeCliente.add(nome);
			TrataCliente tc = new TrataCliente(is,	this, nome);
			
			new	Thread(tc).start();
			this.distribuiMensagem(nome, "NC: " + nome);
		}
	}
	
	public void distribuiMensagem(String sender, String msg) {
		for(Entry<String, PrintStream> entrada : clientes.entrySet())	{
			//System.out.println("Enviando para :" + entrada.getKey());
			PrintStream cliente = entrada.getValue();
			if(!entrada.getKey().equals(sender)) {
				cliente = entrada.getValue();
				cliente.println(msg);
			} else { 
				for (String string : nomeCliente) {
					if(!string.equals(sender))
						cliente.println("NC: " + string);
				}
			}
		}
	}
	public void brodcastMessage(String sender, String message) {
		for(Entry<String, PrintStream> entrada : clientes.entrySet())	{
			//System.out.println("Enviando para :" + entrada.getKey());
			PrintStream cliente = entrada.getValue();
			if(!entrada.getKey().equals(sender)) {
				cliente = entrada.getValue();
				cliente.println("MS: " +message);
			}
		}
	}
	public void enviar(String sender, String reciever, String message) {
		PrintStream cliente = clientes.get(reciever);
		cliente.println("MS: " + message);
		
	}
	public void enviarArquivo(String sender, String reciever, String message, File dadosArquivo) {
		PrintStream cliente = clientes.get(reciever);
		FileInputStream fileInputStream;
		cliente.println("FL: " + message);
		try {
			fileInputStream = new FileInputStream(dadosArquivo);
			// Envie o arquivo byte a byte
			byte[] buffer = new byte[1024];
			int bytesRead;
			System.out.println("Começando a enviar Arquivo");
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				cliente.write(buffer, 0, bytesRead);
			}
			fileInputStream.close();
			System.out.println("terminado a enviar");
			dadosArquivo.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enviarArquivo(String nomeSender, String reciever, String message, InputStream dadosArquivo) {
		PrintStream cliente = clientes.get(reciever);
		cliente.println("FL: " + message);
		byte[] buffer = new byte[1024];
		int bytesRead;
		try {
			while ((bytesRead = dadosArquivo.read(buffer)) != -1) {
				cliente.write(buffer, 0, bytesRead);
				if(bytesRead  !=  1024)
	            	break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void destribuirArquivo(String sender, String message, InputStream dadosArquivo) {
		byte[] buffer = new byte[1024];
		int bytesRead;
		for(Entry<String, PrintStream> entrada : clientes.entrySet()) {
			PrintStream cliente = entrada.getValue();
			if(!entrada.getKey().equals(sender)) {
				cliente = entrada.getValue();
				cliente.println("FL: " + message);
			}
		}
		
		try {
			while ((bytesRead = dadosArquivo.read(buffer)) != -1) {
				for(Entry<String, PrintStream> entrada : clientes.entrySet())	{
					PrintStream cliente = entrada.getValue();
					if(!entrada.getKey().equals(sender)) {
						cliente = entrada.getValue();
						cliente.write(buffer, 0, bytesRead);
					} 					
				}	
				if(bytesRead  !=  1024)
	            	break;
			}
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
}
