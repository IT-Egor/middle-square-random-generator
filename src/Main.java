import mds.model.Simulation;
import randomgenerator.service.RandomGenerator;
import randomgenerator.utility.GeneratorRemote;

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
        Scanner scanner = new Scanner(System.in);
        int numberOfRequests;
        int numberOfChannels;
        int queueSize;
        double inputIntensity;
        double serviceIntensity;
        long seed1;
        long seed2;

        System.out.print("Use default values? (y/n) ");

        if (scanner.next().equals("y")) {
            numberOfRequests = 10000;
            numberOfChannels = 2;
            queueSize = 3;
            inputIntensity = 2;
            serviceIntensity = 1;
            seed1 = 1727707011101L;
            seed2 = 1726506092498L;
        } else {
            System.out.print("Enter number of requests: ");
            numberOfRequests = scanner.nextInt();
            System.out.print("Enter number of channels: ");
            numberOfChannels = scanner.nextInt();
            System.out.print("Enter queue size: ");
            queueSize = scanner.nextInt();
            System.out.print("Enter input intensity: ");
            inputIntensity = scanner.nextDouble();
            System.out.print("Enter service intensity: ");
            serviceIntensity = scanner.nextDouble();
            System.out.print("Enter seed1: ");
            seed1 = scanner.nextLong();
            System.out.print("Enter seed2: ");
            seed2 = scanner.nextLong();
        }

        RandomGenerator generator1 = new RandomGenerator(seed1);
        RandomGenerator generator2 = new RandomGenerator(seed2);
        Simulation simulation = new Simulation(
                numberOfRequests,
                numberOfChannels,
                queueSize,
                inputIntensity,
                serviceIntensity,
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