package queuingsystem.simulation;

import java.util.*;
import java.lang.Math;
import java.util.stream.Stream;

public class SimulationByEvents {
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
    // массив случайных чисел для времени обработки заявок
    private final List<Double> requestServiceTimeSequence;
    //------------------------------------input------------------------------------

    //--------------------------------------------statistics---------------------------------------------
    // T - текущее модельное время, изменяющееся скачком между моментами возникновения событий в системе
    private double modelTime;
    // TA — момент прихода очередной заявки (время, прошедшее с начала моделирования)
    private double timeUtilNextRequest;
    // K — счетчик пришедших заявок
    private int requestsCount;
    // KS — счетчик обслуженных заявок
    private int servicedRequestsCount;
    // L — текущая длина очереди
    private int queueLength;
    // LOS — счетчик отказов (заявок, поучивших отказ в обслуживании)
    private int rejectionsCount;
    // 1/m
    private final double serviceTime;
    //--------------------------------------------statistics---------------------------------------------

    //----------------------------------------------arrays-----------------------------------------------
    // OCP[i] — признак занятости i-го канала (0 — канал свободен, 1 — канал занят)
    private final Boolean[] busyChannels;
    // TD[i] — ожидаемый момент выхода заявки из i-го канала (время, прошедшее с начала моделирования).
    private final double[] channelReleaseExpectedTime;
    // TOS[i] — счетчик суммарного времени занятости i-го канала —
    // сколько единиц модельного времени в течение всего процесса моделирования канал был занят
    private final double[] chanelTotalBusyTime;
    // TL[M] — суммарное время пребывания системы в состоянии, когда в ней ровно M заявок
    private final double[] systemTotalTimeWithRequests;
    //----------------------------------------------arrays-----------------------------------------------

    //----------------------------------------------service----------------------------------------------
    // MIN — ближайший момент выхода обслуженной заявки из канала, считая от текущего модельного времени
    private double nearestMomentOfRequestRelease;
    // S — номер канала, который в текущем состоянии системы освободится первым (в момент времени MIN)
    private int firstReleasedChannelNumber;
    // M - количество заявок в системе
    private int requestsInSystemCount;
    // DTA — время между приходами заявок (генерируемая в процессе моделирования случайная величина)
    private Iterator<Double> timeBetweenRequests;
    // DTS — время обслуживания заявки в канале (генерируемая в процессе моделирования случайная величина)
    private Iterator<Double> requestServiceTime;
    //----------------------------------------------service----------------------------------------------

    //----------------------------------------------setup------------------------------------------------
    public SimulationByEvents(int numberOfRequests,
                              int numberOfChannels,
                              int queueSize,
                              double inputIntensity,
                              double serviceIntensity,
                              List<Double> timeBetweenRequestsSequence,
                              List<Double> requestServiceTimeSequence) {
        this.numberOfRequests = numberOfRequests;
        this.numberOfChannels = numberOfChannels;
        this.queueSize = queueSize;
        this.inputIntensity = inputIntensity;
        this.serviceIntensity = serviceIntensity;
        this.timeBetweenRequestsSequence = timeBetweenRequestsSequence;
        this.requestServiceTimeSequence = requestServiceTimeSequence;

        serviceTime = 1.0 / serviceIntensity;

        busyChannels = new Boolean[numberOfChannels];
        channelReleaseExpectedTime = new double[numberOfChannels];
        chanelTotalBusyTime = new double[numberOfChannels];
        systemTotalTimeWithRequests = new double[numberOfChannels + queueSize + 1];

        reset();
    }

    private void iteratorsSetup() {
        timeBetweenRequests = timeBetweenRequestsSequence
                .stream()
                .map(randDouble -> -1.0 / inputIntensity * Math.log(randDouble))
                .iterator();

        requestServiceTime = requestServiceTimeSequence
                .stream()
                .map(randDouble -> -1 * serviceTime * Math.log(randDouble))
                .iterator();
    }

    private void reset() {
        modelTime = 0;
        timeUtilNextRequest = 0;
        requestsCount = 0;
        servicedRequestsCount = 0;
        queueLength = 0;
        rejectionsCount = 0;
        nearestMomentOfRequestRelease = 0;
        firstReleasedChannelNumber = 0;
        requestsInSystemCount = 0;
        iteratorsSetup();

        Arrays.fill(busyChannels, false);
        Arrays.fill(channelReleaseExpectedTime, Integer.MAX_VALUE);
        Arrays.fill(chanelTotalBusyTime, 0);
        Arrays.fill(systemTotalTimeWithRequests, 0);
    }
    //----------------------------------------------setup------------------------------------------------

