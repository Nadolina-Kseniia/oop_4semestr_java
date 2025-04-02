package LR4;

import javax.swing.*;
import java.awt.*;

public class Msf extends JFrame {
    private String text = "Двигающаяся строка"; // текст, который будет двигаться
    private int x = 0; // Начальная позиция строки
    private int direction = 1; // Направление движения
    private Color currentColor = Color.BLACK; // Начальный цвет строки
    private DrawingPanel drawingPanel; // Панель для рисования

    /**
     * Конструктор объекта класса Msf, выполняет инициализацию объекта класса и его компонентов.
     * (drawingPanel = new DrawingPanel())
     */
    public Msf() {
        setTitle("Двигающаяся строка");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создаем панель для рисования
        drawingPanel = new DrawingPanel();
        // add это метод, который добавляет метод drawingPanel
        // (экземпляр класса DrawingPanel, который наследует JPanel) в контейнер JFrame
        add(drawingPanel, BorderLayout.CENTER);

        // Создаем выпадающий список для выбора цвета
        JComboBox<Color> colorComboBox = new JComboBox<>(new Color[]{
                Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA
        });

        colorComboBox.addActionListener(e -> currentColor = (Color) colorComboBox.getSelectedItem());
        // метод для добавления выпадающего списка colorComboBox в верхнюю часть окна
        add(colorComboBox, BorderLayout.NORTH);

        // Запускаем анимацию
        new Timer(10, e -> {
            moveString();
            // Убрала вызов repaint()
        }).start();
    }

    private void moveString() {
        x += direction;
        if (x + getFontMetrics(getFont()).stringWidth(text) >= getWidth() || x < 0) {
            direction *= -1; // Меняем направление
        }

        // Рисуем строку непосредственно здесь
        Graphics g = drawingPanel.getGraphics(); // Получаем графический контекст
        g.setColor(currentColor);
        g.drawString(text, x, drawingPanel.getHeight() / 2);
        g.dispose(); // Освобождаем ресурсы графического контекста
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Здесь ничего не рисуем, так как рисуем в moveString()
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovingStringFrame frame = new MovingStringFrame();
            frame.setVisible(true);
        });
    }
}