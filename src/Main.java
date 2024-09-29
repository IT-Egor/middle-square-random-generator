import randomgenerator.Histogram;
import randomgenerator.RandomGenerator;
import randomgenerator.distributions.ArbitraryDistribution;
import randomgenerator.utility.GeneratorRemote;
import randomgenerator.utility.Mode;
import randomgenerator.utility.PythonExecutor;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //1726506319873 - 0.7
        //1726506350688 - 0.81
        //1726506092498 - 0.91
        //1726398520353 - inf
//        System.out.println();
//        GeneratorRemote gr = new GeneratorRemote();
//        Scanner scanner = new Scanner(System.in);
//        String action;
//
//        do {
//            gr.printMenu();
//            action = scanner.next();
//            gr.remote(action);
//        } while (!action.equals("0"));

        RandomGenerator generator = new RandomGenerator(1726506092498L, Mode.MANUAL);
        ArbitraryDistribution distribution = new ArbitraryDistribution();
        List<Double> normal = generator.generateNormalDistributionSequence();
        List<Double> dist = generator.generateSequenceWithDistribution(normal, distribution);
        Histogram histogram = new Histogram(50, 0, 6);
        histogram.addListToHist(dist);
        String pythonData = histogram.getHist().toString().replace("[", "").replace("]", "");
        PythonExecutor.execute("src/randomgenerator/utility/evaluate.py", pythonData);

//        List<Double> pSegments =
    }
}