import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.*;
import java.util.Stack;
import java.util.ArrayList;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class ImagePanel extends JPanel {
    private BufferedImage background;
    private List<DrawItem> items = new ArrayList<>();
    private DrawItem selectedItem;
    private DrawItem resizingItem;
    private Point lastMousePoint;
    private ResizeDirection resizeDirection;
    private transient Stack<List<DrawItem>> undoStack = new Stack<>();
    private transient Stack<List<DrawItem>> redoStack = new Stack<>();

    private transient Stack<List<ImageItem>> undoStack = new Stack<>();
    private transient Stack<List<ImageItem>> redoStack = new Stack<>();

    public void pasteImageFromClipboard() {
        try {
            Transferable clipboardContent = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (clipboardContent != null && clipboardContent.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                BufferedImage image = (BufferedImage) clipboardContent.getTransferData(DataFlavor.imageFlavor);
                if (image != null) {
                    Point center = new Point(getWidth() / 2, getHeight() / 2);
                    addImage(image, center);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("autosave.dat"))) {
            out.writeObject(items);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadState() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("autosave.dat"))) {
            items = (List<ImageItem>) in.readObject();
            repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(new ArrayList<>(items));
            items.clear();
            items.addAll(undoStack.pop());
            repaint();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(new ArrayList<>(items));
            items.clear();
            items.addAll(redoStack.pop());
            repaint();
        }
    }

    private void initMouseListeners() {
        MouseAdapter listener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    selectImage(e.getPoint());
                    resizeDirection = ImageItem.ResizeDirection.NONE;
                    if (selectedImage != null) {
                        resizeDirection = selectedImage.getResizeDirection(e.getPoint());
                        if (resizeDirection != ImageItem.ResizeDirection.NONE) {
                            resizingItem = selectedImage;
                        }
                    }
                    lastMousePoint = e.getPoint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                resizingItem = null;
                selectedImage = null;
                resizeDirection = ImageItem.ResizeDirection.NONE;
            }

            public boolean hasBackground() {
                return background != null;
            }

            public boolean hasItems() {
                return !items.isEmpty();
            }

            public void mouseDragged(MouseEvent e) {
                if (resizingItem != null) {
                    handleResize(e.getPoint());
                    repaint();
                } else if (selectedImage != null) {
                    Point current = e.getPoint();
                    int dx = current.x - lastMousePoint.x;
                    int dy = current.y - lastMousePoint.y;
                    selectedImage.move(dx, dy);
                    lastMousePoint = current;
                    repaint();
                }
            }
        };
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    public ImagePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        initMouseListeners();
    }

    private List<DrawItem> copyItems() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(items);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (List<DrawItem>) in.readObject();
        } catch (Exception ex) {
            return new ArrayList<>(items);
        }
    }

    public void setKeepAspectRatio(boolean keep) { /* реализация */ }

    public DrawItem getSelectedItem() {
        return selectedItem;
    }

    public void setBackgroundImage(BufferedImage background) {
        this.background = convertToPNG(background);
        items.clear();
        revalidate();
        repaint();
    }

    public void addImage(BufferedImage image, Point position) {
        items.add(new ImageItem(convertToPNG(image), position));
        repaint();
    }

    public void clearImages() {
        items.clear();
        repaint();
    }

    public BufferedImage getMergedImage() {
        if (background == null) return null;
        BufferedImage result = new BufferedImage(
                background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = result.createGraphics();
        g.drawImage(background, 0, 0, null);
        for (DrawItem item : items) {
            item.draw(g);
        }
        g.dispose();
        return result;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            int x = (getWidth() - background.getWidth()) / 2;
            int y = (getHeight() - background.getHeight()) / 2;
            g.drawImage(background, x, y, this);
        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        for (DrawItem item : items) {
            item.draw(g);
            if (selectedItem == item) {
                drawResizeHandles(g, item);
            }
        }
    }

    private void drawResizeHandles(Graphics g, DrawItem item) {
        int x = item.getX();
        int y = item.getY();
        int width = item.getWidth();
        int height = item.getHeight();
        g.setColor(Color.RED);
        for (int i = 0; i < 8; i++) {
            int hx = switch (i) {
                case 0, 1, 2 -> x - ImageItem.HANDLE_SIZE / 2;
                case 3, 6 -> x + width / 2 - ImageItem.HANDLE_SIZE / 2;
                case 4, 5, 7 -> x + width - ImageItem.HANDLE_SIZE / 2;
                default -> x;
            };
            int hy = switch (i) {
                case 0, 3, 4 -> y - ImageItem.HANDLE_SIZE / 2;
                case 1, 6 -> y + height / 2 - ImageItem.HANDLE_SIZE / 2;
                case 2, 5, 7 -> y + height - ImageItem.HANDLE_SIZE / 2;
                default -> y;
            };
            g.fillRect(hx, hy, ImageItem.HANDLE_SIZE, ImageItem.HANDLE_SIZE);
        }
    }

    private BufferedImage convertToPNG(BufferedImage img) {
        BufferedImage png = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = png.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return png;
    }

    public void autoSave() {
        javax.swing.Timer timer = new javax.swing.Timer(5000, e -> saveState());
        timer.start();
    }

    public void saveState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("autosave.dat"))) {
            out.writeObject(items);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadState() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("autosave.dat"))) {
            items = (List<DrawItem>) in.readObject();
            repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void groupSelectedItems() {
        if (selectedItem != null) {
            items.add(new GroupItem(selectedItem));
            repaint();
        }
    }

    public void ungroupSelectedItem() {
        if (selectedItem instanceof GroupItem group) {
            items.remove(group);
            items.addAll(group.getMembers());
            repaint();
        }
    }

    public void alignLeft() {
        if (selectedItem != null) {
            selectedItem.alignLeft(items);
            repaint();
        }
    }

    public void alignCenter() {
        if (selectedItem != null) {
            selectedItem.alignCenter(items);
            repaint();
        }
    }

    public void alignRight() {
        if (selectedItem != null) {
            selectedItem.alignRight(items);
            repaint();
        }
    }
}