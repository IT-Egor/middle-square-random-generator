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

    //-----------------------------------service-----------------------------------
    // матрица переходов
    private final double[][] transitionIntensities;
    // количество состояний
    private final int numberOfStates;
    // суммарное время пребывания системы в состояниях
    private final double[] stateTotalTime;
    // текущее состояние
    private int currentState;
    // итератор последовательности случайных чисел
    private Iterator<Double> timeBetweenRequests;
    //-----------------------------------service-----------------------------------

    //--------------------------------------------statistics---------------------------------------------
    // время моделирования
    private double modelTime;
    // количество обработанных заявок
    private int servicedRequestsCount;
    //--------------------------------------------statistics---------------------------------------------

    //----------------------------------------------setup------------------------------------------------
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
        servicedRequestsCount = 0;
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
    //----------------------------------------------setup------------------------------------------------

    //--------------------------------------------execution----------------------------------------------
    public void run() {
        while (servicedRequestsCount < numberOfRequests) {
            Object[] result = findMinTransitionTime(transitionIntensities[currentState]);

            int nextState = (int) result[0];
            double minTransitionTime = (double) result[1];

            modelTime += minTransitionTime;
            stateTotalTime[currentState] += minTransitionTime;

            if (nextState < currentState) {
                servicedRequestsCount++;
            }

            currentState = nextState;
        }
    }

    private Object[] findMinTransitionTime(double[] transitionIntensities) {
        int minIndex = 0;
        double minTransitionTime = -1. / transitionIntensities[minIndex] * Math.log(timeBetweenRequests.next());
        for (int i = 1; i < numberOfStates; i++) {
            if (transitionIntensities[i] != 0) {
                double transitionTime = -1. / transitionIntensities[i] * Math.log(timeBetweenRequests.next());
                if (minTransitionTime > transitionTime) {
                    minTransitionTime = transitionTime;
                    minIndex = i;
                }
            }
        }
        return new Object[]{minIndex, minTransitionTime};
    }
    //--------------------------------------------execution----------------------------------------------

    //---------------------------------------------results-----------------------------------------------
    public void printStatus() {
        int cutWidth = 22;
        System.out.println("-".repeat(cutWidth) + "status" + "-".repeat(cutWidth));
        System.out.println("numberOfRequests = " + numberOfRequests);
        System.out.println("numberOfChannels = " + numberOfChannels);
        System.out.println("queueSize = " + queueSize);
        System.out.println("number of states = " + numberOfStates);
        System.out.println("inputIntensity = " + inputIntensity);
        System.out.println("serviceIntensity = " + serviceIntensity);
        System.out.println("firstSequence = " + timeBetweenRequestsSequence.size());
        System.out.println("modelTime = " + modelTime);
        System.out.println("-".repeat(cutWidth) + "status" + "-".repeat(cutWidth));
    }

    public void printResults() {
        int cutWidth = 22;
        System.out.println("-".repeat(cutWidth) + "result" + "-".repeat(cutWidth));
        System.out.println("probabilities of queuing system states:");
        for (int i = 0; i <= numberOfChannels + queueSize; i++) {
            System.out.printf("p%d = %.15f%n", i, stateTotalTime[i] / modelTime);
        }

        System.out.println("rejection probability: " + stateTotalTime[numberOfStates-1] / modelTime);
        System.out.println("load factor: " + (1 - stateTotalTime[0] / modelTime));
        System.out.println("bandwidth: " + (double) servicedRequestsCount / modelTime);

        double average = 0;
        for (int i = 1; i < numberOfStates; i++) {
            average += i * stateTotalTime[i] / modelTime;
        }
        System.out.println("average number of requests: " + average);

        double servicedRequestsAverage = 0;
        for (int i = 0; i < numberOfStates; i++) {
            if (i < numberOfChannels) {
                servicedRequestsAverage += i * stateTotalTime[i];
            } else {
                servicedRequestsAverage += numberOfChannels * stateTotalTime[i];
            }
        }
        System.out.println("serviced requests average: " + servicedRequestsAverage / modelTime);

        double sum = 0;
        for (int i = numberOfChannels + 1; i <= numberOfChannels + queueSize; i++) {
            sum += stateTotalTime[i] * (i - numberOfChannels);
        }
        System.out.println("average queue length: " + sum / modelTime);

        double b = 0;
        double c = servicedRequestsAverage;
        for (int i = 0; i <= numberOfChannels + queueSize; i++) {
            b += i * stateTotalTime[i];
        }
        System.out.println("average waiting time: " + (b - c) / numberOfRequests);
        System.out.println("-".repeat(cutWidth) + "result" + "-".repeat(cutWidth));
    }
    //---------------------------------------------results-----------------------------------------------
}
