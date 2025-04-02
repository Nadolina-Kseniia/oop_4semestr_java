package LR1;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Point {
    double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double distance(Point other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}

public class Main {
    public static void main(String[] args) {
        int n = 0;
        Scanner scanner = new Scanner(System.in);

        // Ввод числа точек
        while (true) {
            System.out.println("Введите количество точек:");
            try {
                n = scanner.nextInt();
                if (n >= 3) break; // Минимум 3 точки для треугольника
                System.out.println("Количество точек должно быть не менее 3.");
            } catch (InputMismatchException e) {
                System.out.println("Вы ввели не число. Пожалуйста, попробуйте снова.");
                scanner.next(); // Очистка некорректного ввода
            }
        }

        // Создание списка точек
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Введите координаты точки " + (i + 1) + " (x y):");
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            points.add(new Point(x, y));
        }

        // Поиск треугольника с максимальным периметром
        double maxPerimeter = 0;
        Point[] bestTriangle = new Point[3];

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                for (int k = j + 1; k < points.size(); k++) {
                    Point p1 = points.get(i);
                    Point p2 = points.get(j);
                    Point p3 = points.get(k);

                    // Вычисление периметра треугольника
                    double perimeter = p1.distance(p2) + p2.distance(p3) + p3.distance(p1);

                    // Проверка, является ли текущий периметр максимальным
                    if (perimeter > maxPerimeter) {
                        maxPerimeter = perimeter;
                        bestTriangle[0] = p1;
                        bestTriangle[1] = p2;
                        bestTriangle[2] = p3;
                    }
                }
            }
        }

        // Вывод результата
        System.out.println("Треугольник с наибольшим периметром:");
        System.out.printf("A(%.2f, %.2f)\n", bestTriangle[0].x, bestTriangle[0].y);
        System.out.printf("B(%.2f, %.2f)\n", bestTriangle[1].x, bestTriangle[1].y);
        System.out.printf("C(%.2f, %.2f)\n", bestTriangle[2].x, bestTriangle[2].y);
        System.out.printf("Периметр: %.2f\n", maxPerimeter);

        scanner.close(); // Закрытие сканера
    }
}