package randomgenerator.utility;

import randomgenerator.service.Histogram;
import randomgenerator.service.RandomGenerator;
import randomgenerator.distributions.impl.CustomDistribution;
import randomgenerator.distributions.Distribution;
import randomgenerator.distributions.impl.UniformDistribution;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class GeneratorRemote {
    private RandomGenerator rg;
    Distribution customDistribution;
    Distribution uniformDistribution;
    private List<Double> uniformNumbers;

    public GeneratorRemote() {
        System.out.println("Middle square random generator");
        createGenerator();
        customDistribution = new CustomDistribution();
        uniformDistribution = new UniformDistribution();
    }

    public RandomGenerator createGenerator() {
        System.out.println();
        System.out.println("Choose mode:");
        System.out.println("AUTO/MANUAL (A/M)");
        Scanner scanner = new Scanner(System.in);
        String mode = scanner.next();
        if (mode.toUpperCase().equals("M")) {
            rg = new RandomGenerator(System.currentTimeMillis(), Mode.MANUAL);
        } else {
            rg = new RandomGenerator();
        }
        return rg;
    }

    public void printMenu() {
        System.out.println();
        System.out.println("Choose action:");
        System.out.println("1. Generate uniform distribution sequence");
        System.out.println("2. Evaluate uniform distribution sequence");
        System.out.println("3. Evaluate custom distribution sequence");
        if (rg.getMode() == Mode.MANUAL) {
            System.out.println("4. Set seed");
        }
        System.out.println("0. Exit");
    }

    public void remote(String action) throws IOException, InterruptedException {
        List<Double> customNumbers;
        List<Integer> pSegments;
        Scanner scanner = new Scanner(System.in);
        Histogram hist;
        int n = 0;

        switch (action) {
            case "1":
                uniformNumbers = rg.generateUniformDistributionSequence();
                System.out.println("\nGenerated uniform distribution sequence" +
                        "\nSeed = " + rg.getSeed() +
                        "\nPeriod = " + uniformNumbers.size());
                break;

            case "2":
                System.out.print("\nNumber of segments in the sequence = ");
                n = scanner.nextInt();
                hist = new Histogram(n, new UniformDistribution());
                hist.clearHist();

                if (uniformNumbers != null) {
                    hist.addListToHist(uniformNumbers);
                    System.out.println("Distribution data: " + hist.getHistValues());
                    String pythonData = hist.getHistValues().toString().replace("[", "").replace("]", "");
                    PythonExecutor.execute("src/randomgenerator/service/evaluations/evaluateUniform.py", pythonData, "");
                } else {
                    System.out.println("Empty list");
                }
                break;

            case "3":
                System.out.print("\nNumber of segments in the sequence = ");
                n = scanner.nextInt();
                hist = new Histogram(n, customDistribution);
                hist.clearHist();

                if (uniformNumbers != null) {
                    customNumbers = rg.generateSequenceWithDistribution(uniformNumbers, customDistribution);
                    hist.addListToHist(customNumbers);
                    pSegments = hist.getProbabilityOfHittingTheSegment();
                    System.out.println("Distribution data: " + hist.getHistValues());
                    System.out.println("Probability of hitting the segment = " + pSegments);
                    String pythonData = hist.getHistValues().toString().replace("[", "").replace("]", "");
                    String pythonData2 = pSegments.toString().replace("[", "").replace("]", "");
                    PythonExecutor.execute("src/randomgenerator/service/evaluations/evaluateAny.py", pythonData, pythonData2);
                } else {
                    System.out.println("Empty list");
                }
                break;

            case "4":
                if (rg.getMode() == Mode.MANUAL) {
                    System.out.print("\nSeed = ");
                    long seed = scanner.nextLong();
                    rg.setSeed(seed);
                } else {
                    System.out.println("\nCommand not found");
                }
                break;

            case "0":
                System.out.println("\nExit");
                break;

            default:
                System.out.println("\nCommand not found");

        }
    }
}
