import queuingsystem.utility.impl.MarkovProcessSimulationRemote;
import queuingsystem.utility.SimulationRemote;
import randomgenerator.utility.GeneratorRemote;
import queuingsystem.utility.impl.SimulationByEventsRemote;

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
        SimulationRemote sr;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Markov or by events simulation? (m/e) ");
        String remote = scanner.next();
        if (remote.equalsIgnoreCase("m")) {
            sr = new MarkovProcessSimulationRemote();
        } else if (remote.equalsIgnoreCase("e")) {
            sr = new SimulationByEventsRemote();
        } else {
            return;
        }

        System.out.print("Use default values? (y/n) ");
        if (scanner.next().equals("y")) {
            sr.simulationSetup(true);
        } else {
            sr.simulationSetup(false);
        }
        sr.printSimulationResults();
    }

    public static void printMenu() {
        System.out.println("Choose mode:");
        System.out.println("1 - generator remote");
        System.out.println("2 - simulation remote");
        System.out.println("0 - exit");
    }
}