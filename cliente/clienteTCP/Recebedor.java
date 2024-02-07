package cliente.clienteTCP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Scanner;


import cliente.view.ViewClienteTCP;
import rsa.RSAUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Recebedor implements Runnable	{
	private	InputStream	servidor;
	private ViewClienteTCP view;
	private String nome;
	private PrivateKey privateKey;
	
	public	Recebedor(InputStream	servidor, ViewClienteTCP view, String nome, PrivateKey privateKey) {
		this.servidor	=	servidor;
		this.view = view;
		this.nome = nome;
		this.privateKey = privateKey;
	}
	public	void	run() {
		Scanner s = new Scanner(this.servidor);
		while	(s.hasNext())	{
			//Dependendo do prefixo da mensagem realiza uma ação diferente
			try {
				String str = s.nextLine();
				System.out.println("str: " + str);
				//Retira um contato da lista
				if (str.startsWith("OUT")) {
					view.removerCliente(str);
				} else if (!str.startsWith("FL: ")) {//Mensagem comum
					System.out.println(str);
					view.receberMensagem(str, true);
                } else {
					trataArquivo(str);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
        }
		s.close();
	}

	private void trataArquivo(String mensagem) throws Exception {
		//Mensagem de recepção de arquivo
		//str = str.substring(4);
		mensagem = mensagem.substring(4);
		String[] dados = RSAUtils.decrypt(mensagem, this.privateKey).split(";", 2);
		//String dados[] = str.split(";", 2);
		System.out.println(Arrays.toString(dados));

		String path = "TCP";
		File directory = new File(path);
		if (!directory.exists()) {
			if (directory.mkdirs())
				System.out.println("Diretório criado com sucesso.");
			else
				System.out.println("Falha ao criar o diretório.");
		}

		Cipher cipher = RSAUtils.getCipherDecryptInstance(this.privateKey);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(path+"//"+nome+"_TCP"+dados[1]);
			byte[] buffer = new byte[256];
			int bytesRead;
			System.out.println("Entrando no while recebedor");
			while ((bytesRead = servidor.read(buffer)) != -1) {
				if(bytesRead != 1){
					byte[] desencryptedBytes = cipher.doFinal(buffer);
					fileOutputStream.write(desencryptedBytes, 0, desencryptedBytes.length);
				}

				if (bytesRead != 256)
					break;
			}
			fileOutputStream.close();
			System.out.println("Saindo do while av : " + bytesRead);
		} catch (Exception e) {
			e.printStackTrace();
		}
		view.receberMensagem(dados[0] + ": Enviou o arquivo " + dados[1], false);
		System.out.println("Arquivo recebido com sucesso!");
	}

}
