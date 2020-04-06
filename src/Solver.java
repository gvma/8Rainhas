import java.util.*;

public class Solver {

    public List<Individual> population = new ArrayList<>();
    final int MAX_CYCLES = 1000000;

    public void run() {
        long start = System.nanoTime();
        System.out.println("Creating initial population!");
        createInitialPopulation();

        System.out.println("Evaluating initial population");
        populationEvaluation();

        Individual[] sorted = new Individual[50];
        int cycles = 1;
        while (!populationRanking(sorted) && ++cycles != MAX_CYCLES) {
            removeIndividualsFromPopulation();

            System.out.println("Starting crossover!");
            crossover(sorted);

            System.out.println("Mutating!");
            mutation();

            resetRanking();

            System.out.println("Evaluating population!");
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
                System.out.println("Ranking 8!\nHere's the individual:");
                System.out.println(population.get(i));
                return true;
            }
            if (population.get(i).ranking > 6) {
                System.out.println(population.get(i));
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

    public List<Individual> selectRandomPairs() {
        List<Individual> pairs = new ArrayList<>();
        boolean[] selected = new boolean[population.size()];
        Random random = new Random();
        int position;
        for (int i = 0; i < population.size(); ++i) {
            position = Math.abs(random.nextInt() % population.size());
            while (selected[position]) {
                position = Math.abs(random.nextInt() % population.size());
            }
            pairs.add(population.get(position));
            selected[position] = true;
        }
        return pairs;
    }

    public Individual merge(Individual left, Individual right, int mode) {
        Individual individual = new Individual();
        int index = 0;
        if (mode == 1) {
            for (int i = 0; i < 32; ++i) {
                individual.chromosome.set(index++, left.chromosome.get(i));
            }
            for (int i = 0; i < 32; ++i) {
                individual.chromosome.set(index++, right.chromosome.get(i));
            }
        } else if (mode == 2) {
            for (int i = 0; i < 32; ++i) {
                individual.chromosome.set(index++, left.chromosome.get(i));
            }
            for (int i = 32; i < 64; ++i) {
                individual.chromosome.set(index++, right.chromosome.get(i));
            }
        } else if (mode == 3) {
            for (int i = 32; i < 64; ++i) {
                individual.chromosome.set(index++, left.chromosome.get(i));
            }
            for (int i = 0; i < 32; ++i) {
                individual.chromosome.set(index++, right.chromosome.get(i));
            }
        } else if (mode == 4) {
            for (int i = 32; i < 64; ++i) {
                individual.chromosome.set(index++, left.chromosome.get(i));
            }
            for (int i = 32; i < 64; ++i) {
                individual.chromosome.set(index++, right.chromosome.get(i));
            }
        }
        return individual;
    }

    public Individual getSlice(int firstIndex, int lastIndex, Individual selectedIndividual) {
        Individual individual = new Individual();
        for (int i = firstIndex; i < lastIndex; ++i) {
            individual.chromosome.set(i, selectedIndividual.chromosome.get(i));
        }
        return individual;
    }

    public void addNewGeneration(Individual[] sorted) {
        // 20 best individuals
        for (int i = 40; i < 50; ++i) {
            population.add(sorted[i]);
        }

        // 5 pairs amongst the 10 best individuals
        List<Individual> pairs = selectRandomPairs();
        int fatherIndex = 0, motherIndex = 1;

        for (int i = 0; i < 5; ++i) {
            Individual p1 = getSlice(0, 32, pairs.get(fatherIndex));
            Individual p2 = getSlice(32, 64, pairs.get(fatherIndex));
            Individual m1 = getSlice(0, 32, pairs.get(motherIndex));
            Individual m2 = getSlice(32, 64, pairs.get(motherIndex));

            population.add(merge(p1, m1, 1));
            population.add(merge(p1, m2, 2));
            population.add(merge(p2, m1, 3));
            population.add(merge(p2, m2, 4));
            population.add(merge(m1, p1, 1));
            population.add(merge(m1, p2, 2));
            population.add(merge(m2, p1, 3));
            population.add(merge(m2, p1, 4));

            fatherIndex = motherIndex + 1;
            motherIndex = fatherIndex + 1;
        }
    }

    public void crossover(Individual[] sorted) {
        addNewGeneration(sorted);
        fixNewPopulation();
    }

    public int queensInChromosome(Individual individual) {
        int queens = 0;
        for (int i = 0; i < 64; ++i) {
            if (individual.chromosome.get(i) == 1) {
                ++queens;
            }
        }
        return queens;
    }

    public void fixNewPopulation() {
        Random random = new Random();
        for (Individual individual : population) {
            int queens = queensInChromosome(individual);
            if (queens > 8) {
                for (int i = 0; i < queens - 8; ++i) {
                    int position = Math.abs(random.nextInt() % 64);
                    while (individual.chromosome.get(position) == 0) {
                        position = Math.abs(random.nextInt() % 64);
                    }
                    individual.chromosome.set(position, 0);
                }
            } else if (queens < 8) {
                for (int i = 0; i < 8 - queens; ++i) {
                    int position = Math.abs(random.nextInt() % 64);
                    while (individual.chromosome.get(position) == 1) {
                        position = Math.abs(random.nextInt() % 64);
                    }
                    individual.chromosome.set(position, 1);
                }
            }
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
