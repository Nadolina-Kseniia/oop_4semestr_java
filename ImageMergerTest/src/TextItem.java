import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class TextItem implements DrawItem, Serializable {
    private String text;
    private Font font;
    private Color color;
    private int x, y;

    public TextItem(String text, Font font, Color color, Point position) {
        this.text = text;
        this.font = font;
        this.color = color;
        this.x = position.x;
        this.y = position.y;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void resize(int dw, int dh, ResizeDirection dir) {
        font = font.deriveFont((float) font.getSize2D() + (dw + dh) / 2);
    }

    public boolean contains(Point point) {
        return point.x >= x && point.x <= x + 100 && point.y >= y - 20 && point.y <= y;
    }

    public ResizeDirection getResizeDirection(Point point) {
        return ResizeDirection.NONE;
    }

    public void alignLeft(List<DrawItem> items) {}
    public void alignCenter(List<DrawItem> items) {}
    public void alignRight(List<DrawItem> items) {}
    public void groupWith(List<DrawItem> items) {}

    public double getAspectRatio() {
        return 1.0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return 100; }
    public int getHeight() { return 30; }
}