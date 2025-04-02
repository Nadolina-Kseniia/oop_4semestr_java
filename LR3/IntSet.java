package LR3;

import java.util.HashSet;
import java.util.Set;

public class IntSet {
    private Set<Integer> set;

    public IntSet() {
        this.set = new HashSet<>();
    }

    public void add(int... values) {
        for (int value : values) {
            set.add(value);
        }
    }

    public boolean contains(int value) {
        return set.contains(value);
    }

    public IntSet intersection(IntSet other) {
        IntSet result = new IntSet();
        for (Integer value : this.set) {
            if (other.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public IntSet union(IntSet other) {
        IntSet result = new IntSet();
        result.set.addAll(this.set);
        result.set.addAll(other.set);
        return result;
    }

    public void display() {
        System.out.println(set);
    }

    public int size() {
        return set.size();
    }
}