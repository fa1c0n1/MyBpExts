package me.falcon.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.GridLayout;

public class MainUI extends JFrame {

    private JSplitPane splitPane;

    public JTextArea textAreaRawCipherRd;
    public JTextArea textAreaRawPlainRd;
    public JTextArea textAreaNewPlainWr;
    public JTextArea textAreaNewCipherRd;
    public JButton btnDecrypt;
    public JButton btnEncrypt;

    public MainUI() {
        setBounds(100, 100, 930, 497);
        JPanel contentPane = new JPanel();

        contentPane.setLayout(new GridLayout(2, 1, 5,5));

        textAreaRawCipherRd = new JTextArea();
        textAreaRawPlainRd = new JTextArea();
        textAreaNewPlainWr = new JTextArea();
        textAreaNewCipherRd = new JTextArea();

        textAreaRawCipherRd.setText("textAreaRawCipher");
        textAreaRawCipherRd.setEditable(false);
        textAreaRawPlainRd.setText("textAreaRawPlainRd");
        textAreaRawPlainRd.setEditable(false);
        textAreaNewPlainWr.setText("textAreaNewPlainWr");
        textAreaNewPlainWr.setEditable(true);
        textAreaNewCipherRd.setText("textAreaNewCipherRd");
        textAreaNewCipherRd.setEditable(false);

        btnDecrypt = new JButton("解密");
        btnEncrypt = new JButton("加密");

        contentPane.add(textAreaRawCipherRd);
        contentPane.add(btnDecrypt);
        contentPane.add(textAreaRawPlainRd);
        contentPane.add(textAreaNewCipherRd);
        contentPane.add(btnEncrypt);
        contentPane.add(textAreaNewPlainWr);

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
