package LR5;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client21 {

    private static JFrame frame; // Теперь frame - поле класса
    private static JTextArea textArea;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Socket socket;

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 8030;

        SwingUtilities.invokeLater(() -> createAndShowGUI());

        try {
            connectToServer(serverAddress, serverPort);
            receiveMessages(); // Запускаем поток приема сообщений
        } catch (IOException e) {
            textArea.append("Ошибка подключения к серверу: " + e.getMessage() + "\n");
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Игра 21");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText("Добро пожаловать в игру 21! Подключаемся к серверу...\n");

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 20, 550, 200);
        frame.add(scrollPane);

        JButton hitButton = new JButton("Взять карту");
        hitButton.setBounds(100, 250, 150, 40);
        hitButton.addActionListener(e -> sendCommand("взять"));
        frame.add(hitButton);

        JButton stayButton = new JButton("Остаться");
        stayButton.setBounds(300, 250, 150, 40);
        stayButton.addActionListener(e -> sendCommand("стоп"));
        frame.add(stayButton);

        frame.setLayout(null);
        frame.setVisible(true);
    }

    private static void connectToServer(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    final String messageCopy = serverMessage;
                    SwingUtilities.invokeLater(() -> textArea.append("Сервер: " + messageCopy + "\n"));

                    if (serverMessage.contains("Игра завершена")) {
                        SwingUtilities.invokeLater(() -> textArea.append("Вы были отключены от сервера. Перезапустите клиент, чтобы начать новую игру.\n"));
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> textArea.append("Другой игрок отключился. Вы победили!\n"));
            }
        }).start();
    }

    private static void receiveMessages() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                String finalServerMessage = serverMessage; // Для лямбда-выражения
                SwingUtilities.invokeLater(() -> textArea.append("Сервер: " + finalServerMessage + "\n"));

                if (serverMessage.contains("Игра завершена")) {
                    handleGameEnd(finalServerMessage);
                    break;
                } else if (serverMessage.contains("Другой игрок отключился. Вы победили!")) {
                    handleOpponentDisconnect();
                    break;
                }
            }
        } catch (IOException e) {
            return;
        }
    }

    private static void disconnectFromServer() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            System.out.println("Отключено от сервера.");
        } catch (IOException e) {
            System.err.println("Ошибка при отключении от сервера: " + e.getMessage());
        }
    }


    private static void handleGameEnd(String message) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(message + "\n");
            disableButtons();
        });
        disconnectFromServer();
    }

    private static void handleOpponentDisconnect() {
        SwingUtilities.invokeLater(() -> {
            textArea.append("Другой игрок отключился. Вы победили!\n");
            disableButtons();
        });
        disconnectFromServer();
    }

    private static void disableButtons() {
        frame.getContentPane().getComponent(1).setEnabled(false); // Убедитесь, что индексы компонентов верны
        frame.getContentPane().getComponent(2).setEnabled(false);
    }

    private static void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }
}