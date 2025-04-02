package LR2;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

// 11. Есть текст со списками цен. Извлечь из него цены в USD, RUR, EU.
// Пример правильных выражений: 23.78 USD, 0.002 USD.
// Пример неправильных выражений: 22 UDD.

public class Main2 {
    public static void main(String[] args) {
        // Пример текста для проверки
        String inputText = "Цены: 23.78 USD, 0.002 USD, 22 UD, 15 CAN, 9.5 EU, -0.4 USD, 0.15 RUR, 555 fants";

        // Регулярное выражение для поиска правильных цен
        String correctRegex = "\\b([0-9]\\d{0,10}(?:\\.\\d{0,10})?)\\s*(USD|RUR|EU)\\b";
        // Регулярное выражение для поиска неправильных цен
        String incorrectRegex = "\\b(\\d+(\\.\\d{0,10})?)\\s*([A-Za-z]+)\\b";

        // Компилируем регулярные выражения
        Pattern correctPattern = Pattern.compile(correctRegex);
        Pattern incorrectPattern = Pattern.compile(incorrectRegex);

        Matcher correctMatcher = correctPattern.matcher(inputText);
        Matcher incorrectMatcher = incorrectPattern.matcher(inputText);

        // Переменные для хранения строк с правильными и неправильными выражениями
        StringBuilder correctPrices = new StringBuilder("Пример правильных выражений: ");
        StringBuilder incorrectPrices = new StringBuilder("Пример неправильных выражений: ");

        // Проверяем текст на наличие правильных выражений
        boolean foundCorrectPrices = false; // Изначально предполагаем, что правильные цены не найдены
        while (correctMatcher.find()) { // Используем Matcher для поиска всех правильных цен
            correctPrices.append(correctMatcher.group()).append(", "); // Добавляем найденную цену в строку
            foundCorrectPrices = true; // Устанавливаем флаг, что правильные цены найдены
        }

        // Проверяем текст на наличие неправильных выражений
        boolean foundIncorrectPrices = false;
        while (incorrectMatcher.find()) {
            String potentialIncorrect = incorrectMatcher.group();
            // Проверяем, является ли найденное выражение правильным

            // Matcher - объект, который используется для поиска совпадений в строке
            // с помощью регулярного выражения. Он предоставляет методы для выполнения
            // поиска и извлечения найденных данных.

            //checkCorrectMatcher - инструмент для нахождения цены

            // correctPattern - Это объект типа Pattern, который содержит скомпилированное
            // регулярное выражение, предназначенное для поиска правильных цен. Он должен быть
            // определен ранее в коде и должен соответствовать шаблону, который мы описали для правильных цен

            //matcher(potentialIncorrect):
            //
            //Метод matcher() вызывается на объекте correctPattern. Этот метод принимает строку
            // (potentialIncorrect), которую мы хотим проверить на соответствие регулярному выражению.
            //
            //potentialIncorrect — это строка, которая содержит потенциально неправильное выражение цены,
            // найденное ранее с помощью неправильного регулярного выражения.
            Matcher checkCorrectMatcher = correctPattern.matcher(potentialIncorrect);
            // CheckCorrectMatcher.find() ищет все совпадения с правильным шаблоном.
            if (!checkCorrectMatcher.find()) {
                incorrectPrices.append(potentialIncorrect).append(", "); // Добавляем всю неправильную цену
                foundIncorrectPrices = true;
            }
        }

        // Удаляем лишние пробелы в конце строк
        if (foundCorrectPrices) {
            correctPrices.setLength(correctPrices.length() - 2); // Удаляем последний пробел и запятую
            correctPrices.append("."); // Добавляем точку в конце
        } else {
            correctPrices.append("Нет.");
        }

        if (foundIncorrectPrices) {
            incorrectPrices.setLength(incorrectPrices.length() - 2); // Удаляем последний пробел и запятую
            incorrectPrices.append("."); // Добавляем точку в конце
        } else {
            incorrectPrices.append("Нет.");
        }

        // Выводим результаты
        System.out.println(correctPrices);
        System.out.println(incorrectPrices);
    }
}