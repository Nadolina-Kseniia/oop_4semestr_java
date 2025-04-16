package Web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class IndexHandler implements HttpHandler {

    private static final String TEXT_FILE = "C:/oop_4semestr/Web/text.txt";
    private static final String INDEX_FILE = "C:/oop_4semestr/Web/index.html";
    private static final String CHARSET = "UTF-8";
    private static final Random random = new Random();


    private static final String[] FONTS = {"Arial", "Verdana", "Times New Roman", "Courier New", "Courier"};
    private static final String[] COLORS = {"red", "green", "blue", "purple", "orange"};

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Параметры по умолчанию
        int fontSize = 14;
        int lines = 3;

        // Получение параметров запроса
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] parts = param.split("=");
                if (parts.length == 2) {
                    String name = parts[0];
                    String value = parts[1];
                    try {
                        String decodedValue = URLDecoder.decode(value, CHARSET);
                        if ("fontSize".equals(name)) {
                            fontSize = Integer.parseInt(decodedValue);
                        } else if ("lines".equals(name)) {
                            lines = Integer.parseInt(decodedValue);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение параметра: " + name);
                    }
                }
            }
        }

        // Чтение текста из файла
        List<String> textLines = readTextFromFile(TEXT_FILE);

        // Генерация случайного шрифта и цвета
        String randomFont = FONTS[random.nextInt(FONTS.length)];
        String randomColor = COLORS[random.nextInt(COLORS.length)];

        // Генерация HTML-кода с текстом
        StringBuilder generatedText = new StringBuilder();
        generatedText.append("<div style='text-align: left;'>");
        for (int i = 0; i < lines && i < textLines.size(); i++) {
            String line = textLines.get(i);
            generatedText.append("<p style='font-size: ")
                    .append(fontSize)
                    .append("px; font-family: ")
                    .append(randomFont)
                    .append("; color: ")
                    .append(randomColor)
                    .append(";'>")
                    .append(line)
                    .append("</p>");
        }
        generatedText.append("</div>");

        // Чтение содержимого index.html
        File indexFile = new File(INDEX_FILE);
        FileInputStream fis = new FileInputStream(indexFile);
        byte[] data = fis.readAllBytes();
        fis.close();
        String indexContent = new String(data, CHARSET);

        // Вставка сгенерированного текста в index.html
        String response = indexContent.replace("<div id=\"generatedText\">", "<div id=\"generatedText\">" + generatedText);

        // Отправка ответа клиенту
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=" + CHARSET);
        exchange.sendResponseHeaders(200, response.getBytes(CHARSET).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(CHARSET));
        os.close();
    }

    private List<String> readTextFromFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), CHARSET))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
