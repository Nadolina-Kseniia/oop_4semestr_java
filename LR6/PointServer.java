import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PointServer {
    private static final String POINTS_FILE = "points.txt";
    private static final int PORT = 8081; // Фиксированный порт
    private static List<Point> points = new ArrayList<>();
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        // Добавляем обработчик завершения работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Завершение работы сервера...");
            if (server != null) {
                server.stop(0);
            }
        }));

        loadPointsFromFile();

        // Создаем сервер на фиксированном порту
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Обработчик главной страницы
        server.createContext("/", exchange -> {
            try {
                String html = readHtmlFile("index.html");
                exchange.sendResponseHeaders(200, html.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.getBytes());
                os.close();
            } catch (IOException e) {
                sendErrorResponse(exchange, 500, "Ошибка чтения HTML файла: " + e.getMessage());
            }
        });

        // Обработчик обработки формы
        server.createContext("/process", exchange -> {
            try {
                // Получаем параметры
                String query = exchange.getRequestURI().getQuery();
                String[] params = query.split("&");

                double A = Double.parseDouble(params[0].split("=")[1]);
                double B = Double.parseDouble(params[1].split("=")[1]);
                double C = Double.parseDouble(params[2].split("=")[1]);
                double maxDistance = Double.parseDouble(params[3].split("=")[1]);

                // Обработка данных
                List<Point> result = findPointsNearLine(A, B, C, maxDistance);

                // Формируем ответ
                StringBuilder response = new StringBuilder();
                response.append("<html><body><h2>Результат:</h2><ul>");
                for (Point p : result) {
                    response.append("<li>").append(p.toString()).append("</li>");
                }
                response.append("</ul><a href='/'>Назад</a></body></html>");

                // Правильная отправка ответа
                byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } catch (Exception e) {
                String error = "Ошибка: " + e.getMessage();
                byte[] errorBytes = error.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, errorBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorBytes);
                }
            }
        });

        server.start();
        System.out.println("Сервер запущен на порту " + PORT);
        System.out.println("Для остановки нажмите Ctrl+C");
    }

    private static void sendErrorResponse(com.sun.net.httpserver.HttpExchange exchange,
                                          int code, String message) throws IOException {
        String response = "<html><body><h1>Ошибка " + code + "</h1><p>" + message + "</p></body></html>";
        exchange.sendResponseHeaders(code, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void loadPointsFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(POINTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] coords = line.trim().split("\\s+");
                if (coords.length == 2) {
                    try {
                        double x = Double.parseDouble(coords[0]);
                        double y = Double.parseDouble(coords[1]);
                        points.add(new Point(x, y));
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка формата данных в файле: " + line);
                    }
                }
            }
        }
    }

    private static List<Point> findPointsNearLine(double A, double B, double C, double maxDistance) {
        List<Point> result = new ArrayList<>();
        for (Point p : points) {
            double distance = p.distanceToLine(A, B, C);
            if (distance <= maxDistance) {
                result.add(p);
            }
        }
        return result;
    }

    private static String readHtmlFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    static class Point {
        private final double x;
        private final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double distanceToLine(double A, double B, double C) {
            return Math.abs(A * x + B * y + C) / Math.sqrt(A * A + B * B);
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }
}
