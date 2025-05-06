import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;
import java.awt.image.AffineTransformOp;

public class ImageItem implements DrawItem, Serializable {
    public static final int HANDLE_SIZE = 8;
    private BufferedImage image;
    private int x, y;
    private double rotation = 0;
    private float alpha = 1.0f;

    public ImageItem(BufferedImage image, Point position) {
        this.image = image;
        this.x = position.x;
        this.y = position.y;
    }

    @Override
    public void draw(Graphics g) {
        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(rotation), x + image.getWidth() / 2, y + image.getHeight() / 2);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawImage(image, transform, null);
        g2d.dispose();
    }

    @Override
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    @Override
    public void resize(int dw, int dh, ResizeDirection dir) {
        int newWidth = image.getWidth() + dw;
        int newHeight = image.getHeight() + dh;
        if (newWidth > 10 && newHeight > 10) {
            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
            g.dispose();
            image = resized;
        }
    }

    @Override
    public boolean contains(Point point) {
        return point.x >= x && point.x <= x + image.getWidth()
                && point.y >= y && point.y <= y + image.getHeight();
    }

    @Override
    public ResizeDirection getResizeDirection(Point point) {
        Rectangle bounds = new Rectangle(x, y, image.getWidth(), image.getHeight());
        if (new Rectangle(x - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(point)) return ResizeDirection.NORTH_WEST;
        if (new Rectangle(x + image.getWidth() - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(point)) return ResizeDirection.NORTH_EAST;
        if (new Rectangle(x - HANDLE_SIZE / 2, y + image.getHeight() - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(point)) return ResizeDirection.SOUTH_WEST;
        if (new Rectangle(x + image.getWidth() - HANDLE_SIZE / 2, y + image.getHeight() - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(point)) return ResizeDirection.SOUTH_EAST;
        if (new Rectangle(x, y - HANDLE_SIZE / 2, image.getWidth(), HANDLE_SIZE).contains(point)) return ResizeDirection.NORTH;
        if (new Rectangle(x, y + image.getHeight() - HANDLE_SIZE / 2, image.getWidth(), HANDLE_SIZE).contains(point)) return ResizeDirection.SOUTH;
        if (new Rectangle(x - HANDLE_SIZE / 2, y, HANDLE_SIZE, image.getHeight()).contains(point)) return ResizeDirection.WEST;
        if (new Rectangle(x + image.getWidth() - HANDLE_SIZE / 2, y, HANDLE_SIZE, image.getHeight()).contains(point)) return ResizeDirection.EAST;
        return ResizeDirection.NONE;
    }

    public void rotate(double degrees) {
        rotation = degrees;
    }

    public void flipHorizontal() {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);
    }

    public void flipVertical() {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public double getAspectRatio() {
        return (double) image.getHeight() / image.getWidth();
    }


    public void alignLeft(List<DrawItem> items) {
        for (DrawItem item : items) {
            item.move(this.x - item.getX(), 0);
        }
    }

    public void alignCenter(List<DrawItem> items) {
        int centerX = this.x + this.image.getWidth() / 2;
        for (DrawItem item : items) {
            int dx = centerX - item.getX() - item.getWidth() / 2;
            item.move(dx, 0);
        }
    }

    public void alignRight(List<DrawItem> items) {
        int rightX = this.x + this.image.getWidth();
        for (DrawItem item : items) {
            int dx = rightX - item.getX() - item.getWidth();
            item.move(dx, 0);
        }
    }

    public void groupWith(List<DrawItem> items) {
        // Логика группировки
    }


    public int getX() { return x; }


    public int getY() { return y; }


    public int getWidth() { return image.getWidth(); }



    public int getHeight() { return image.getHeight(); }
}