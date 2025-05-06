import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class ShapeItem implements DrawItem, Serializable {
    public enum ShapeType { RECTANGLE, OVAL, LINE }
    private ShapeType type;
    private Point start, end;
    private Color color;

    public ShapeItem(ShapeType type, Point start, Point end, Color color) {
        this.type = type;
        this.start = new Point(start);
        this.end = new Point(end);
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        switch (type) {
            case RECTANGLE -> g.drawRect(start.x, start.y, end.x - start.x, end.y - start.y);
            case OVAL -> g.drawOval(start.x, start.y, end.x - start.x, end.y - start.y);
            case LINE -> g.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    @Override
    public void move(int dx, int dy) {
        start.translate(dx, dy);
        end.translate(dx, dy);
    }

    @Override
    public void resize(int dw, int dh, ResizeDirection dir) {
        // Реализация изменения размера фигуры
    }

    @Override
    public boolean contains(Point point) {
        return new Rectangle(start, new Dimension(end.x - start.x, end.y - start.y)).contains(point);
    }

    @Override
    public ResizeDirection getResizeDirection(Point point) {
        return ResizeDirection.NONE;
    }

    @Override
    public void alignLeft(List<DrawItem> items) {}

    @Override
    public void alignCenter(List<DrawItem> items) {}

    @Override
    public void alignRight(List<DrawItem> items) {}

    @Override
    public void groupWith(List<DrawItem> items) {}

    @Override
    public double getAspectRatio() {
        return 1.0;
    }

    @Override
    public int getX() { return start.x; }

    @Override
    public int getY() { return start.y; }

    @Override
    public int getWidth() { return end.x - start.x; }

    @Override
    public int getHeight() { return end.y - start.y; }
}