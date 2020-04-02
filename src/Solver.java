import java.util.*;

public class Solver {

    public List<Individual> population = new ArrayList<>();
    final int MAX_CYCLES = 1000000;

    public void run() {
        long start = System.nanoTime();
        createInitialPopulation();
        populationEvaluation();
        Individual[] sorted = new Individual[50];
        int cycles = 1;
        while (!populationRanking(sorted) && ++cycles != MAX_CYCLES) {
            removeIndividualsFromPopulation();
            crossover(sorted);
            mutation();
            resetRanking();
            populationEvaluation();
        }
        if (cycles == MAX_CYCLES) {
            System.out.println(sorted[49]);
        }
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.println((timeElapsed * 1e-9) + " seconds");
    }

    public void resetRanking() {
        for (Individual individual : population) {
            individual.ranking = 0;
        }
    }

    public boolean populationRanking(Individual[] sorted) {
        for (int i = 0; i < 50; ++i) {
            if (population.get(i).ranking == 8) {
                System.out.println("Ranking 8!\nHere's the individual:\n");
                System.out.println(population.get(i));
                return true;
            }
            sorted[i] = population.get(i);
        }
        Arrays.sort(sorted);
        return false;
    }

    public void mutation() {
        Random randomInt = new Random();

        int position = Math.abs(randomInt.nextInt() % 50); // get random individual from population
        Individual randomIndividual = population.get(position);

        // get random positions in random individual
        int positionToRemove = Math.abs(randomInt.nextInt() % 64), positionToAdd = Math.abs(randomInt.nextInt() % 64);

        while (randomIndividual.chromosome.get(positionToRemove) == 0) {
            positionToRemove = Math.abs(randomInt.nextInt() % 64);
        }

        while (randomIndividual.chromosome.get(positionToAdd) == 1) {
            positionToAdd = Math.abs(randomInt.nextInt() % 64);
        }

        randomIndividual.chromosome.set(positionToRemove, 0);
        randomIndividual.chromosome.set(positionToAdd, 1);
    }

    public void removeIndividualsFromPopulation() {
        population = new ArrayList<>();
    }

    public void crossover(Individual[] sorted) {
        // 20 best individuals
        for (int i = 29; i < 50; ++i) {
            population.add(sorted[i]);
        }

        // 5 worst individuals
        for (int i = 0; i < 5; ++i) {
            population.add(sorted[i]);
        }

        for (int i = 0; i < 25; ++i) {
            Individual worst = sorted[i], best = sorted[49 - i];
            Individual individual = new Individual();
            for (int j = 0; j < 32; ++j) {
                individual.chromosome.set(j, best.chromosome.get(63 - j));
                individual.chromosome.set(63 - j, worst.chromosome.get(j));
            }
            population.add(individual);
        }
    }

    public void addRandomIndividuals(int loops) {
        Random randomInt = new Random();

        for (int i = 0; i < loops; ++i) {

            Individual individual = new Individual();
            int position = 0;
            for (int j = 0; j < 8; ++j) {
                while (individual.chromosome.get(position) == 1) {
                    position = Math.abs(randomInt.nextInt() % 64);
                }
                individual.chromosome.set(position, 1);
            }

            population.add(individual);
        }
    }

    public void createInitialPopulation() {
        addRandomIndividuals(50);
    }

    // Get the score for each individual in my population
    public void populationEvaluation() {

        for (Individual individual : population) {
            int position = 0;
            for (Integer gene : individual.chromosome) {
                if (gene == 1) { // it's a queen
                    geneEvaluation(individual, position);
                }
                ++position;
            }
        }
    }

    public boolean checkRight(Individual individual, int index) {
        for (int i = 0; i < 8; ++i) {
            if ((index + i) % 8 == 7) {
                return true;
            }
            // index + i + i to make sure we are not considering our own position, the if above assures it doesn't crash
            if (individual.chromosome.get(index + i + 1) == 1) {
                return false;
            }
        }

        return true;
    }

    public boolean checkLeft(Individual individual, int index) {
        for (int i = 0; i < 8; ++i) {
            if ((index - i) % 8 == 0) {
                return true;
            }
            // index - i - i to make sure we are not considering our own position, the if above assures it doesn't crash
            if (individual.chromosome.get(index - i - 1) == 1) {
                return false;
            }
        }

        return true;
    }

    public boolean checkDownwards(Individual individual, int index) {
        for (int i = 1; i < 8; ++i) {
            if (index + i * 8 >= 64) {
                return true;
            }
            if (individual.chromosome.get(index + i * 8) == 1) {
                return false;
            }
        }

        return true;
    }

    public boolean checkUpwards(Individual individual, int index) {
        for (int i = 1; i < 8; ++i) {
            if (index - i * 8 < 0) {
                return true;
            }
            if (individual.chromosome.get(index - i * 8) == 1) {
                return false;
            }
        }

        return true;
    }

    public void printBoard(int[][] board) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                System.out.print(board[i][j]);
            }
            System.out.print("\n");
        }
    }

    public boolean isUnderAttack(int x, int y, int[][] board, int xSignal, int ySignal) {
        for (int i = 1, j = 1; i <= 8; ++i, ++j) {
            int newX = x + i * xSignal, newY = y + j * ySignal;
            if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
                return false;
            }
            if (board[newX][newY] == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDiagonals(Individual individual, int index) {
        int[][] board = new int[8][8];

        for (int i = 0; i < 64; ++i) {
            board[i / 8][i % 8] = individual.chromosome.get(i);
        }
        // printBoard(board);

        int[] dx = {1, 1, -1, -1}, dy = {1, -1, 1, -1};
        for (int i = 0; i < 4; ++i) {
            if (isUnderAttack(index / 8, index % 8, board, dx[i], dy[i])) {
                return false;
            }
        }
        return true;
    }

    public void geneEvaluation(Individual individual, int index) {
        if (checkDiagonals(individual, index) && checkRight(individual, index) && checkLeft(individual, index)
                && checkDownwards(individual, index) && checkUpwards(individual, index)) {
            ++individual.ranking;
        }
    }
}
