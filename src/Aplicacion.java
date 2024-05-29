import analisis.Parser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Aplicacion extends JFrame implements ActionListener {
    private JPanel contenido;
    private JLabel lblResultadoSintactico, lblResultadoLexico;
    private JTextArea txtCodigo, txtByteCode;
    private JButton btnAnalizar;
    public Aplicacion(){
        super("Parser Recursivo Descendente");
        setSize(400,400);
        setLocationRelativeTo(null);

        lblResultadoLexico = new JLabel("");
        lblResultadoSintactico = new JLabel("");
        btnAnalizar = new JButton("Analizar");
        txtCodigo = new JTextArea();
        JPanel p = new JPanel();

        p.add(lblResultadoLexico);
        p.add(lblResultadoSintactico);

        add(p, BorderLayout.NORTH);
        txtByteCode = new JTextArea("ByteCode:");
        contenido = new JPanel(new GridLayout(1,2, 15, 15));
        contenido.add(txtCodigo);
        contenido.add(txtByteCode);


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
        System.out.println(txtCodigo.getText());
        Parser p = new Parser(txtCodigo.getText());
        txtByteCode.setText("ByteCode: \n" + p.getBytecode());
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
    }
}
