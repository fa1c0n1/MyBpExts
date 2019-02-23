package me.falcon.ui;

import me.falcon.utils.AESUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static javax.swing.JOptionPane.PLAIN_MESSAGE;


public class MainUI extends JFrame {

    public JPanel mainPanel;
    public JPanel cipherPanel;
    public JPanel plainPanel;
    public JPanel optionPanel;
    public JTextArea textAreaCipher;
    public JTextArea textAreaPlain;
    public JButton btnDecrypt;
    public JButton btnEncrypt;
    public JTextField textFieldAesKey;
    public JComboBox cmboxMode;
    public JComboBox cmboxPadding;
    public JComboBox cmboxCipherDataForm;

    public MainUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
        cipherPanel = new JPanel();
        cipherPanel.setLayout(new GridLayout(1, 1));
        plainPanel = new JPanel();
        plainPanel.setLayout(new GridLayout(1, 1));
        optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));

        mainPanel.setLayout(new GridLayout(1, 3, 5,5));
        mainPanel.add(cipherPanel);
        mainPanel.add(optionPanel);
        mainPanel.add(plainPanel);

        textAreaCipher = new JTextArea();
        textAreaCipher.setLineWrap(true);
        textAreaPlain = new JTextArea();
        textAreaPlain.setLineWrap(true);
        textAreaCipher.setText("textAreaRawCipher");
        textAreaPlain.setText("textAreaRawPlain");
        cipherPanel.add(textAreaCipher);
        plainPanel.add(textAreaPlain);

        textFieldAesKey = new JTextField();
        cmboxMode = new JComboBox();
        cmboxMode.addItem("ECB");
        cmboxMode.addItem("CBC");

        cmboxPadding = new JComboBox();
        cmboxPadding.addItem("NoPadding");
        cmboxPadding.addItem("PKCS5Padding");
        cmboxPadding.addItem("PKCS7Padding");
        cmboxPadding.addItem("ZeroBytePadding");
        cmboxPadding.addItem("ISO10126Padding");

        cmboxCipherDataForm = new JComboBox();
        cmboxCipherDataForm.addItem("Hex");
        cmboxCipherDataForm.addItem("Base64");

        textAreaPlain.addFocusListener(new JTextComponentHintListener(textAreaPlain, "请输入数据明文"));
        textAreaCipher.addFocusListener(new JTextComponentHintListener(textAreaCipher,"请输入数据密文"));

        btnDecrypt = new JButton("解密→");
        btnEncrypt = new JButton("←加密");

        btnDecrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aesKey = textFieldAesKey.getText();
                String cipherDataStr = textAreaCipher.getText().trim();
                String mode = (String) cmboxMode.getSelectedItem();
                String padding = (String) cmboxPadding.getSelectedItem();
                String workMode = "AES" + "/" + mode + "/" + padding;
                String cipherDataForm = (String) cmboxCipherDataForm.getSelectedItem();
                try {
                    textAreaPlain.setForeground(Color.BLACK);
                    if ("Hex".equals(cipherDataForm)) {
                        byte[] cipherData = Hex.decodeHex(cipherDataStr.toCharArray());
                        textAreaPlain.setText(new String(AESUtils.aesDecrypt(aesKey, cipherData, workMode), "utf-8").trim());
                    } else if ("Base64".equals(cipherDataForm)) {
                        byte[] cipherData = Base64.decodeBase64(cipherDataStr);
                        textAreaPlain.setText(new String(AESUtils.aesDecrypt(aesKey, cipherData, workMode), "utf-8").trim());
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, e1.getMessage(), "解密失败", PLAIN_MESSAGE);
                }
            }
        });

        btnEncrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aesKey = textFieldAesKey.getText();
                String mode = (String) cmboxMode.getSelectedItem();
                String padding = (String) cmboxPadding.getSelectedItem();
                String plainData = textAreaPlain.getText().trim();
                String cipherDataForm = (String) cmboxCipherDataForm.getSelectedItem();
                try {
                    if ("NoPadding".equals(padding)) {
                        plainData = AESUtils.aesPadding16(plainData);
                    }
                    String workMode = "AES" + "/" + mode + "/" + padding;
                    textAreaCipher.setForeground(Color.BLACK);
                    byte[] cipherData = AESUtils.aesEncrypt(aesKey, plainData.getBytes("utf-8"), workMode);
                    if ("Hex".equals(cipherDataForm)) {
                        String cipherStr = Hex.encodeHexString(cipherData);
                        textAreaCipher.setText(cipherStr);
                    } else if ("Base64".equals(cipherDataForm)) {
                        String cipherStr = Base64.encodeBase64String(cipherData);
                        textAreaCipher.setText(cipherStr);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, e1.getMessage(), "加密失败", PLAIN_MESSAGE);
                }
            }
        });

        optionPanel.add(textFieldAesKey);
        optionPanel.add(cmboxMode);
        optionPanel.add(cmboxPadding);
        optionPanel.add(cmboxCipherDataForm);
        optionPanel.add(btnDecrypt);
        optionPanel.add(btnEncrypt);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class JTextComponentHintListener implements FocusListener {
        private String hintText;
        private JTextComponent textComponent;

        public JTextComponentHintListener(JTextComponent textComponent, String hintText) {
            this.hintText = hintText;
            this.textComponent = textComponent;
            this.textComponent.setForeground(Color.GRAY);
            this.textComponent.setText(hintText);
        }

        @Override
        public void focusGained(FocusEvent e) {
            //获取到焦点时
            String tmpText = this.textComponent.getText();
            if (tmpText.equals(this.hintText)) {
                 this.textComponent.setText("");
                 this.textComponent.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            //失去焦点时
            String tmpText = this.textComponent.getText();
            if ("".equals(tmpText)) {
                this.textComponent.setText(this.hintText);
                this.textComponent.setForeground(Color.GRAY);
            }
        }
    }

}
