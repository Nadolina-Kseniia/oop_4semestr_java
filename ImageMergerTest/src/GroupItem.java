import java.awt.*;
import java.io.Serializable;
import java.util.*;

public class GroupItem implements DrawItem, Serializable {
    private List<DrawItem> members = new ArrayList<>();

    public GroupItem() {} // Без параметров

    public void add(DrawItem item) {
        members.add(item);
    }

    @Override
    public void draw(Graphics g) {
        for (DrawItem item : members) {
            item.draw(g);
        }
    }

    @Override
    public void move(int dx, int dy) {
        for (DrawItem item : members) {
            item.move(dx, dy);
        }
    }

    @Override
    public void resize(int dw, int dh, ResizeDirection dir) {
        // Реализация изменения размера группы
    }

    @Override
    public boolean contains(Point point) {
        for (DrawItem item : members) {
            if (item.contains(point)) return true;
        }
        return false;
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
    public int getX() { return 0; }

    @Override
    public int getY() { return 0; }

    @Override
    public int getWidth() { return 100; }

    @Override
    public int getHeight() { return 100; }

    public List<DrawItem> getMembers() {
        return new ArrayList<>(members);
    }
}