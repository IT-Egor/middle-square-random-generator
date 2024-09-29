package randomgenerator.utility;

import randomgenerator.Histogram;
import randomgenerator.RandomGenerator;
import randomgenerator.distributions.CustomDistribution;
import randomgenerator.distributions.Distribution;
import randomgenerator.distributions.NormalDistribution;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class GeneratorRemote {
    private RandomGenerator rg;
    List<Double> normalNumbers;
    List<Double> customNumbers;
    List<Integer> pSegments;

    public GeneratorRemote() {
        System.out.println("Middle square random generator");
        createGenerator();
    }

    public RandomGenerator createGenerator() {
        System.out.println();
        System.out.println("Choose mode:");
        System.out.println("AUTO/MANUAL (A/M)");
        Scanner scanner = new Scanner(System.in);
        String mode = scanner.next();
        switch (mode.toUpperCase()) {
            case "M":
                rg = new RandomGenerator(System.currentTimeMillis(), Mode.MANUAL);
                break;

            default:
                rg = new RandomGenerator();
        }
        return rg;
    }

    public void printMenu() {
        System.out.println();
        System.out.println("Choose action:");
        System.out.println("1. Generate normal distribution sequence");
        System.out.println("2. Evaluate normal distribution sequence");
        System.out.println("3. Evaluate custom distribution sequence");
        if (rg.getMode() == Mode.MANUAL) {
            System.out.println("4. Set seed");
        }
        System.out.println("0. Exit");
    }

    public void remote(String action) throws IOException, InterruptedException {
        Distribution customDistribution = new CustomDistribution();
        Scanner scanner = new Scanner(System.in);
        Histogram hist;
        int n = 0;

        switch (action) {
            case "1":
                normalNumbers = rg.generateNormalDistributionSequence();
                System.out.println("\nGenerated normal distribution sequence" +
                        "\nSeed = " + rg.getSeed() +
                        "\nPeriod = " + normalNumbers.size());
                break;

            case "2":
                System.out.print("\nNumber of segments in the sequence = ");
                n = scanner.nextInt();
                hist = new Histogram(n, new NormalDistribution());
                hist.clearHist();

                if (normalNumbers != null) {
                    hist.addListToHist(normalNumbers);
                    System.out.println("Distribution data: " + hist.getHist());
                    String pythonData = hist.getHist().toString().replace("[", "").replace("]", "");
                    PythonExecutor.execute("src/randomgenerator/utility/evaluateNormal.py", pythonData, "");
                } else {
                    System.out.println("Empty list");
                }
                break;

            case "3":
                System.out.print("\nNumber of segments in the sequence = ");
                n = scanner.nextInt();
                hist = new Histogram(n, customDistribution);
                hist.clearHist();

                if (normalNumbers != null) {
                    customNumbers = rg.generateSequenceWithDistribution(normalNumbers, customDistribution);
                    hist.addListToHist(customNumbers);
                    pSegments = hist.getProbabilityOfHittingTheSegment();
                    System.out.println("Distribution data: " + hist.getHist());
                    System.out.println("Probability of hitting the segment = " + pSegments);
                    String pythonData = hist.getHist().toString().replace("[", "").replace("]", "");
                    String pythonData2 = pSegments.toString().replace("[", "").replace("]", "");
                    PythonExecutor.execute("src/randomgenerator/utility/evaluateAny.py", pythonData, pythonData2);
                } else {
                    System.out.println("Empty list");
                }

                break;

            case "0":
                System.out.println("\nExit");
                break;

            default:
                if (rg.getMode() == Mode.MANUAL) {
                    System.out.print("\nSeed = ");
                    long seed = scanner.nextLong();
                    rg.setSeed(seed);
                } else {
                    System.out.println("\nCommand not found");
                }
        }
    }
}
