package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.List;
import java.awt.TextField;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import clienteTCP.Cliente;
import clienteUDP.ClienteUDP;

import java.awt.GridLayout;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class ViewClienteUDP extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNomeDoCliente;
	private JTextField textField_1;
	private JTextPane textPane;
	private ClienteUDP clienteUDP;
	private ViewClienteUDP this_viewClienteUDP;
	private List list;
	private String nome;
	private String nomeArquivo;
	private File arquivo;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewClienteUDP frame = new ViewClienteUDP();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ViewClienteUDP() {
		this_viewClienteUDP = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 556, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		txtNomeDoCliente = new JTextField();
		txtNomeDoCliente.setText("132");
		txtNomeDoCliente.setToolTipText("Nome do cliente");
		panel.add(txtNomeDoCliente);
		txtNomeDoCliente.setColumns(10);

		JButton btnNewButton = new JButton("Conectar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nomeCliente = txtNomeDoCliente.getText();

				clienteUDP = new ClienteUDP("localhost", 6789, this_viewClienteUDP);

				try {
					clienteUDP.executa(nomeCliente);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				nome = nomeCliente;

				btnNewButton.setEnabled(false);
			}
		});
		panel.add(btnNewButton, BorderLayout.EAST);

		JLabel lblNewLabel = new JLabel("Nome:");
		panel.add(lblNewLabel, BorderLayout.WEST);

		textPane = new JTextPane();
		contentPane.add(textPane, BorderLayout.CENTER);

		this.list = new List();
		contentPane.add(list, BorderLayout.WEST);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 365, 0, 61, 0 };
		gbl_panel_1.rowHeights = new int[] { 21, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JButton btnNewButton_1 = new JButton("Enviar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String mensagem = textField_1.getText();
				String destino = list.getSelectedItem();
				System.out.println(destino + ";" + nome + ": " + mensagem);
				textField_1.setText("");

				if (arquivo == null)
					clienteUDP.send("MG: " + destino + ";" + nome + ": " + mensagem);
				else {
					try {
						clienteUDP.sendArquivo("FL: " + destino + ";"+ nome + ";" + "UDP"+arquivo.getName(), arquivo);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					arquivo = null;
				}
				textField_1.setEditable(true);
			}
		});

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.BOTH;
		gbc_textField_1.insets = new Insets(0, 0, 0, 5);
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 0;
		panel_1.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

		JButton btnNewButton_2 = new JButton("");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				int respostDoFileChooser = jFileChooser.showOpenDialog(null);
				if (respostDoFileChooser == JFileChooser.APPROVE_OPTION) {
					arquivo = jFileChooser.getSelectedFile();
					nomeArquivo = jFileChooser.getSelectedFile().getName();
					textField_1.setText(nomeArquivo);
					textField_1.setEditable(false);
				} else {
					System.out.println("Nada selecionado");
					textField_1.setText("");
					textField_1.setEditable(true);
				}
			}
		});
		btnNewButton_2.setIcon(new ImageIcon(ViewClienteUDP.class.getResource("/resources/anexar-arquivo.png")));
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_2.gridx = 1;
		gbc_btnNewButton_2.gridy = 0;
		panel_1.add(btnNewButton_2, gbc_btnNewButton_2);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton_1.gridx = 2;
		gbc_btnNewButton_1.gridy = 0;
		panel_1.add(btnNewButton_1, gbc_btnNewButton_1);
	}

	public void receberMensagem(String mensagem) {
		if (mensagem.startsWith("NC: ")) {
			list.add(mensagem.substring(4));
		} else {
			// System.out.println("Escrevendo mensagem" + mensagem);
			String texto = textPane.getText();
			textPane.setText(texto + mensagem + "\n");
			// textField.set
		}
		// System.out.println("mensagem: " + mensagem);
	}
}
