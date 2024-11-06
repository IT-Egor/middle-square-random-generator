package queuingsystem.utility.impl;

import queuingsystem.simulation.MarkovProcessSimulation;
import queuingsystem.utility.SimulationRemote;
import randomgenerator.service.RandomGenerator;

import java.util.List;
import java.util.Scanner;

public class MarkovProcessSimulationRemote implements SimulationRemote {
    private Scanner scanner = new Scanner(System.in);
    private int numberOfRequests;
    private int numberOfChannels;
    private int queueSize;
    private double inputIntensity;
    private double serviceIntensity;
    private long seed1;
    private List<Double> timeBetweenRequestsSequence;
    boolean isSetup = false;
    MarkovProcessSimulation markovProcessSimulation;

    @Override
    public void simulationSetup(boolean defaultValues) {
        if (defaultValues) {
            numberOfRequests = 10000;
            numberOfChannels = 2;
            queueSize = 3;
            inputIntensity = 1.2;
            serviceIntensity = 1. / 1.2;
            seed1 = 1727707011101L;
        } else {
            System.out.print("Enter number of requests (< 20_000): ");
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
        }

        RandomGenerator generator1 = new RandomGenerator(seed1);
        timeBetweenRequestsSequence = generator1.generateUniformDistributionSequence();

        markovProcessSimulation = new MarkovProcessSimulation(
                numberOfRequests,
                numberOfChannels,
                queueSize,
                inputIntensity,
                serviceIntensity,
                timeBetweenRequestsSequence
        );

        isSetup = true;
    }

    @Override
    public void runSimulation() {
        if (!isSetup) {
            simulationSetup(true);
        }
        markovProcessSimulation.run();
        int cutWidth = 22;
        System.out.println("=".repeat(cutWidth) + "Markov" + "=".repeat(cutWidth));
        markovProcessSimulation.printStatus();
        System.out.println();
        markovProcessSimulation.printResults();
        System.out.println("=".repeat(cutWidth) + "Markov" + "=".repeat(cutWidth));
    }
}
