/**package LR6;

import org.apache.catalina.startup.Tomcat;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class ClosestPointsApp {

    private static final String DATA_FILE = "points.txt"; // Файл с точками

    public static void main(String[] args) throws IOException {
        // Создаем вложенную папку public внутри resources, если она не существует
        File publicDir = new File("src/main/resources/public");
        if (!publicDir.exists()) {
            publicDir.mkdirs();
        }



        staticFiles.location("/public"); // Статические файлы (HTML, CSS, JS)

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return render(model, "index.mustache"); // Шаблон главной страницы
        });

        post("/find", (req, res) -> {
            double a = Double.parseDouble(req.queryParams("a"));
            double b = Double.parseDouble(req.queryParams("b"));
            double c = Double.parseDouble(req.queryParams("c"));
            double maxDistance = Double.parseDouble(req.queryParams("maxDistance"));

            List<Point> closestPoints = findClosestPoints(a, b, c, maxDistance);

            Map<String, Object> model = new HashMap<>();
            model.put("points", closestPoints);
            return render(model, "result.mustache"); // Шаблон страницы с результатами
        });


        Tomcat tomcat = new Tomcat();
        tomcat.setPort(4567); // порт приложения
        tomcat.addWebapp("/", new File("src/main/webapp").getAbsolutePath());


        try {
            tomcat.start();
            System.out.println("Приложение запущено на порту " + tomcat.getConnector().getPort());
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }


    }

    // Функция для поиска ближайших точек
    private static List<Point> findClosestPoints(double a, double b, double c, double maxDistance) throws IOException {
        List<Point> points = readPointsFromFile(DATA_FILE);
        List<Point> closestPoints = new ArrayList<>();

        for (Point point : points) {
            double distance = calculateDistance(point, a, b, c);
            if (distance <= maxDistance) {
                closestPoints.add(point);
            }
        }

        return closestPoints;
    }

    // Функция для чтения точек из файла
    private static List<Point> readPointsFromFile(String filename) throws IOException {
        List<Point> points = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));

        for (String line : lines) {
            String[] coordinates = line.split(",");
            double x = Double.parseDouble(coordinates[0].trim());
            double y = Double.parseDouble(coordinates[1].trim());
            points.add(new Point(x, y));
        }

        return points;
    }


    // Функция для вычисления расстояния от точки до прямой
    private static double calculateDistance(Point point, double a, double b, double c) {
        return Math.abs(a * point.x + b * point.y + c) / Math.sqrt(a * a + b * b);
    }



    // Функция для рендеринга Mustache шаблонов
    private static String render(Map<String, Object> model, String templatePath) {
        return new MustacheTemplateEngine().render(
                new ModelAndView(model, templatePath)
        );
    }



}

// Класс для представления точки
class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
 */