import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter out;
    private String userName;

    public ChatClient() {
        // Ввод имени пользователя
        userName = JOptionPane.showInputDialog("Введите ваше имя:");

        frame = new JFrame("Interface of Chat");
        textArea = new JTextArea(20, 30);
        textField = new JTextField(30);

        // Настройки области для вывода сообщений
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        // Настройка основного окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);

        // Обработка ввода сообщения
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.pack();
        frame.setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Отправка имени пользователя на сервер
            out.println(userName + " присоединился к чату.");

            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        textArea.append(message + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength()); // Прокрутка вниз
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.trim().isEmpty()) {
            out.println(userName + ": " + message); // Добавление имени к сообщению
            textField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}