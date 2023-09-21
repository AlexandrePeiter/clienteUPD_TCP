package cliente.view;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import cliente.clienteTCP.Cliente;

import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;

public class ViewCliente extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cliente cliente;
	private JTextPane textPane;
	private JTextField texto;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ViewCliente dialog = new ViewCliente();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ViewCliente() {
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		{
			textPane = new JTextPane();
			textPane.setEditable(false);
			JScrollPane jsp = new JScrollPane(textPane);
			textPane.setForeground(Color.BLACK);
			getContentPane().add(jsp);
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			panel.setLayout(new BorderLayout(0, 0));
			
			JButton btnEnviar = new JButton("Enviar");
			btnEnviar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cliente.send(texto.getText());
					texto.setText("");
				}
			});
			
			panel.add(btnEnviar, BorderLayout.EAST);
			
			texto = new JTextField();
			texto.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						cliente.send(texto.getText());
						texto.setText("");
					}
				}
			});
			panel.add(texto, BorderLayout.CENTER);
			texto.setColumns(10);
		}
	}
	public void Conetar() {
		//cliente = new	Cliente("localhost",12345, this);
		try {
			cliente.executa("Alexandre");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void recebeMensagen(String str) {
		String aux = textPane.getText();
		textPane.setText(aux + str + "\n");;
	}

}
