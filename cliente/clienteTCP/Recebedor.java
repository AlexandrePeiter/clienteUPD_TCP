package cliente.clienteTCP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Scanner;


import cliente.view.ViewClienteTCP;

public class Recebedor implements Runnable	{
	private	InputStream	servidor;
	private ViewClienteTCP view;
	private String nome;
	
	public	Recebedor(InputStream	servidor, ViewClienteTCP view, String nome) {
		this.servidor	=	servidor;
		this.view = view;
		this.nome = nome;
	}
	public	void	run() {
		Scanner s = new Scanner(this.servidor);
		while	(s.hasNext())	{
			//Dependendo do prefixo da mensagem realiza uma ação diferente
			String str = s.nextLine();
			//Retira um contato da lista
			System.out.println("str: " + str);
			if (str.startsWith("OUT")) {
				view.removerCliente(str);
			} else	if(!str.startsWith("FL: ")) {//Mensagem comum
				System.out.println(str);
				view.receberMensagem(str);	
			}
			else {
				//Mensagem de recepção de arquivo
				//str = str.substring(4);
				String dados[] = str.split(";", 2);
				String arquivoNome = dados[1];
				File arquivoRecebido = new File("TCP//" +nome+"_"+arquivoNome);
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(arquivoRecebido);
					byte[] buffer = new byte[1024];
					int bytesRead;
					System.out.println("Entrando no while recebedor");
					 while ((bytesRead = servidor.read(buffer)) != -1) {
				            fileOutputStream.write(buffer, 0, bytesRead);
				            //System.out.println(bytesRead);
				            if(bytesRead !=  1024)
				            	break;
				        }
					
					System.out.println("Saindo do while av : " + bytesRead);
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				view.receberMensagem(dados[0] + "Enviou o arquivo " +arquivoNome);
				System.out.println("Arquivo recebido com sucesso!");
			}
			
		}
		s.close();
	}

}
