import java.util.ArrayList;

public class Individual implements Comparable<Individual> {

    public ArrayList<Integer> chromosome;
    public int ranking;

    public Individual() {
        chromosome = new ArrayList<>();
        for (int i = 0; i < 64; ++i) {
            chromosome.add(0);
        }
    }

    @Override
    public String toString() {
        String chromossome = "";
        for (int i = 0; i < 64; ++i) {
            if (i % 8 == 0) {
                chromossome += "\n\t\t";
            }
            chromossome += this.chromosome.get(i) +  " ";
        }
        return "Individual {\n\tranking: \"" + ranking + "\",\n\tchromosome:\t" + chromossome + "\t\n}";
    }

    @Override
    public int compareTo(Individual individual) {
        if (this.ranking > individual.ranking) {
            return 1;
        } else if (this.ranking < individual.ranking) {
            return -1;
        }
        return 0;
    }
}
