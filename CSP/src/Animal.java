import java.util.List;

public class Animal {

    int size;
    int allowed[];
    int name;
    boolean isChosen;

    public Animal(int size, int[] allowed, int name, boolean isChosen) {
        this.size = size;
        this.allowed = allowed;
        this.name = name;
        this.isChosen = isChosen;
    }
}