    //--------------------------------------------execution----------------------------------------------
    public void run() {
        reset();

        timeUtilNextRequest += timeBetweenRequests.next();
        requestsCount++;

        while (requestsCount < numberOfRequests) {
            firstReleasedChannelNumber = 1;
            nearestMomentOfRequestRelease = channelReleaseExpectedTime[firstReleasedChannelNumber - 1];

            int i = 1;
            do {
                if (channelReleaseExpectedTime[i - 1] < nearestMomentOfRequestRelease) {
                    firstReleasedChannelNumber = i;
                    nearestMomentOfRequestRelease = channelReleaseExpectedTime[i - 1];
                }
                i++;
            } while (i <= numberOfChannels);

            if (timeUtilNextRequest < nearestMomentOfRequestRelease) {
                requestsInSystemCount = queueLength
                        + (int) Stream.of(busyChannels)
                                .filter(isChanelFree -> isChanelFree)
                                .count();
                systemTotalTimeWithRequests[requestsInSystemCount] += timeUtilNextRequest - modelTime;
                modelTime = timeUtilNextRequest;

                i = 1;
                boolean breakFlag = false;
                do {
                    if (busyChannels[i - 1]) {
                        i++;
                    } else {
                        busyChannels[i - 1] = true;

                        double currentRequestServiceTime = requestServiceTime.next();
                        channelReleaseExpectedTime[i - 1] = modelTime + currentRequestServiceTime;
                        chanelTotalBusyTime[i - 1] += currentRequestServiceTime;

                        breakFlag = true;
                        break;
                    }
                } while (i <= numberOfChannels);

                if (!breakFlag) {
                    if (queueLength == queueSize) {
                        rejectionsCount++;
                    } else {
                        queueLength++;
                    }
                }

                timeUtilNextRequest += timeBetweenRequests.next();
                requestsCount++;
            } else {
                requestsInSystemCount = queueLength
                        + (int) Stream.of(busyChannels)
                                .filter(isChanelFree -> isChanelFree)
                                .count();
                systemTotalTimeWithRequests[requestsInSystemCount]
                        += channelReleaseExpectedTime[firstReleasedChannelNumber - 1] - modelTime;

                modelTime = channelReleaseExpectedTime[firstReleasedChannelNumber - 1];
                servicedRequestsCount++;

                if (queueLength == 0) {
                    busyChannels[firstReleasedChannelNumber - 1] = false;
                    channelReleaseExpectedTime[firstReleasedChannelNumber - 1] = Integer.MAX_VALUE;
                } else {
                    queueLength--;

                    double currentRequestServiceTime = requestServiceTime.next();
                    channelReleaseExpectedTime[firstReleasedChannelNumber - 1] = modelTime + currentRequestServiceTime;
                    chanelTotalBusyTime[firstReleasedChannelNumber - 1] += currentRequestServiceTime;
                }
            }
        }
    }
    //--------------------------------------------execution----------------------------------------------

    //---------------------------------------------results-----------------------------------------------
    public void printStatus() {
        int cutWidth = 22;
        System.out.println("-".repeat(cutWidth) + "status" + "-".repeat(cutWidth));
        System.out.println("numberOfRequests = " + numberOfRequests);
        System.out.println("numberOfChannels = " + numberOfChannels);
        System.out.println("queueSize = " + queueSize);
        System.out.println("inputIntensity = " + inputIntensity);
        System.out.println("serviceIntensity = " + serviceIntensity);
        System.out.println("firstSequence = " + timeBetweenRequestsSequence.size());
        System.out.println("secondSequence = " + requestServiceTimeSequence.size());
        System.out.println();
        System.out.println("modelTime = " + modelTime);
        System.out.println("timeUtilNextRequest = " + timeUtilNextRequest);
        System.out.println("requestsCount = " + requestsCount);
        System.out.println("servicedRequestsCount = " + servicedRequestsCount);
        System.out.println("queueLength = " + queueLength);
        System.out.println("rejectionsCount = " + rejectionsCount);
        System.out.println("serviceTime = " + serviceTime);
        System.out.println();
        System.out.println("nearestMomentOfRequestRelease = " + nearestMomentOfRequestRelease);
        System.out.println("firstReleasedChannelNumber = " + firstReleasedChannelNumber);
        System.out.println("requestsInSystemCount = " + requestsInSystemCount);
        System.out.println("-".repeat(cutWidth) + "status" + "-".repeat(cutWidth));
    }

    public void printResults() {
        int cutWidth = 22;
        System.out.println("-".repeat(cutWidth) + "result" + "-".repeat(cutWidth));
        System.out.println("number of requests: " + (rejectionsCount + servicedRequestsCount));
        System.out.println("number of serviced requests: " + servicedRequestsCount);
        System.out.println("number of rejections: " + rejectionsCount);
        System.out.println("model time: " + modelTime);

        System.out.println("probabilities of queuing system states:");
        for (int i = 0; i <= numberOfChannels + queueSize; i++) {
            System.out.printf("p%d = %.15f%n", i, systemTotalTimeWithRequests[i] / modelTime);
        }

        System.out.println("rejection probability: "
                + (double) rejectionsCount / (rejectionsCount + servicedRequestsCount));
        System.out.println("load factor: " + (1 - systemTotalTimeWithRequests[0] / modelTime));
        System.out.println("bandwidth: " + (double) servicedRequestsCount / modelTime);

        double average = 0;
        for (int i = 1; i <= numberOfChannels + queueSize; i++) {
            average += i * systemTotalTimeWithRequests[i] / modelTime;
        }
        System.out.println("average number of requests: " + average);

        System.out.println("average number of busy channels: "
                + Arrays.stream(chanelTotalBusyTime).sum() / modelTime);

        double sum = 0;
        for (int i = numberOfChannels + 1; i <= numberOfChannels + queueSize; i++) {
            sum += systemTotalTimeWithRequests[i] * (i - numberOfChannels);
        }
        System.out.println("average queue length: " + sum / modelTime);

        double b = 0;
        double c = Arrays.stream(chanelTotalBusyTime).sum();
        for (int i = 0; i <= numberOfChannels + queueSize; i++) {
            b += i * systemTotalTimeWithRequests[i];
        }
        System.out.println("average waiting time: " + (b - c) / numberOfRequests);
        System.out.println("-".repeat(cutWidth) + "result" + "-".repeat(cutWidth));
    }
    //---------------------------------------------results-----------------------------------------------
}
