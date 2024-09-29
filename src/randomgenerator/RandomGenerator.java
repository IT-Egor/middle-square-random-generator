package randomgenerator;

import randomgenerator.distributions.Distribution;
import randomgenerator.utility.Mode;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class RandomGenerator {
    private Long seed;
    private Mode mode;
    private Distribution distribution;

    public RandomGenerator(Long seed) {
        this.seed = seed;
        mode = Mode.AUTO;
    }

    public RandomGenerator(Long seed, Mode mode) {
        this.seed = seed;
        this.mode = mode;
    }

    public RandomGenerator() {
        mode = Mode.AUTO;
        updateSeed();
    }

    public RandomGenerator(Distribution distribution) {
        this.distribution = distribution;
        this.mode = Mode.AUTO;
        updateSeed();
    }

    public RandomGenerator(Long seed, Mode mode, Distribution distribution) {
        this.seed = seed;
        this.mode = mode;
        this.distribution = distribution;
    }


    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public void writeSeed() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter seed: ");
        seed = scanner.nextLong();
    }

    public Long getSeed() {
        return seed;
    }

    public Mode getMode() {
        return mode;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    private void updateSeed() {
        seed = System.currentTimeMillis();
    }



    public List<Double> generateNormalDistributionSequence() {
        List<Double> values = new ArrayList<>();
        while (values.size() < 20_000) {
            if (mode == Mode.AUTO) {
                updateSeed();
            } else {
                if (!values.isEmpty()) {
                    System.out.println("Zero or repeating sequence");
                    writeSeed();
                }
            }
            values.clear();
            Long prev = generateNext(seed);
            values.add(normalize(prev));

            Long next = generateNext(prev);
            values.add(normalize(next));

            while (!Objects.equals(next, prev) && values.size() < 100_000) {
                prev = generateNext(next);
                values.add(normalize(prev));
                next = generateNext(prev);
                values.add(normalize(next));
            }
        }
        return values;
    }

    private Long generateNext(Long x) {
        return ((x * x) >> 16) & 0xffffffffL;
    }

    private Double normalize(Long value) {
        return value.doubleValue() / 0xffffffffL;
    }
}
