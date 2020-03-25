import java.util.ArrayList;

public class Individual {

    public ArrayList<Integer> chromosome;
    public double ranking;

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
                chromossome += "\n";
            }
            chromossome += this.chromosome.get(i);
        }
        return "Individual {\nranking: \"" + ranking + "\",\nchromosome:\n\"" + chromossome + "\"\n}";
    }
}
