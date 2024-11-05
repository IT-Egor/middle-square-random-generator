package mds.model;

import java.util.*;
import java.lang.Math;

public class Simulation {
    //------------------------------------input------------------------------------
    // N — максимальное число входящих заявок (условие окончания моделирования)
    private final int numberOfRequests;
    // NK — количество каналов
    private final int numberOfChannels;
    // LMAX — максимально допустимая длина очереди
    private final int queueSize;
    // интенсивность потока заявок (лямбда)
    private final double l;
    // интенсивность потока обработки заявок (мю)
    private final double m;

    private final List<Double> timeBetweenRequestsSequence;
    private final List<Double> requestServiceTimeSequence;
    //------------------------------------input------------------------------------

    //--------------------------------------------statistics---------------------------------------------
    // T - текущее модельное время, изменяющееся скачком между моментами возникновения событий в системе
    private double modelTime;
    // TA — момент прихода очередной заявки (время, прошедшее с начала моделирования)
    private double timeOfNextRequest;
    // K — счетчик пришедших заявок
    private int requestsCount;
    // KS — счетчик обслуженных заявок
    private int servicedRequestsCount;
    // L — текущая длина очереди
    private int queueLength;
    // LOS — счетчик отказов (заявок, поучивших отказ в обслуживании)
    private int bounceCount;

    private final double serviceTime;
    //--------------------------------------------statistics---------------------------------------------

    //----------------------------------------------arrays-----------------------------------------------
    // OCP[i] — признак занятости i-го канала (0 — канал свободен, 1 — канал занят)
    private List<Boolean> busyChannels;
    // TD[i] — ожидаемый момент выхода заявки из i-го канала (время, прошедшее с начала моделирования).
    private double[] channelReleaseExpectedTime;
    // TOS[i] — счетчик суммарного времени занятости i-го канала —
    // сколько единиц модельного времени в течение всего процесса моделирования канал был занят
    private double[] chanelTotalBusyTime;
    // TL[M] — суммарное время пребывания системы в состоянии, когда в ней ровно M заявок
    private double[] systemTotalTimeWithRequests;
    //----------------------------------------------arrays-----------------------------------------------

    //----------------------------------------------helpers----------------------------------------------
    // MIN — ближайший момент выхода обслуженной заявки из канала, считая от текущего модельного времени
    private double nearestMomentOfRequestRelease;
    // S — номер канала, который в текущем состоянии системы освободится первым (в момент времени MIN)
    private int firstReleasedChannelNumber;
    // M - количество заявок в системе
    private int numberOfRequestsInSystem;
    // DTA — время между приходами заявок (генерируемая в процессе моделирования случайная величина)
    private Iterator<Double> timeBetweenRequests;
    // DTS — время обслуживания заявки в канале (генерируемая в процессе моделирования случайная величина)
    private Iterator<Double> requestServiceTime;
    //----------------------------------------------helpers----------------------------------------------


    public Simulation(int numberOfRequests,
                      int numberOfChannels,
                      int queueSize,
                      double l,
                      double numberOfRequestsInSystem,
                      List<Double> timeBetweenRequestsSequence,
                      List<Double> requestServiceTimeSequence) {
        this.numberOfRequests = numberOfRequests;
        this.numberOfChannels = numberOfChannels;
        this.queueSize = queueSize;
        this.l = l;
        this.m = numberOfRequestsInSystem;
        this.timeBetweenRequestsSequence = timeBetweenRequestsSequence;
        this.requestServiceTimeSequence = requestServiceTimeSequence;

        serviceTime = 1.0 / numberOfRequestsInSystem;

        busyChannels = new ArrayList<>();
        for (int i = 0; i < numberOfChannels; i++) {
            busyChannels.add(false);
        }
        channelReleaseExpectedTime = new double[numberOfChannels];
        chanelTotalBusyTime = new double[numberOfChannels];
        systemTotalTimeWithRequests = new double[numberOfChannels + queueSize + 1];

        reset();
        iteratorsSetup();
    }

    private void iteratorsSetup() {
        timeBetweenRequests = timeBetweenRequestsSequence
                .stream()
                .map(randDouble -> -1.0/l * Math.log(randDouble))
                .iterator();

        requestServiceTime = requestServiceTimeSequence
                .stream()
                .map(randDouble -> -1 * serviceTime * Math.log(randDouble))
                .iterator();
    }

