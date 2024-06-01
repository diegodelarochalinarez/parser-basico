import analisis.Parser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class Aplicacion extends JFrame implements ActionListener {
    private JPanel contenido;
    private JLabel lblResultadoSintactico, lblResultadoLexico;
    private JTextArea txtCodigo, txtEnsamblador;
    private JButton btnAnalizar, btnArchivo;
    public Aplicacion(){
        super("Parser Recursivo Descendente");
        setSize(600,700);
        setLocationRelativeTo(null);

        lblResultadoLexico = new JLabel("");
        lblResultadoSintactico = new JLabel("");
        btnAnalizar = new JButton("Analizar");
        btnArchivo = new JButton("Elegir Archivo");
        txtCodigo = new JTextArea();
        JPanel p = new JPanel(new GridLayout(1,3,10,10));
        btnArchivo.addActionListener(this);
        p.add(btnArchivo);
        p.add(lblResultadoLexico);
        p.add(lblResultadoSintactico);

        add(p, BorderLayout.NORTH);
        txtEnsamblador = new JTextArea("");
        contenido = new JPanel(new GridLayout(1,2, 15, 15));
        contenido.add(txtCodigo);
        JScrollPane scrollPane = new JScrollPane(txtEnsamblador);
        contenido.add(scrollPane);


        add(contenido, BorderLayout.CENTER);
        add(btnAnalizar, BorderLayout.SOUTH);

        btnAnalizar.addActionListener(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Aplicacion();
   }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnAnalizar){
            System.out.println(txtCodigo.getText());
            Parser p = new Parser(txtCodigo.getText());
            txtEnsamblador.setText(p.getAssemblerCode());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("ensambla.txt"))) {
                writer.write(p.getAssemblerCode());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if(p.isCorrecto()) {
                lblResultadoSintactico.setText("Sintacticamente Correcto");
                lblResultadoSintactico.setForeground(new Color(106, 166, 108));

            }else{
                lblResultadoSintactico.setText("Sintacticamente Incorrecto");
                lblResultadoSintactico.setForeground(new Color(191, 25, 25));
            }
            if(!p.isCorrectoLexico()){
                lblResultadoLexico.setText("Lexicamente Correcto");
                lblResultadoLexico.setForeground(new Color(106, 166, 108));
            }else{
                lblResultadoLexico.setText("Lexicamente Incorrecto");
                lblResultadoLexico.setForeground(new Color(191, 25, 25));
            }
            return;
        }
        if(e.getSource() == btnArchivo){
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    txtCodigo.setText(content.toString());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
