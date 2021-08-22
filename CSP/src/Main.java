//import javafx.scene.input.Mnemonic;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int CHOOSE_FIRST_EMPTY_CAGE = 0;
    private static final int MRV_METHOD = 1;


    private static final int CHOOSE_FIRST_DOMAIN = 0;
    private static final int LCV_METHOD = 1;



    public static void main(String[] args) {
        int N = 6, P = 6, M = 5;
        int cageSize[] = {1, 2, 4, 2, 3, 3};
        int animalSize[] = {3, 1, 2, 1, 2, 2};
        int neighbours[][] = {{1, 2}, {2, 3}, {3, 4}, {4, 5}, {4, 6}};
        int notAllowed[][] = {{1, 0, 1, 0, 1, 1}, {0, 1, 0, 0, 1, 0}, {1, 0, 1, 1, 1, 1}, {0, 0, 1, 1, 1, 0}, {1, 1, 1, 1, 1, 1}, {1, 0, 1, 0, 1, 1}};
        int[][] allNeighbours;
        allNeighbours = neighbourHandling(N, M, neighbours);


        //1 3 4 5 2 6


//        int N = 3, P = 3, M = 2;
//        int cageSize[] = {1, 3, 2};
//        int animalSize[] = {2, 3, 1};
//        int neighbours[][] = {{1, 2}, {2, 3}};
//        int notAllowed[][] = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
//        int[][] allNeighbours;
//        allNeighbours = neighbourHandling(N, M, neighbours);


        ArrayList<Cage> cages = new ArrayList<>();
        ArrayList<Animal> animals = new ArrayList<>();

        for (int i = 0; i < animalSize.length; i++) {
            Animal animal = new Animal(animalSize[i], notAllowed[i], i + 1, false);
            animals.add(animal);
        }


        for (int i = 0; i < cageSize.length; i++) {
            Cage cage = new Cage(allNeighbours[i], cageSize[i], null, animals, i + 1, false);
            cages.add(cage);
        }


        ArrayList<Cage> x = backTracking(cages);
//        ArrayList<Cage> x = forwardCheckingBackTracking(cages);

        for (Cage cage :
                x) {
            System.out.println("cage " + cage.name + " : " + cage.getValue().name);
        }

    }


    static int[][] neighbourHandling(int N, int M, int[][] allNeighbours) {
        int neighbours[][] = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                neighbours[i][j] = 0;
            }
        }

        for (int i = 0; i < M; i++) {
            neighbours[allNeighbours[i][0] - 1][allNeighbours[i][1] - 1] = 1;
            neighbours[allNeighbours[i][1] - 1][allNeighbours[i][0] - 1] = 1;

        }

        return neighbours;

    }

    static Cage chooseVariable(ArrayList<Cage> variables, int chooseMethod) {
        switch (chooseMethod) {

            //choose first null variable
            case 0: {
                for (Cage cage :
                        variables) {
                    if (!cage.isAssigned) return cage;
                }
                break;
            }

            //choose with mrv method
            case 1: {
                int min = Integer.MAX_VALUE;
                int minIndex = -1;
                for (Cage cage :
                        variables) {
                    if (!cage.isAssigned) {
                        if (cage.domain.size() < min) {
                            min = cage.domain.size();
                            minIndex = cage.name - 1;
                        }

                    }
                }

                return variables.get(minIndex);

            }


        }
        return null;
    }

    static ArrayList<Animal> orderedDomain(Cage selected,ArrayList<Cage> cages, int orderMethod) {
        switch (orderMethod) {
            case 0: {
                return (ArrayList<Animal>) selected.domain.clone();
            }

            case 1: {


                int[] conflictHappened = new int[selected.domain.size()];
                for (int i = 0; i < conflictHappened.length; i++) {
                    conflictHappened[i] = 0;
                }


                for (int i = 0; i < selected.domain.size(); i++) {

                    //remove from others domain
                    for (Cage cage :
                            cages) {
                        if (!cage.isAssigned) {
                            if (cage.domain.contains(selected.domain.get(i)))
                                conflictHappened[i]++;
                        }
                    }


                    for (Cage cage :
                            cages) {
                        if (selected.neighbours[cage.name - 1] == 1 && !cage.isAssigned) {
                            for (Animal neighbourDomain :
                                    cage.domain) {
                                if (neighbourDomain.allowed[selected.domain.get(i).name - 1] == 0) {
                                    conflictHappened[i]++;
                                }
                            }
                        }
                    }

                }


                ArrayList<Animal> newDomain = new ArrayList<>();

                for (int j = 0; j < conflictHappened.length; j++) {


                    int min = Integer.MAX_VALUE - 1;
                    int minIndex = -1;
                    for (int i = 0; i < conflictHappened.length; i++) {
                        if (conflictHappened[i] < min) {
                            min = conflictHappened[i];
                            minIndex = i;
                        }
                    }
                    newDomain.add(selected.domain.get(minIndex));
                    conflictHappened[minIndex] = Integer.MAX_VALUE;
                }
                return newDomain;


            }
        }
        return null;

    }


    //backtracking function
    static ArrayList<Cage> backTracking(ArrayList<Cage> cages) {
        ArrayList<Cage> solution = new ArrayList<>();


        //solution is complete or not
        boolean isGoal = true;
        for (Cage cage :
                cages) {
            if (!cage.isAssigned) isGoal = false;
        }
        if (isGoal) {
            return cages;
        }

//        arc_consistency(cages);
//        for (Cage cage :
//                cages) {
//            if (cage.domain.isEmpty()) return null;
//        }

        Cage selectedVariable = chooseVariable(cages, MRV_METHOD);


        for (Animal value :
                orderedDomain(selectedVariable,cages, LCV_METHOD)) {

            if (selectedVariable.isValid(cages, value)) {
                selectedVariable.assignValue(value);
                solution = backTracking(cages);
                if (solution != null) return solution;
                selectedVariable.unAssignValue();
            }
        }
        return null;
    }


    static void forwardChecking(Cage variable, ArrayList<Cage> cages) {

        //remove from others domain
        for (Cage cage :
                cages) {
            if (!cage.isAssigned) {
                cage.removeFromDomain(variable.getValue());
            }
        }


        for (Cage cage :
                cages) {
            if (variable.neighbours[cage.name - 1] == 1 && !cage.isAssigned) {
                ArrayList<Animal> delete = new ArrayList<>();
                for (Animal neighbourDomain :
                        cage.domain) {
                    if (neighbourDomain.allowed[variable.getValue().name - 1] == 0) {
                        delete.add(neighbourDomain);
                    }
                }
                cage.removeCollectionFromDomain(delete);
            }
        }
    }

    static boolean checkIfAnyDomainIsEmpty(ArrayList<Cage> cages) {
        for (Cage cage :
                cages
        ) {
            if (cage.checkIfDomainIsEmpty()) return true;
        }
        return false;
    }


    static ArrayList<Cage> forwardCheckingBackTracking(ArrayList<Cage> cages) {

        ArrayList<Cage> solution = new ArrayList<>();

        //solution is complete or not
        boolean isGoal = true;
        for (Cage cage :
                cages) {
            if (!cage.isAssigned) isGoal = false;
        }
        if (isGoal) {
            return cages;
        }

        ArrayList<Cage> copyOfCages = copyListOfCages(cages);
        Cage selectedVariable = chooseVariable(copyOfCages, 0);
        ArrayList<Animal> orderedDomain = copyListOfAnimals(orderedDomain(selectedVariable,copyOfCages, 0));
        for (Animal value :
                orderedDomain) {


            selectedVariable.assignValue(value);
            forwardChecking(selectedVariable, copyOfCages);

            if (checkIfAnyDomainIsEmpty(copyOfCages)) {
                for (int i = 0; i < copyOfCages.size(); i++) {
                    copyOfCages.get(i).domain = cages.get(i).domain;

                }

                selectedVariable.unAssignValue();

                continue;
            }


            solution = forwardCheckingBackTracking(copyOfCages);
            if (solution != null) return solution;
            selectedVariable.unAssignValue();

        }

        return null;


    }


    static ArrayList<Animal> copyListOfAnimals(ArrayList<Animal> animals) {
        ArrayList<Animal> newArrayList = new ArrayList<>();
        for (Animal animal :
                animals
        ) {
            newArrayList.add(new Animal(animal.size, animal.allowed, animal.name, animal.isChosen));
        }
        return newArrayList;
    }


    static ArrayList<Cage> copyListOfCages(ArrayList<Cage> cages) {
        ArrayList<Cage> newArrayList = new ArrayList<>();
        for (Cage cage :
                cages
        ) {
            newArrayList.add(new Cage(cage.neighbours, cage.size, cage.getValue(), cage.domain, cage.name, cage.isAssigned));
        }
        return newArrayList;
    }

    static void arc_consistency(ArrayList<Cage> cages) {
        for (Cage a :
                cages
        ) {
            if (!a.isAssigned) {
                for (Cage b :
                        cages) {
                    if (!b.isAssigned)
                        if (a.name != b.name) {
                            removeInconsistentValues(a, b);

                        }
                }
            }
        }

    }

    static void removeInconsistentValues(Cage a, Cage b) {

        Animal firstElementOfADomain = a.domain.get(0);

        if (a.domain.size() == 1 && b.domain.contains(firstElementOfADomain)) {
            b.domain.remove(firstElementOfADomain);
        }

        if (a.neighbours[b.name - 1] == 1) {
            ArrayList<Animal> copyOfdomainsOfB = copyListOfAnimals(b.domain);
            ArrayList<Animal> copyOfdomainsOfA = copyListOfAnimals(a.domain);
            for (Animal domainsOfA :
                    copyOfdomainsOfA) {
                for (Animal domainsOfB :
                        b.domain) {
                    if (domainsOfA.allowed[domainsOfB.name - 1] == 0) {
                        copyOfdomainsOfB.remove(domainsOfA);
                    }
                }
                if (copyOfdomainsOfB.isEmpty()) {
                    a.domain.remove(domainsOfA);
                }
            }

        }
    }

}
