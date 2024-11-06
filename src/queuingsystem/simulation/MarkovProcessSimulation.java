package queuingsystem.simulation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MarkovProcessSimulation {

    //------------------------------------input------------------------------------
    // N — максимальное число входящих заявок (условие окончания моделирования)
    private final int numberOfRequests;
    // NK — количество каналов
    private final int numberOfChannels;
    // LMAX — максимально допустимая длина очереди
    private final int queueSize;
    // интенсивность потока заявок (лямбда)
    private final double inputIntensity;
    // интенсивность потока обработки заявок (мю)
    private final double serviceIntensity;
    // массив случайных чисел для времени между заявками
    private final List<Double> timeBetweenRequestsSequence;
    //------------------------------------input------------------------------------

    private final double[][] transitionIntensities;
    private Iterator<Double> timeBetweenRequests;
    private final int numberOfStates;

    private double modelTime;
    private int servicedRequests;
    private final double[] stateTotalTime;
    private int currentState;


    public MarkovProcessSimulation(
            int numberOfRequests,
            int numberOfChannels,
            int queueSize,
            double inputIntensity,
            double serviceIntensity,
            List<Double> timeBetweenRequestsSequence) {
        this.numberOfRequests = numberOfRequests;
        this.numberOfChannels = numberOfChannels;
        this.queueSize = queueSize;
        this.inputIntensity = inputIntensity;
        this.serviceIntensity = serviceIntensity;
        this.timeBetweenRequestsSequence = timeBetweenRequestsSequence;

        numberOfStates = numberOfChannels + queueSize + 1;
        transitionIntensities = new double[numberOfStates][numberOfStates];
        stateTotalTime = new double[numberOfStates];
        fillTransitionIntensitiesMatrix();
        reset();
    }

    private void reset() {
        modelTime = 0;
        servicedRequests = 0;
        currentState = 0;
        timeBetweenRequests = timeBetweenRequestsSequence.iterator();

        Arrays.fill(stateTotalTime, 0);
    }

    private void fillTransitionIntensitiesMatrix() {
        for (int i = 0; i < numberOfStates; i++) {
            for (int j = 0; j < numberOfStates; j++) {
                if (j == i+1) {
                    transitionIntensities[i][j] = inputIntensity;
                } else if (j == i-1) {
                    if (j+1 <= numberOfChannels) {
                        transitionIntensities[i][j] = serviceIntensity * (j+1);
                    } else {
                        transitionIntensities[i][j] = serviceIntensity * numberOfChannels;
                    }
                } else {
                    transitionIntensities[i][j] = 0;
                }
            }
        }
    }
}
