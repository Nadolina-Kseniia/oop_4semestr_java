public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Метод для вычисления расстояния от точки до прямой (Ax + By + C = 0)
    public double distanceToLine(double A, double B, double C) {
        return Math.abs(A * x + B * y + C) / Math.sqrt(A * A + B * B);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
