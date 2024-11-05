import mds.model.Model;
import randomgenerator.service.RandomGenerator;
import randomgenerator.utility.Mode;

import java.io.IOException;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //1726506319873 - 0.7
        //1726506350688 - 0.81
        //1727707011101 - 0.84
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

        RandomGenerator generator1 = new RandomGenerator(1727707011101L, Mode.MANUAL);
        RandomGenerator generator2 = new RandomGenerator(1726506092498L, Mode.MANUAL);
        Model model = new Model(10, 2, 3, 2, 1, generator1.generateUniformDistributionSequence(), generator2.generateUniformDistributionSequence());
        model.printStatus();
        Iterator<Double> it1 = model.getTimeBetweenRequests();
        Iterator<Double> it2 = model.getRequestServiceTime();
        System.out.println(generator1.generateUniformDistributionSequence().get(0));
        System.out.println(generator2.generateUniformDistributionSequence().get(0));
        System.out.println(it1.next());
        System.out.println(it2.next());
    }
}