package mds.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.lang.Math;

public class Model {
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
    private int modelTime;
    // TA — момент прихода очередной заявки (время, прошедшее с начала моделирования)
    private int timeOfNextRequest;
    // K — счетчик пришедших заявок
    private int requestsCount;
    // KS — счетчик обслуженных заявок
    private int servedRequestsCount;
    // L — текущая длина очереди
    private int queueLength;
    // LOS — счетчик отказов (заявок, поучивших отказ в обслуживании)
    private int bounceCount;

    private double serviceTime;
    //--------------------------------------------statistics---------------------------------------------

    //----------------------------------------------arrays-----------------------------------------------
    // OCP[i] — признак занятости i-го канала (0 — канал свободен, 1 — канал занят)
    private boolean[] busyChannels;
    // TD[i] — ожидаемый момент выхода заявки из i-го канала (время, прошедшее с начала моделирования).
    private int[] channelReleaseExpectedTime;
    // TOS[i] — счетчик суммарного времени занятости i-го канала —
    // сколько единиц модельного времени в течение всего процесса моделирования канал был занят
    private int[] chanelTotalBusyTime;
    // TL[M] — суммарное время пребывания системы в состоянии, когда в ней ровно M заявок
    private int[] systemTotalTimeWithRequests;
    //----------------------------------------------arrays-----------------------------------------------

    //----------------------------------------------helpers----------------------------------------------
    // MIN — ближайший момент выхода обслуженной заявки из канала, считая от текущего модельного времени
    private int nearestMomentOfRequestRelease;
    // S — номер канала, который в текущем состоянии системы освободится первым (в момент времени MIN)
    private int firstReleasedChannelNumber;
    // DTA — время между приходами заявок (генерируемая в процессе моделирования случайная величина)
    private Iterator<Double> timeBetweenRequests;
    // DTS — время обслуживания заявки в канале (генерируемая в процессе моделирования случайная величина)
    private Iterator<Double> requestServiceTime;
    //----------------------------------------------helpers----------------------------------------------


    public Model(int numberOfRequests,
                 int numberOfChannels,
                 int queueSize,
                 double l,
                 double m,
                 List<Double> timeBetweenRequestsSequence,
                 List<Double> requestServiceTimeSequence) {
        this.numberOfRequests = numberOfRequests;
        this.numberOfChannels = numberOfChannels;
        this.queueSize = queueSize;
        this.l = l;
        this.m = m;
        this.timeBetweenRequestsSequence = timeBetweenRequestsSequence;
        this.requestServiceTimeSequence = requestServiceTimeSequence;

        serviceTime = 1.0 / m;

        busyChannels = new boolean[numberOfChannels];
        channelReleaseExpectedTime = new int[numberOfChannels];
        chanelTotalBusyTime = new int[numberOfChannels];
        systemTotalTimeWithRequests = new int[numberOfChannels + queueSize + 1];

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

    private void reset() {
        modelTime = 0;
        timeOfNextRequest = 0;
        requestsCount = 0;
        servedRequestsCount = 0;
        queueLength = 0;
        bounceCount = 0;
        nearestMomentOfRequestRelease = 0;
        firstReleasedChannelNumber = 0;
        iteratorsSetup();

        Arrays.fill(busyChannels, false);
        Arrays.fill(channelReleaseExpectedTime, Integer.MAX_VALUE);
        Arrays.fill(chanelTotalBusyTime, 0);
        Arrays.fill(systemTotalTimeWithRequests, 0);
    }

    public Iterator<Double> getTimeBetweenRequests() {
        return timeBetweenRequests;
    }

    public Iterator<Double> getRequestServiceTime() {
        return requestServiceTime;
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
        System.out.println("servedRequestsCount = " + servedRequestsCount);
        System.out.println("queueLength = " + queueLength);
        System.out.println("bounceCount = " + bounceCount);
    }
}
