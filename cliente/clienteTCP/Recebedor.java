package cliente.clienteTCP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Scanner;


import cliente.view.ViewClienteTCP;

public class Recebedor implements Runnable	{
	private	InputStream	servidor;
	private ViewClienteTCP view;
	public	Recebedor(InputStream	servidor, ViewClienteTCP view) {
		this.servidor	=	servidor;
		this.view = view;
	}
	public	void	run() {
		Scanner s = new Scanner(this.servidor);
		while	(s.hasNext())	{
			
			String str = s.nextLine();
			
			if(!str.startsWith("FL: ")) {
				System.out.println(str);
				view.receberMensagem(str);	
			}
			else {
				//str = str.substring(4);
				String dados[] = str.split(";", 2);
				String arquivoNome = dados[1];
				File arquivoRecebido = new File(arquivoNome);
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(arquivoRecebido);
					byte[] buffer = new byte[1024];
					int bytesRead;
					System.out.println("Entrando dno while recebedor");
					 while ((bytesRead = servidor.read(buffer)) != -1) {
				            fileOutputStream.write(buffer, 0, bytesRead);
				            //System.out.println(bytesRead);
				            if(bytesRead !=  1024)
				            	break;
				        }
					
					System.out.println("Saindo do while av : " + servidor.available());
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				view.receberMensagem(dados[0] + "Enviou o arquivo " +arquivoNome);
				System.out.println("Arquivo recebido com sucesso!");
			}
			s.close();
		}
	}

}
