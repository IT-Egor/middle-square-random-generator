import randomgenerator.utility.GeneratorRemote;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //1726506319873 - 0.7
        //1726506350688 - 0.81
        //1726506092498 - 0.91
        //1726398520353 - inf
        System.out.println();
        GeneratorRemote gr = new GeneratorRemote();
        Scanner scanner = new Scanner(System.in);
        String action;

        do {
            gr.printMenu();
            action = scanner.next();
            gr.remote(action);
        } while (!action.equals("0"));

//        RandomGenerator generator = new RandomGenerator(1726506092498L, Mode.MANUAL);
//        generator = new RandomGenerator();
//        LR2Distribution distribution = new LR2Distribution();
//        List<Double> normal = generator.generateNormalDistributionSequence();
//        List<Double> dist = generator.generateSequenceWithDistribution(normal, distribution);
//        Histogram histogram = new Histogram(50, distribution);
//        histogram.addListToHist(dist);
//        String pythonData = histogram.getHist().toString().replace("[", "").replace("]", "");
////        PythonExecutor.execute("src/randomgenerator/utility/evaluateNormal.py", pythonData);
//
//        List<Integer> pSegments = histogram.getProbabilityOfHittingTheSegment();
//
//        System.out.println("histogram.getHist() = " + histogram.getHist());
//        System.out.println("histogram.getHistSum() = " + histogram.getHistSum());
//        System.out.println("pSegments = " + pSegments);
//        System.out.println("pSegments.size() = " + pSegments.size());
//
//        int sum = 0;
//        for (int pSegment : pSegments) {
//            sum += pSegment;
//        }
//        System.out.println("sum = " + sum);
    }
}