    public void run() {
        reset();

        timeOfNextRequest += timeBetweenRequests.next();
        requestsCount++;

        while (requestsCount < numberOfRequests) {
            firstReleasedChannelNumber = 1;
            nearestMomentOfRequestRelease = channelReleaseExpectedTime[firstReleasedChannelNumber - 1];

            int i = 1;
            do {
                if (channelReleaseExpectedTime[i-1] < nearestMomentOfRequestRelease) {
                    firstReleasedChannelNumber = i;
                    nearestMomentOfRequestRelease = channelReleaseExpectedTime[i-1];
                }
                i++;
            } while (i <= numberOfChannels);

            if (timeOfNextRequest < nearestMomentOfRequestRelease) {
                numberOfRequestsInSystem = queueLength + (int) busyChannels.stream().filter(b -> b).count();
                systemTotalTimeWithRequests[numberOfRequestsInSystem] += timeOfNextRequest - modelTime;
                modelTime = timeOfNextRequest;

                i = 1;
                boolean breakFlag = false;
                do {
                    if (busyChannels.get(i - 1)) {
                        i++;
                    } else {
                        busyChannels.set(i - 1, true);

                        double dts = requestServiceTime.next();
                        channelReleaseExpectedTime[i - 1] = modelTime + dts;
                        chanelTotalBusyTime[i - 1] += dts;

                        breakFlag = true;
                        break;
                    }
                } while (i <= numberOfChannels);

                if (!breakFlag) {
                    if (queueLength == queueSize) {
                        bounceCount++;
                    } else {
                        queueLength++;
                    }
                }

                timeOfNextRequest += timeBetweenRequests.next();
                requestsCount++;
            } else {
                numberOfRequestsInSystem = queueLength + (int) busyChannels.stream().filter(b -> b).count();
                systemTotalTimeWithRequests[numberOfRequestsInSystem] += channelReleaseExpectedTime[firstReleasedChannelNumber - 1] - modelTime;

                modelTime = channelReleaseExpectedTime[firstReleasedChannelNumber - 1];
                servicedRequestsCount++;

                if (queueLength == 0) {
                    busyChannels.set(firstReleasedChannelNumber - 1, false);
                    channelReleaseExpectedTime[firstReleasedChannelNumber - 1] = Integer.MAX_VALUE;
                } else {
                    queueLength--;

                    double dts = requestServiceTime.next();
                    channelReleaseExpectedTime[firstReleasedChannelNumber - 1] = modelTime + dts;
                    chanelTotalBusyTime[firstReleasedChannelNumber - 1] += dts;
                }
            }

        }
    }

    private void reset() {
        modelTime = 0;
        timeOfNextRequest = 0;
        requestsCount = 0;
        servicedRequestsCount = 0;
        queueLength = 0;
        bounceCount = 0;
        nearestMomentOfRequestRelease = 0;
        firstReleasedChannelNumber = 0;
        numberOfRequestsInSystem = 0;
        iteratorsSetup();

        Collections.fill(busyChannels, false);
        Arrays.fill(channelReleaseExpectedTime, Integer.MAX_VALUE);
        Arrays.fill(chanelTotalBusyTime, 0);
        Arrays.fill(systemTotalTimeWithRequests, 0);
    }

    public void printStatus() {
        System.out.println("numberOfRequests = " + numberOfRequests);
        System.out.println("numberOfChannels = " + numberOfChannels);
        System.out.println("queueSize = " + queueSize);
        System.out.println("l = " + l);
        System.out.println("m = " + m);
        System.out.println("firstSequence = " + timeBetweenRequestsSequence.size());
        System.out.println("secondSequence = " + requestServiceTimeSequence.size());
        System.out.println();
        System.out.println("modelTime = " + modelTime);
        System.out.println("timeOfNextRequest = " + timeOfNextRequest);
        System.out.println("requestsCount = " + requestsCount);
        System.out.println("servicedRequestsCount = " + servicedRequestsCount);
        System.out.println("queueLength = " + queueLength);
        System.out.println("bounceCount = " + bounceCount);
        System.out.println("serviceTime = " + serviceTime);
        System.out.println();
        System.out.println("nearestMomentOfRequestRelease = " + nearestMomentOfRequestRelease);
        System.out.println("firstReleasedChannelNumber = " + firstReleasedChannelNumber);
        System.out.println("numberOfRequestsInSystem = " + numberOfRequestsInSystem);
    }

    public void printResult() {
        System.out.println("Кол-во заявок " + (bounceCount + servicedRequestsCount));
        System.out.println("Кол-во обработанных заявок " + servicedRequestsCount);
        System.out.println("Кол-во отказов " + bounceCount);
        System.out.println("Модельное время " + modelTime);
        System.out.println("Вероятности состояний СМО:");
        for (int i = 0; i <= numberOfChannels + queueSize; i++) {
            System.out.println(String.format("p%d = %f", i, systemTotalTimeWithRequests[i] / modelTime));
        }
        System.out.println("Вероятность отказа " + (double) bounceCount / (bounceCount + servicedRequestsCount));
        System.out.println("Коэффициент загрузки " + (1 - systemTotalTimeWithRequests[0] / modelTime));
        System.out.println("Пропускная способность " + (double) servicedRequestsCount / modelTime);
        double average = 0;
        for (int i = 1; i <= numberOfChannels + queueSize; i++) {
            average += i * systemTotalTimeWithRequests[i] / modelTime;
        }
        System.out.println("Среднее число заявок " + average);
        System.out.println("Среднее количество занятых каналов " + Arrays.stream(chanelTotalBusyTime).sum() / modelTime);
        double sum = 0;
        for (int i = numberOfChannels + 1; i <= numberOfChannels + queueSize; i++) {
            sum += systemTotalTimeWithRequests[i] * (i - numberOfChannels);
        }
        System.out.println("Средняя длина очереди " + sum / modelTime);
        double b = 0;
        double c = 0;
        for (int i = 0; i <= numberOfChannels + queueSize; i++) {
            b += i * systemTotalTimeWithRequests[i];
        }
        for (int i = 0; i < numberOfChannels; i++) {
            c += chanelTotalBusyTime[i];
        }
        System.out.println("Среднее время ожидания " + (b - c) / numberOfRequests);
    }
}
