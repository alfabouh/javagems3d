import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

public class RSAApp extends JFrame {
    private RSA rsa = new RSA();

    private JTextArea inputArea = new JTextArea(5, 40);
    private JTextArea outputArea = new JTextArea(5, 40);
    private JTextArea keysArea = new JTextArea(5, 40);

    public RSAApp() {
        setTitle("RSA Cryptosystem");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        panel.add(new JScrollPane(inputArea));
        panel.add(new JScrollPane(outputArea));
        panel.add(new JScrollPane(keysArea));

        add(panel, BorderLayout.CENTER);

        JButton encryptBtn = new JButton("Encrypt");
        JButton decryptBtn = new JButton("Decrypt");
        JButton generateBtn = new JButton("Generate Keys");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encryptBtn);
        buttonPanel.add(decryptBtn);
        buttonPanel.add(generateBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        generateBtn.addActionListener(e -> {
            rsa.generateKeys();
            keysArea.setText(
                    "Public Key (e, n):\n" + rsa.getE() + "\n" + rsa.getN() +
                    "\n\nPrivate Key (d, n):\n" + rsa.getD() + "\n" + rsa.getN()
            );
        });

        encryptBtn.addActionListener(e -> {
            try {
                BigInteger message = textToBigInteger(inputArea.getText());
                BigInteger cipher = rsa.encrypt(message);
                outputArea.setText(cipher.toString());
            } catch (Exception ex) {
                outputArea.setText("Ошибка ввода: используйте текст или числа");
            }
        });

        decryptBtn.addActionListener(e -> {
            try {
                BigInteger cipher = new BigInteger(inputArea.getText());
                String message = bigIntegerToText(rsa.decrypt(cipher));
                outputArea.setText(message);
            } catch (Exception ex) {
                outputArea.setText("Ошибка ввода: неверный формат шифртекста");
            }
        });
    }
    private BigInteger textToBigInteger(String text) {
        return new BigInteger(text.getBytes());
    }

    private String bigIntegerToText(BigInteger number) {
        return new String(number.toByteArray());
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RSAApp().setVisible(true));
    }
}