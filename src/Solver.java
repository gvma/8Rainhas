import java.util.ArrayList;
import java.util.Random;

public class Solver {

    public ArrayList<Individual> population = new ArrayList<>();

    public void run() {

        createInitialPopulation();
        populationEvaluation();
    }

    public void createInitialPopulation() {
        Random randomInt = new Random();

        for (int i = 0; i < 1; ++i) {

            Individual individual = new Individual();

            for (int j = 0; j < 8; ++j) {
                int position = Math.abs(randomInt.nextInt() % 64);
                individual.chromosome.set(position, 1);
            }

            population.add(individual);
        }

    }

    public int checkRight(Individual individual, int index) {
        for (int i = 0; i < 8; ++i) {
            if ((index + i) % 8 == 7) {
                return 1;
            }
            // index + i + i to make sure we are not considering our own position, the if above assures it doesn't crash
            if (individual.chromosome.get(index + i + 1) == 1) {
                return 0;
            }
        }

        return 1;
    }

    public int checkLeft(Individual individual, int index) {
        for (int i = 0; i < 8; ++i) {
            if ((index - i) % 8 == 0) {
                return 1;
            }
            // index - i - i to make sure we are not considering our own position, the if above assures it doesn't crash
            if (individual.chromosome.get(index - i - 1) == 1) {
                return 0;
            }
        }

        return 1;
    }

    public int checkDownwards(Individual individual, int index) {
        for (int i = 1; i < 8; ++i) {
            if (index + i * 8 >= 64) {
                return 1;
            }
            if (individual.chromosome.get(index + i * 8) == 1) {
                return 0;
            }
        }

        return 1;
    }

    public int checkUpwards(Individual individual, int index) {
        for (int i = 1; i < 8; ++i) {
            if (index - i * 8 < 0) {
                return 1;
            }
            if (individual.chromosome.get(index - i * 8) == 1) {
                return 0;
            }
        }

        return 1;
    }

    public int checkUpwardsRight(Individual individual, int index) {
        for (int i = 1; i < 8; ++i) {
            if (index - i * 8 < 0) {

            }
        }
        
        return 1;
    }

    public int geneEvaluation(Individual individual, int index) {
        int score = 0;

        if (checkRight(individual, index) == 1 && checkLeft(individual, index) == 1 && checkDownwards(individual, index) == 1 && checkUpwards(individual, index) == 1) {
            System.out.println("Updating ranking...");
            ++score;
        }

        return score;
    }

    // Get the score for each individual in my population
    public int populationEvaluation() {

        for (Individual individual : population) {
            int count = 0;
            double ranking = 0;
            System.out.println(individual);
            for (Integer gene : individual.chromosome) {
                if (gene == 1) { // is a queen
                    System.out.println("======================");
                    System.out.println("Evaluating index " + count);
                    geneEvaluation(individual, count);
                    System.out.println("======================");
                }
                ++count;
            }
            // validate population
        }

        // 10000000 01100000 00100000 00000000 00001000 00000000 00000000 00000000

        // 10000000
        // 01100000
        // 00100000
        // 00000000
        // 00001000
        // 00000000
        // 00000000
        // 00000000
        return 0;
    }

    // Crossover => dividir em metade e juntar os 2 (salvar resultados da população e ficar com somente 50, que é o tamanho da população inicial)
    // Critério de avaliação => verificar se as rainhas se atacam (pontuação 0 a 8)
}
