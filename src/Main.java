import mds.model.Simulation;
import randomgenerator.service.RandomGenerator;
import randomgenerator.utility.GeneratorRemote;
import randomgenerator.utility.Mode;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //1726506319873 - 0.7
        //1726506350688 - 0.81
        //1727707011101 - 0.84
        //1726506092498 - 0.91
        //1726398520353 - inf

        Scanner scanner = new Scanner(System.in);
        String action;

        do {
            System.out.println("-".repeat(50));
            printMenu();
            action = scanner.next();
            System.out.println("-".repeat(50));

            switch (action) {
                case "1":
                    generatorRemote();
                    break;

                case "2":
                    simulationRemote();
                    break;

                case "0":
                    System.out.println("Exit");

                default:
                    System.out.println("Invalid action");
            }

        } while (!action.equals("0"));

    }

    public static void generatorRemote() throws IOException, InterruptedException {
        System.out.println();
        GeneratorRemote gr = new GeneratorRemote();
        Scanner scanner = new Scanner(System.in);
        String action;

        do {
            gr.printMenu();
            action = scanner.next();
            gr.remote(action);
        } while (!action.equals("0"));
    }

    public static void simulationRemote() {
        RandomGenerator generator1 = new RandomGenerator(1727707011101L);
        RandomGenerator generator2 = new RandomGenerator(1726506092498L);
        Simulation simulation = new Simulation(
                10000,
                2,
                3,
                2,
                1,
                generator1.generateUniformDistributionSequence(),
                generator2.generateUniformDistributionSequence()
        );

        simulation.run();
        simulation.printStatus();
        System.out.println();
        simulation.printResults();
    }

    public static void printMenu() {
        System.out.println("Choose mode:");
        System.out.println("1 - generator remote");
        System.out.println("2 - simulation remote");
        System.out.println("0 - exit");
    }
}