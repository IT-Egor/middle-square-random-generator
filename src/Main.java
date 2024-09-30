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
}