import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

public interface DrawItem {
    void draw(Graphics g);
    void move(int dx, int dy);
    void resize(int dw, int dh, ResizeDirection direction); // Убрано ImageItem. перед ResizeDirection
    boolean contains(Point point);
    ResizeDirection getResizeDirection(Point point); // То же самое
    void alignLeft(List<DrawItem> items);
    void alignCenter(List<DrawItem> items);
    void alignRight(List<DrawItem> items);
    void groupWith(List<DrawItem> items);
    double getAspectRatio();
    int getX();
    int getY();
    int getWidth();
    int getHeight();
}