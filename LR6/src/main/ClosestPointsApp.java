package main;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static spark.Spark.*;

public class ClosestPointsApp {

    private static final String DATA_FILE = "points.txt";

    public static void main(String[] args) throws IOException {

        staticFiles.location("/public"); // Путь относительно resources

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return render(model, "index.mustache");
        });

        post("/find", (req, res) -> {
            try {
                // ... (parsing parameters, finding closest points, same as before)
            } catch (NumberFormatException e) {
                halt(400, "Invalid input: " + e.getMessage());
                return null;
            } // Не нужно ловить IOException здесь, он обрабатывается в readPointsFromFile
        });

        port(4567);

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
