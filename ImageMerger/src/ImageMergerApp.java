import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageMergerApp extends JFrame {
    private List<BufferedImage> images = new ArrayList<>();
    private JPanel imagePanel;
    private JScrollPane scrollPane;
    private JButton addButton;
    private JButton mergeButton;
    private JButton saveButton;
    private JButton clearButton;
    private JComboBox<String> layoutComboBox;

    public ImageMergerApp() {
        setTitle("Image Merger Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Toolbar with buttons
        JToolBar toolBar = new JToolBar();
        addButton = new JButton("Add Images");
        mergeButton = new JButton("Merge Images");
        saveButton = new JButton("Save Result");
        clearButton = new JButton("Clear All");

        // Disable buttons initially
        mergeButton.setEnabled(false);
        saveButton.setEnabled(false);
        clearButton.setEnabled(false);

        // Layout options
        String[] layouts = {"Horizontal", "Vertical", "Grid"};
        layoutComboBox = new JComboBox<>(layouts);

        toolBar.add(addButton);
        toolBar.add(mergeButton);
        toolBar.add(saveButton);
        toolBar.add(clearButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("Layout:"));
        toolBar.add(layoutComboBox);

        // Panel for displaying images
        imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(imagePanel);

        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addImages();
            }
        });

        mergeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mergeImages();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }

    private void addImages() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPEG Images", "jpg", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    if (image != null) {
                        images.add(image);
                        addImageToPanel(image, file.getName());
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading image: " + file.getName(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (!images.isEmpty()) {
                mergeButton.setEnabled(true);
                clearButton.setEnabled(true);
            }
        }
    }

    private void addImageToPanel(BufferedImage image, String filename) {
        // Scale image for preview
        int previewWidth = 200;
        int previewHeight = (int) ((double) image.getHeight() / image.getWidth() * previewWidth);

        Image scaledImage = image.getScaledInstance(previewWidth, previewHeight, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);

        JLabel label = new JLabel(icon);
        label.setText(filename);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        imagePanel.add(label);
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void mergeImages() {
        if (images.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No images to merge", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedLayout = (String) layoutComboBox.getSelectedItem();
        BufferedImage mergedImage = null;

        try {
            switch (selectedLayout) {
                case "Horizontal":
                    mergedImage = mergeHorizontally();
                    break;
                case "Vertical":
                    mergedImage = mergeVertically();
                    break;
                case "Grid":
                    mergedImage = mergeInGrid();
                    break;
            }

            if (mergedImage != null) {
                // Display merged image
                imagePanel.removeAll();
                addImageToPanel(mergedImage, "Merged Result");
                saveButton.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error merging images: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BufferedImage mergeHorizontally() {
        // Calculate total width and max height
        int totalWidth = 0;
        int maxHeight = 0;

        for (BufferedImage img : images) {
            totalWidth += img.getWidth();
            if (img.getHeight() > maxHeight) {
                maxHeight = img.getHeight();
            }
        }

        // Create new image
        BufferedImage merged = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = merged.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, totalWidth, maxHeight);

        // Draw images
        int x = 0;
        for (BufferedImage img : images) {
            g.drawImage(img, x, 0, null);
            x += img.getWidth();
        }

        g.dispose();
        return merged;
    }

    private BufferedImage mergeVertically() {
        // Calculate max width and total height
        int maxWidth = 0;
        int totalHeight = 0;

        for (BufferedImage img : images) {
            if (img.getWidth() > maxWidth) {
                maxWidth = img.getWidth();
            }
            totalHeight += img.getHeight();
        }

        // Create new image
        BufferedImage merged = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = merged.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, maxWidth, totalHeight);

        // Draw images
        int y = 0;
        for (BufferedImage img : images) {
            g.drawImage(img, 0, y, null);
            y += img.getHeight();
        }

        g.dispose();
        return merged;
    }

    private BufferedImage mergeInGrid() {
        // Simple grid layout - try to make it as square as possible
        int count = images.size();
        int cols = (int) Math.ceil(Math.sqrt(count));
        int rows = (int) Math.ceil((double) count / cols);

        // Calculate max width and height per cell
        int maxCellWidth = 0;
        int maxCellHeight = 0;

        for (BufferedImage img : images) {
            if (img.getWidth() > maxCellWidth) {
                maxCellWidth = img.getWidth();
            }
            if (img.getHeight() > maxCellHeight) {
                maxCellHeight = img.getHeight();
            }
        }

        // Create new image
        int totalWidth = cols * maxCellWidth;
        int totalHeight = rows * maxCellHeight;
        BufferedImage merged = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = merged.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, totalWidth, totalHeight);

        // Draw images in grid
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (index < images.size()) {
                    BufferedImage img = images.get(index);
                    int x = col * maxCellWidth;
                    int y = row * maxCellHeight;
                    g.drawImage(img, x, y, null);
                    index++;
                }
            }
        }

        g.dispose();
        return merged;
    }

    private void saveImage() {
        if (imagePanel.getComponentCount() == 0) {
            JOptionPane.showMessageDialog(this, "No image to save", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPEG Images", "jpg", "jpeg"));
        fileChooser.setDialogTitle("Save Merged Image");

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Ensure file has .jpg extension
            if (!file.getName().toLowerCase().endsWith(".jpg") && !file.getName().toLowerCase().endsWith(".jpeg")) {
                file = new File(file.getAbsolutePath() + ".jpg");
            }

            try {
                // Get the merged image from the panel
                JLabel label = (JLabel) imagePanel.getComponent(0);
                ImageIcon icon = (ImageIcon) label.getIcon();
                BufferedImage image = new BufferedImage(
                        icon.getIconWidth(),
                        icon.getIconHeight(),
                        BufferedImage.TYPE_INT_RGB);
                Graphics g = image.createGraphics();
                icon.paintIcon(null, g, 0, 0);
                g.dispose();

                ImageIO.write(image, "jpg", file);
                JOptionPane.showMessageDialog(this, "Image saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearAll() {
        images.clear();
        imagePanel.removeAll();
        imagePanel.revalidate();
        imagePanel.repaint();
        mergeButton.setEnabled(false);
        saveButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageMergerApp app = new ImageMergerApp();
                app.setVisible(true);
            }
        });
    }
}