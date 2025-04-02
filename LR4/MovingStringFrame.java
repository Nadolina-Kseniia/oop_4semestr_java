package LR4;

import javax.swing.*;
import java.awt.*;

public class MovingStringFrame extends JFrame {
    private String text = "Двигающаяся строка";
    private int x = 0; // Начальная позиция строки
    private int direction = 1; // Направление движения
    private Color currentColor = Color.BLACK; // Начальный цвет строки

    public MovingStringFrame() {
        setTitle("Двигающаяся строка");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создаем панель для рисования
        DrawingPanel drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        // Создаем выпадающий список для выбора цвета
        JComboBox<Color> colorComboBox = new JComboBox<>(new Color[]{
                Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA
        });

        colorComboBox.addActionListener(e -> currentColor = (Color) colorComboBox.getSelectedItem());
        add(colorComboBox, BorderLayout.NORTH);

        // Запускаем анимацию
        new Timer(10, e -> {
            moveString();
            drawingPanel.repaint(); // Перерисовываем панель
        }).start();
    }

    private void moveString() {
        x += direction;
        if (x + getFontMetrics(getFont()).stringWidth(text) >= getWidth() || x < 0) {
            direction *= -1; // Меняем направление
        }
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(currentColor);
            g.drawString(text, x, getHeight() / 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovingStringFrame frame = new MovingStringFrame();
            frame.setVisible(true);
        });
    }
}