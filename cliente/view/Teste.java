package cliente.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Teste {
	public static void main(String[] args) {
        // Cria��o da janela de di�logo
        JFrame frame = new JFrame("Entrada de Dados");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Painel para conter os componentes
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        // R�tulos e campos de entrada
        JLabel nameLabel = new JLabel("Nome:");
        JTextField nameField = new JTextField(20);

        JLabel numberLabel = new JLabel("N�mero:");
        JTextField numberField = new JTextField(20);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(numberLabel);
        panel.add(numberField);

        // Bot�o "OK" e "Cancelar"
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancelar");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = nameField.getText();
                String numero = numberField.getText();
                // Verificar se os campos est�o vazios
                if (nome.isEmpty() || numero.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        int numeroInteiro = Integer.parseInt(numero);

                        // Exibir os valores coletados
                        JOptionPane.showMessageDialog(frame, "Nome: " + nome + "\nN�mero: " + numeroInteiro);
                        frame.dispose(); // Fechar a janela ap�s a confirma��o
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Erro: O valor inserido n�o � um n�mero v�lido.", "Erro", JOptionPane.ERROR_MESSAGE);
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
        // Adicionar o painel � janela de di�logo
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
