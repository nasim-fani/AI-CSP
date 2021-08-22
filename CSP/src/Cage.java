import java.util.ArrayList;
import java.util.List;

public class Cage {


    int neighbours[];
    int size;
    private Animal value = null;
    ArrayList<Animal> domain = new ArrayList<>();
    int name;
    boolean isAssigned = false;


    public Cage(int[] neighbours, int size, Animal value, ArrayList<Animal> allAnimals, int name, boolean isAssigned) {
        this.neighbours = neighbours;
        this.size = size;
        this.value = value;
        this.domain = setDomain(allAnimals);
        this.name = name;
        this.isAssigned = isAssigned;
    }


    public void unAssignValue() {
        value.isChosen = false;
        this.value = null;
        isAssigned = false;
    }


    public void assignValue(Animal value) {
        this.value = value;
        isAssigned = true;
        value.isChosen = true;
    }

    ArrayList<Animal> setDomain(ArrayList<Animal> allAnimals) {
        ArrayList<Animal> domain = new ArrayList<>();
        for (Animal animal :
                allAnimals) {
            if (animal.size <= this.size) domain.add(animal);
        }
        return domain;
    }


    boolean valueIsChosenBefore(Animal value) {
        if (value.isChosen) return true;
        return false;
    }

    boolean hasConflict(ArrayList<Cage> cages, Animal value) {
        for (Cage cage :
                cages) {
            if (neighbours[cage.name - 1] == 1 && cage.isAssigned) {
                if (value.allowed[cage.getValue().name - 1] == 0) return true;

            }
        }
        return false;
    }


    boolean isValid(ArrayList<Cage> cages, Animal value) {

        if (valueIsChosenBefore(value) || hasConflict(cages, value)) {
            return false;
        }
        return true;
    }

    Animal getValue() {
        return value;
    }

    void removeFromDomain(Animal value) {
        Animal remove = null;
        for (Animal animal :
                this.domain) {
            if(value.name==animal.name){
                remove = animal;
            }
        }
        if(remove!=null) this.domain.remove(remove);

    }


    void removeCollectionFromDomain(ArrayList<Animal> values) {
        this.domain.removeAll(values);
    }

    boolean checkIfDomainIsEmpty() {
        if (this.domain.size() < 1) {
            return true;
        }
        return false;
    }

}
