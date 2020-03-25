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

        for (int i = 0; i < 5; ++i) {

            Individual individual = new Individual();

            for (int j = 0; j < 8; ++j) {
                int position = Math.abs(randomInt.nextInt() % 64);
                individual.chromosome.set(position, 1);
            }

            population.add(individual);
        }

    }

    public boolean checkRight(Individual individual, int position) {
        boolean isAttacked = false;

        if (position % 8 == 0) {
            for (int i = 1; i <= 8 - (position % 8) - 1; ++i) {
                if (individual.chromosome.get(position + i) == 1) {
                    isAttacked = true;
                    break;
                }
            }
        } else {
            for (int i = 1; i <= 8 - (position % 8); ++i) {
                if (individual.chromosome.get(position + i) == 1) {
                    isAttacked = true;
                    break;
                }
            }
        }

        return isAttacked;
    }

    public boolean checkLeft(Individual individual, int position) {
        boolean isAttacked = false;

        if (position % 8 == 7) {
            System.out.println("Left Corner case");
            for (int i = 1; i <= (position % 8); ++i) {
                if (individual.chromosome.get(position - i) == 1) {
                    isAttacked = true;
                }
            }
        } else {
            for (int i = 1; i <= (position % 8) - 1; ++i) {
                if (individual.chromosome.get(position - i) == 1) {
                    isAttacked = true;
                }
            }
        }

        return isAttacked;
    }

    public double geneEvaluation(Individual individual, int position) {
        double score = 0;

        if (!checkRight(individual, position)) {
            ++score;
        }

        if (!checkLeft(individual, position)) {
            ++score;
        }

        return score;
    }

    // Get the score for each individual in my population
    public int populationEvaluation() {

        for (Individual individual : population) {
            int count = 0;
            for (Integer gene : individual.chromosome) {
                if (gene == 1) { // is a queen
                    System.out.println("Evaluating position " + count);
                    individual.ranking = geneEvaluation(individual, individual.chromosome.get(count));
                }
                ++count;
            }
            System.out.println(individual);
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
