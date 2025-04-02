package LR5;

import java.io.*;
import java.net.*;
import java.util.*;

class Server21 {
    private static final int PORT = 8030;
    private static final Map<String, Integer> CARD_VALUES = new HashMap<>();
    private static final List<String> DECK = new ArrayList<>();
    private static final Random RANDOM = new Random();

    private static final List<PlayerHandler> players = Collections.synchronizedList(new ArrayList<>());
    private static int currentPlayerIndex = 0;

    static {
        CARD_VALUES.put("2", 2);
        CARD_VALUES.put("3", 3);
        CARD_VALUES.put("4", 4);
        CARD_VALUES.put("5", 5);
        CARD_VALUES.put("6", 6);
        CARD_VALUES.put("7", 7);
        CARD_VALUES.put("8", 8);
        CARD_VALUES.put("9", 9);
        CARD_VALUES.put("10", 10);
        CARD_VALUES.put("J", 10);
        CARD_VALUES.put("Q", 10);
        CARD_VALUES.put("K", 10);
        CARD_VALUES.put("A", 11);

        DECK.addAll(CARD_VALUES.keySet());
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Сервер запущен и готов к игре...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket playerSocket = serverSocket.accept();
                System.out.println("Игрок подключился к серверу.");
                PlayerHandler playerHandler = new PlayerHandler(playerSocket, players.size() + 1);
                players.add(playerHandler);
                new Thread(playerHandler).start();

                if (players.size() == 2) {
                    startGame();
                }
            }
        }
    }

    private static void startGame() {
        synchronized (players) {
            for (PlayerHandler player : players) {
                player.sendMessage("Игра началась! Ожидайте своей очереди.");
            }
            players.get(currentPlayerIndex).notifyTurn();
        }
    }


    private static void endGame(String winnerMessage) {
        synchronized (players) {
            if (!players.isEmpty()) { // Проверяем, есть ли еще игроки в списке
                for (PlayerHandler player : players) {
                    player.sendMessage(winnerMessage);
                    player.disconnectPlayer(); // Вызываем disconnectPlayer, чтобы отправить сообщение о завершении игры
                }
                players.clear();
            }
        }
        currentPlayerIndex = 0;
    }

    private static class PlayerHandler implements Runnable {
        private final Socket socket;
        private final PrintWriter out;
        private final BufferedReader in;
        private final List<String> playerCards = new ArrayList<>();
        private final int playerId;
        private boolean isMyTurn = false;
        private boolean hasStopped = false;

        public PlayerHandler(Socket socket, int playerId) throws IOException {
            this.socket = socket;
            this.playerId = playerId;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                sendMessage("Добро пожаловать в игру 21! Вы - Игрок " + playerId + ". Ожидаем второго игрока...");

                while (true) {
                    synchronized (this) {
                        while (!isMyTurn) {
                            wait();
                        }
                    }

                    if (hasStopped) {
                        endTurn();
                        continue;
                    }

                    playTurn();
                    if (!hasStopped) {
                        endTurn();
                    }
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Ошибка при обработке игрока: " + e.getMessage());
            } finally {
                disconnectPlayer();
                players.remove(this);
                System.out.println("Игрок отключен.");
            }
        }

        private void playTurn() throws IOException {
            if (playerCards.isEmpty()) {
                playerCards.add(drawCard());
                sendMessage("Ваша первая карта: " + playerCards.get(0));
            }

            sendMessage("Ваши карты: " + playerCards);
            sendMessage("Ваш текущий счет: " + calculateScore(playerCards));
            sendMessage("Нажмите: 'взять', чтобы взять карту, или 'стоп', чтобы закончить.");

            // Проверка на отключение второго игрока сразу после отправки сообщений
            if (players.size() == 1) {
                sendMessage("Второй игрок отключился. Первый игрок победил!");
                return; // Завершение хода, если второй игрок отключен
            }

            String response = in.readLine();
            if ("взять".equalsIgnoreCase(response)) {
                String card = drawCard();
                playerCards.add(card);
                int score = calculateScore(playerCards);
                sendMessage("Вы взяли карту: " + card);
                sendMessage("Ваши карты: " + playerCards);
                sendMessage("Ваш текущий счет: " + score);

                if (score > 21) {
                    sendMessage("Перебор! Ваш счет: " + score + ". Вы проиграли.");
                    endGame("Игрок " + playerId + " перебрал. Победитель: Игрок " + getOtherPlayerId() + "!");
                }
            } else if ("стоп".equalsIgnoreCase(response)) {
                hasStopped = true;
                sendMessage("Вы закончили игру с итоговым счетом: " + calculateScore(playerCards) + ". Ожидайте окончания игры.");
                checkGameOver();
            }
        }

        private void endTurn() {
            synchronized (players) {
                isMyTurn = false;
                if (players.size() > 1) { // Проверяем количество игроков
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    PlayerHandler nextPlayer = players.get(currentPlayerIndex);
                    if (!nextPlayer.hasStopped) { // Проверяем, не отключился ли следующий игрок
                        nextPlayer.notifyTurn();
                    } else {
                        // Если следующий игрок отключился, нужно найти следующего активного игрока и передать ход ему
                        findNextActivePlayerAndNotify();
                    }


                } else if (players.size() == 1 && !hasStopped) {
                    // Если остался только один игрок и это текущий игрок, он объявляется победителем
                    sendMessage("Вы победили! Другой игрок отключился.");
                    endGame("Игрок " + getOtherPlayerId() + " отключился. Победитель: Игрок " + playerId + "!");


                }
            }
        }

        private void findNextActivePlayerAndNotify() {
            synchronized (players) {
                int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
                while(players.get(nextPlayerIndex).hasStopped && players.size() > 1){
                    nextPlayerIndex = (nextPlayerIndex + 1) % players.size();
                }
                if(players.size() > 1) {
                    players.get(nextPlayerIndex).notifyTurn();
                } else if (players.size() == 1 && !hasStopped) {
                    sendMessage("Вы победили! Другой игрок отключился.");
                    endGame("Игрок " + getOtherPlayerId() + " отключился. Победитель: Игрок " + playerId + "!");
                }
            }
        }

        private synchronized void notifyTurn() {
            if (!hasStopped) {
                isMyTurn = true;
                sendMessage("Ваш ход!");
                notify();
            }
        }

        private void sendMessage(String message) {
            out.println(message);
        }

        private void disconnectPlayer() {
            try {
                if (!socket.isClosed()) { // Проверяем, закрыт ли уже сокет
                    sendMessage("Игра завершена. Вы будете отключены.");

                    try (OutputStream out = socket.getOutputStream()) { // try-with-resources для автоматического закрытия OutputStream
                        out.flush(); // Очищаем буфер вывода перед закрытием сокета
                    } catch (IOException ex) {
                        // Игнорируем исключение при очистке - сокет все равно будет закрыт
                    }

                    socket.close();
                    // Уведомляем другого игрока об отключении
                    synchronized (players) {
                        for (PlayerHandler otherPlayer : players) {
                            if (otherPlayer != this) {
                                otherPlayer.sendMessage("Вы победили! Другой игрок отключился");
                                otherPlayer.hasStopped = true; // Заканчиваем игру для другого игрока
                                otherPlayer.notifyTurn(); // Чтобы другой игрок обработал сообщение и завершил игру.
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при отключении игрока: " + e.getMessage()); // Более информативное сообщение об ошибке
            }
        }


        private String drawCard() {
            return DECK.get(RANDOM.nextInt(DECK.size()));
        }

        private int calculateScore(List<String> cards) {
            int score = 0;
            int aceCount = 0;

            for (String card : cards) {
                score += CARD_VALUES.get(card);
                if ("A".equals(card)) {
                    aceCount++;
                }
            }

            while (score > 21 && aceCount > 0) {
                score -= 10;
                aceCount--;
            }

            return score;
        }

        private void checkGameOver() {
            synchronized (players) {
                boolean allStopped = players.stream().allMatch(player -> player.hasStopped);
                if (allStopped) {
                    PlayerHandler player1 = players.get(0);
                    PlayerHandler player2 = players.get(1);

                    int score1 = player1.calculateScore(player1.playerCards);
                    int score2 = player2.calculateScore(player2.playerCards);

                    String winnerMessage;

                    if (score1 > 21 && score2 > 21) {
                        winnerMessage = "Игра окончена! Ничья. Оба игрока превысили 21.";
                    } else if (score1 > 21) {
                        winnerMessage = "Игра окончена! Игрок 2 победил. Игрок 1 превысил 21.";
                    } else if (score2 > 21) {
                        winnerMessage = "Игра окончена! Игрок 1 победил. Игрок 2 превысил 21.";
                    } else if (score1 > score2) {
                        winnerMessage = "Игра окончена! Игрок 1 победил со счетом " + score1 + " против " + score2;
                    } else if (score2 > score1) {
                        winnerMessage = "Игра окончена! Игрок 2 победил со счетом " + score2 + " против " + score1;
                    } else {
                        winnerMessage = "Игра окончена! Ничья со счетом " + score1;
                    }

                    for (PlayerHandler player : players) {
                        player.sendMessage(winnerMessage);
                    }

                    endGame("Игра завершена.");
                }
            }
        }


        private int getOtherPlayerId() {
            return 3 - playerId; // Более элегантный способ получить ID другого игрока
        }
    }
}