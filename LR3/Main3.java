package LR3;

public class Main3 {
    public static void main(String[] args) {
        IntSet set1 = new IntSet();
        set1.add(1, 2, 5, 6, 9, 10, 10);

        IntSet set2 = new IntSet();
        set2.add(2, 3, 4, 5, 6, 7, 8, 9);

        displaySet("Set 1: ", set1);
        displaySet("Set 2: ", set2);
        displaySet("Пересечение: ", set1.intersection(set2));
        displaySet("Объединение: ", set1.union(set2));

        System.out.println("Размер Set 1: " + set1.size());
        System.out.println("Размер Set 2: " + set2.size());
    }

    private static void displaySet(String title, IntSet set) {
        System.out.print(title);
        set.display();
    }
}