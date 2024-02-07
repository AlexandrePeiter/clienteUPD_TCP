package servidor.serverTCP;

import javax.crypto.Cipher;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.Map.Entry;

public class Servidor	{
	public	static	void	main(String[]	args)	throws	IOException	{
		new	Servidor(12345).executa();
	}
	
	private int	porta;
	private	HashMap<String, PrintStream> clientes;
	private HashMap<String, String> chavesClientes;
	private List<String> nomeCliente;

	private PublicKey chavePublica;
	private PrivateKey chavePrivada;
	
	public Servidor (int porta) {
		this.porta = porta;
		this.clientes = new	HashMap<>();
		this.chavesClientes = new HashMap<>();
		this.nomeCliente = new ArrayList<>();
	}

	public String encriptar(String txt) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, this.chavePublica);
		byte[] encryptedBytes = cipher.doFinal(txt.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public String decriptar(String txtEncriptado) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, this.chavePrivada);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(txtEncriptado));
		return new String(decryptedBytes);
	}

	public String getStringChavePublica() {
		return Base64.getEncoder().encodeToString(chavePublica.getEncoded());
	}

	@SuppressWarnings("resource")
	public void executa () throws IOException {
		//Cria o servidor na porta especificado pelo usuario
		ServerSocket servidor = new ServerSocket(this.porta);
		System.out.println("Porta 12345 aberta!");
		
		while(true){
			//Realiza o hanshake com o cliente
			Socket cliente = servidor.accept();
			String clientHost = cliente.getInetAddress().getHostAddress();
			System.out.println("Nova conexão com o cliente" + clientHost);
			
			//Cria uma nova stream, coloca na hashmasp, com a chave igual ao nome do cliente
			PrintStream ps = new PrintStream(cliente.getOutputStream());
			
			InputStream is = cliente.getInputStream();
			Scanner	s	=	new	Scanner(is);
			String nome = "", publicKeyCliente = "";
			if(s.hasNextLine()) {
				nome = s.nextLine();
				if(s.hasNextLine())
					publicKeyCliente = s.nextLine();
			}


			System.out.println(nome + " " + publicKeyCliente);
			this.clientes.put(nome, ps);
			this.chavesClientes.put(nome, publicKeyCliente);
			this.nomeCliente.add(nome);
			TrataCliente tc = new TrataCliente(is,	this, nome);

			//Inicia uma nova thread
			new	Thread(tc).start();
			this.distribuiMensagem(nome, "NC: " + nome + ";" + publicKeyCliente);
		}
	}
	
	public void distribuiMensagem(String sender, String msg) {
		//Envia um mensagem para os clientes disponiveis
		for(Entry<String, PrintStream> entrada : clientes.entrySet())	{
			//System.out.println("Enviando para :" + entrada.getKey());
			PrintStream cliente = entrada.getValue();
			if(!entrada.getKey().equals(sender)) {
				//cliente = entrada.getValue();
				cliente.println(msg);
			} else {
				String publicKeyClienteNovo;
				for (String string : nomeCliente) {
					if(!string.equals(sender)) {
						publicKeyClienteNovo = chavesClientes.get(string);
						cliente.println("NC: " + string + ";" + publicKeyClienteNovo);
					}
				}
			}
		}
	}
	public void brodcastMessage(String sender, String message) {
		//Realiza o broadcast de uma mensagem para os clientes disponiveis
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
		//Envia uma mensagem para um cliente especifico
		PrintStream cliente = clientes.get(reciever);
		cliente.println("MS: " + message);
		
	}
	public void enviarArquivo(String sender, String receiver, String message, File dadosArquivo) {
		//Envia um arquivo para um cliente especifico
		PrintStream cliente = clientes.get(receiver);
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
	
	public void enviarArquivo(String nomeSender, String receiver, String message, InputStream dadosArquivo) {
		//Envia um arquvio para um cliente especifico
		PrintStream cliente = clientes.get(receiver);
		cliente.println("FL: " + message);
		int i = 0;
		while( i < 1000000 ) {
			i++;
		}
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
	public void distribuirArquivo(String sender, String message, InputStream dadosArquivo) {
		//Envia uma mensagem para todos os clientes
		byte[] buffer = new byte[1024];
		int bytesRead;
		for(Entry<String, PrintStream> entrada : clientes.entrySet()) {
			PrintStream cliente = entrada.getValue();
			if(!entrada.getKey().equals(sender)) {
				cliente = entrada.getValue();
				cliente.println("FL: " + message);
			}
		}
		int i = 0;
		while( i < 1000000 ) {
			i++;
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

	public void enviarChaveRSA(String receiver){
		//Envia uma mensagem para um cliente especifico
		PrintStream cliente = clientes.get(receiver);
		String chave = getStringChavePublica();
		cliente.println("RSAPK;" + chave);
	}

	public void removerCliente(String nome) {
		//Disconecta um cliente e avisa para os restantes
		clientes.remove(nome);
		nomeCliente.remove(nome);
		for(Entry<String, PrintStream> entrada : clientes.entrySet())	{
			//System.out.println("Enviando para :" + entrada.getKey());
			PrintStream cliente = entrada.getValue();
			cliente.println("OUT;" + nome);
		}
	}
	
}
