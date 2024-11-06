import org.w3c.dom.ls.LSOutput;
import queuingsystem.simulation.MarkovProcessSimulation;
import queuingsystem.simulation.SimulationByEvents;
import randomgenerator.service.RandomGenerator;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        RandomGenerator generator = new RandomGenerator(1726506092498L);

        MarkovProcessSimulation simulation = new MarkovProcessSimulation(
                1000,
                2,
                3,
                1.2,
                1.2,
                generator.generateUniformDistributionSequence()
        );
    }
}
