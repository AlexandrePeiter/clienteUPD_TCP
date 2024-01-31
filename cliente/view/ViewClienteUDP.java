package cliente.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import cliente.clienteUDP.ClienteUDP;


import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
	private final JScrollPane scrollPane = new JScrollPane();
	private StyledDocument styledDoc;
	private JButton btnNewButton;
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
		setTitle("UDP Cliente");
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

		btnNewButton = new JButton("Conectar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nome = txtNomeDoCliente.getText();
				
				boolean valido = nome.matches("^[a-zA-Z0-9\\s]+$");
		        if(!valido) {
		        	JOptionPane.showMessageDialog(contentPane, "Erro: Insira apenas alfanuméricos.", "Erro", JOptionPane.ERROR_MESSAGE);
		        } if(nome.equals("broadcast")) {
		        	JOptionPane.showMessageDialog(contentPane, "Erro: Nome Inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
		        } else {
		        	exibirCampos();
		        }
			}
		});
		panel.add(btnNewButton, BorderLayout.EAST);

		JLabel lblNewLabel = new JLabel("Nome:");
		panel.add(lblNewLabel, BorderLayout.WEST);

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
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

				String horaSistema = LocalDateTime.now().format(formatter);
				if (arquivo == null) {
					clienteUDP.send("MG: " + destino + ";" + "(" + horaSistema + ") " + nome + ";: " + mensagem);
					addTextWithAlignment(styledDoc, "(" + horaSistema + ") " + destino + " > " + mensagem + "\n",
							Alignment.RIGHT, Color.BLUE);
				}
				else {
					try {
						clienteUDP.sendArquivo("FL: " + destino + ";" + "(" + horaSistema + ") " + nome + ";" + "UDP" + arquivo.getName(),
								arquivo);
						addTextWithAlignment(styledDoc, "(" + horaSistema + ") " + destino + " > " + "Arquivo: " + arquivo.getName() + "\n",
								Alignment.RIGHT, Color.BLUE);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					arquivo = null;
				}
				textField_1.setEditable(true);
			}
		});

		textField_1 = new JTextField();
		textPane = new JTextPane();
		styledDoc = textPane.getStyledDocument();
		textField_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String mensagem = textField_1.getText();
					String destino = list.getSelectedItem();
					System.out.println(destino + ";" + nome + ": " + mensagem);

					textField_1.setText("");

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

					String horaSistema = LocalDateTime.now().format(formatter);

					if (arquivo == null)
						clienteUDP.send("MG: " + destino + ";" + "(" + horaSistema + ") " + nome + ";: " + mensagem);
					addTextWithAlignment(styledDoc, "(" + horaSistema + ") " + destino + " > " + mensagem + "\n",
							Alignment.RIGHT, Color.BLUE);
					textField_1.setEditable(true);
				}
			}
		});
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
		btnNewButton_2
				.setIcon(new ImageIcon(ViewClienteUDP.class.getResource("/cliente/resources/anexar-arquivo.png")));
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

		contentPane.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(textPane);

		JLabel lblNewLabel_1 = new JLabel("Mensagens");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		scrollPane.setColumnHeaderView(lblNewLabel_1);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new BorderLayout(0, 0));

		this.list = new List();
		list.add("broadcast");
		list.select(0);
		panel_2.add(list);

		JLabel lblNewLabel_2 = new JLabel("Clientes");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblNewLabel_2, BorderLayout.NORTH);
	}

	public void receberMensagem(String mensagem) {
		if (mensagem.startsWith("NC: ")) {
			list.add(mensagem.substring(4));
		} else {
			// System.out.println("Escrevendo mensagem" + mensagem);
			addTextWithAlignment(styledDoc, mensagem + "\n", Alignment.LEFT, Color.BLACK);
			// textField.set
		}
		// System.out.println("mensagem: " + mensagem);
	}

	enum Alignment {
		LEFT, CENTER, RIGHT
	}

	private static void addTextWithAlignment(StyledDocument doc, String text, Alignment alignment, Color cor) {
		SimpleAttributeSet set = new SimpleAttributeSet();

		switch (alignment) {
		case LEFT:
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_LEFT);
			break;
		case CENTER:
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_CENTER);
			break;
		case RIGHT:
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_RIGHT);
			break;
		}

		// Define a cor do texto (aqui, usamos preto)
		StyleConstants.setForeground(set, cor);
		try {
			// Cria um parágrafo com o estilo de alinhamento
			doc.insertString(doc.getLength(), text, set);
			doc.setParagraphAttributes(doc.getLength() - text.length(), text.length(), set, false);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void exibirCampos() {
		JFrame frame = new JFrame("Servidor UPD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Painel para conter os componentes
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        frame.setBounds(165, 250, 100, 100);
        // Rótulos e campos de entrada
        JLabel nameLabel = new JLabel("Endereço IP:");
        JTextField nameField = new JTextField(20);
        nameField.setText("localhost");
        JLabel numberLabel = new JLabel("Porta :");
        JTextField numberField = new JTextField(20);
        numberField.setText("6789");
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(numberLabel);
        panel.add(numberField);

        // Botão "OK" e "Cancelar"
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancelar");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String host = nameField.getText();
                String numero = numberField.getText();
                // Verificar se os campos estão vazios
                if (host.isEmpty() || numero.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        int porta = Integer.parseInt(numero);
                        System.out.println(host + porta);
                        
                        clienteUDP = new ClienteUDP(host, porta, this_viewClienteUDP);

        				try {
        					clienteUDP.executa(nome);
         					btnNewButton.setEnabled(false);
        				} catch (Exception e1) {
        					JOptionPane.showMessageDialog(frame, "Erro: Servidor não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
        				}
         				
                        frame.dispose(); // Fechar a janela após a confirmação
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Erro: O valor inserido não é um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Fechar a janela ao clicar em Cancelar
            }
        });
        panel.add(okButton);
        panel.add(cancelButton);
        // Adicionar o painel à janela de diálogo
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
	}

	public void removerCliente(String mensagem) {
		mensagem = mensagem.substring(4);
		list.remove(mensagem);
		
	}
}
