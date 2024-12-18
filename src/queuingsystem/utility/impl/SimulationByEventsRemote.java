package queuingsystem.utility.impl;

import queuingsystem.simulation.SimulationByEvents;
import queuingsystem.utility.SimulationRemote;
import randomgenerator.service.RandomGenerator;

import java.util.List;
import java.util.Scanner;

public class SimulationByEventsRemote implements SimulationRemote {
    private Scanner scanner = new Scanner(System.in);
    private int numberOfRequests;
    private int numberOfChannels;
    private int queueSize;
    private double inputIntensity;
    private double serviceIntensity;
    private long seed1;
    private long seed2;
    private List<Double> timeBetweenRequestsSequence;
    private List<Double> requestServiceTimeSequence;
    boolean isSetup = false;
    SimulationByEvents simulationByEvents;

    @Override
    public void simulationSetup(boolean defaultValues) {
        if (defaultValues) {
            numberOfRequests = 10000;
            numberOfChannels = 2;
            queueSize = 3;
            inputIntensity = 2;
            serviceIntensity = 1;
            seed1 = 1727707011101L;
            seed2 = 1726506092498L;
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
            System.out.print("Enter seed2: ");
            seed2 = scanner.nextLong();
        }

        RandomGenerator generator1 = new RandomGenerator(seed1);
        RandomGenerator generator2 = new RandomGenerator(seed2);
        timeBetweenRequestsSequence = generator1.generateUniformDistributionSequence();
        requestServiceTimeSequence = generator2.generateUniformDistributionSequence();

        simulationByEvents = new SimulationByEvents(
                numberOfRequests,
                numberOfChannels,
                queueSize,
                inputIntensity,
                serviceIntensity,
                timeBetweenRequestsSequence,
                requestServiceTimeSequence
        );

        isSetup = true;
    }

    @Override
    public void printSimulationResults() {
        if (!isSetup) {
            simulationSetup(true);
        }
        simulationByEvents.run();
        int cutWidth = 22;
        System.out.println("=".repeat(cutWidth) + "Events" + "=".repeat(cutWidth));
        simulationByEvents.printStatus();
        System.out.println();
        simulationByEvents.printResults();
        System.out.println("=".repeat(cutWidth) + "Events" + "=".repeat(cutWidth));
    }
}
