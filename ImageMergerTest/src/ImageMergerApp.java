import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageMergerApp extends JFrame {
    private ImagePanel imagePanel;
    private JButton mergeButton;
    private JButton saveButton;
    private JButton clearButton;
    private JButton addButton;
    private JButton setBackgroundButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton pasteButton;
    private BufferedImage mergedImage;

    public ImageMergerApp() {
        setTitle("Image Merger Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar();

        setBackgroundButton = new JButton("Set Background");
        addButton = new JButton("Add Images");
        mergeButton = new JButton("Merge Images");
        saveButton = new JButton("Save Result");
        clearButton = new JButton("Clear All");
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");
        pasteButton = new JButton("Paste");

        mergeButton.setEnabled(false);
        saveButton.setEnabled(false);
        clearButton.setEnabled(false);
        addButton.setEnabled(false);

        toolBar.add(setBackgroundButton);
        toolBar.add(addButton);
        toolBar.add(mergeButton);
        toolBar.add(saveButton);
        toolBar.add(clearButton);
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBar.add(pasteButton);

        imagePanel = new ImagePanel();
        JScrollPane scrollPane = new JScrollPane(imagePanel);

        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        setBackgroundButton.addActionListener(this::setBackground);
        addButton.addActionListener(this::addImages);
        mergeButton.addActionListener(this::mergeImages);
        saveButton.addActionListener(this::saveImage);
        clearButton.addActionListener(this::clearAll);
        undoButton.addActionListener(e -> imagePanel.undo());
        redoButton.addActionListener(e -> imagePanel.redo());
        pasteButton.addActionListener(e -> imagePanel.pasteImageFromClipboard());
    }

    private void setBackground(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg", "gif", "bmp"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage background = ImageIO.read(fileChooser.getSelectedFile());
                imagePanel.setBackgroundImage(background);
                updateButtonsState();
            } catch (IOException ex) {
                showError("Error loading background image", ex);
            }
        }
    }

    private void addImages(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg", "gif", "bmp"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    if (image != null) {
                        int x = (imagePanel.getWidth() - image.getWidth()) / 2;
                        int y = (imagePanel.getHeight() - image.getHeight()) / 2;
                        imagePanel.addImage(image, new Point(x, y));
                    }
                } catch (IOException ex) {
                    showError("Error loading image: " + file.getName(), ex);
                }
            }
            updateButtonsState();
        }
    }

    private void mergeImages(ActionEvent e) {
        mergedImage = imagePanel.getMergedImage();
        if (mergedImage != null) {
            showMergedResult();
            saveButton.setEnabled(true);
        } else {
            showError("No background image set", null);
        }
    }

    private void showMergedResult() {
        JDialog dialog = new JDialog(this, "Merged Result", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        JLabel label = new JLabel(new ImageIcon(mergedImage));
        JScrollPane scroll = new JScrollPane(label);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private void saveImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = ensureFileExtension(fileChooser.getSelectedFile(), "png");
            if (file.exists() && !confirmOverwrite(file)) return;
            try {
                ImageIO.write(mergedImage, "png", file);
                JOptionPane.showMessageDialog(this,
                        "Image saved successfully as PNG!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Error saving image", ex);
            }
        }
    }

    private File ensureFileExtension(File file, String extension) {
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith("." + extension)) {
            return new File(path + "." + extension);
        }
        return file;
    }

    private boolean confirmOverwrite(File file) {
        int response = JOptionPane.showConfirmDialog(this,
                "File already exists. Overwrite?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION);
        return response == JOptionPane.YES_OPTION;
    }

    private void clearAll(ActionEvent e) {
        imagePanel.clearImages();
        mergedImage = null;
        updateButtonsState();
    }

    private void updateButtonsState() {
        boolean hasBackground = imagePanel.hasBackground();
        boolean hasItems = imagePanel.hasItems();
        mergeButton.setEnabled(hasBackground && hasItems);
        clearButton.setEnabled(hasBackground || mergedImage != null);
        saveButton.setEnabled(mergedImage != null);
        addButton.setEnabled(hasBackground);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this,
                message + (ex != null ? ": " + ex.getMessage() : ""),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageMergerApp().setVisible(true));
    }
}