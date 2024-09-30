package randomgenerator.service;

import randomgenerator.distributions.Distribution;

import java.util.ArrayList;
import java.util.List;

public class Histogram {
    private List<Integer> histogram;
    private int segmentsNumber;
    private double left;
    private double right;
    private Distribution distribution;

    public Histogram(int segmentsNumber, Distribution distribution) {
        this.segmentsNumber = segmentsNumber;
        this.distribution = distribution;
        this.left = distribution.getLeft();
        this.right = distribution.getRight();
        this.histogram = new ArrayList<>(segmentsNumber);
        for (int i = 0; i < segmentsNumber; i++) {
            histogram.add(0);
        }
    }

    public List<Integer> getHist() {
        return histogram;
    }

    public void addToHist(Double value) {
        int partIndex = (int) (((value - left) / (right - left)) * segmentsNumber);
        histogram.set(partIndex, histogram.get(partIndex)+1);
    }

    public void addListToHist(List<Double> values) {
        for (double value : values) {
            addToHist(value);
        }
    }

    public void clearHist() {
        for (int i = 0; i < segmentsNumber; i++) {
            histogram.set(i, 0);
        }
    }

    public int getHistSum() {
        int sum = 0;
        for (int part : histogram) {
            sum += part;
        }
        return sum;
    }

    public double getHistAvg() {
        return (double) getHistSum() / segmentsNumber;
    }

    public int getSegmentsNumber() {
        return segmentsNumber;
    }

    public List<Double> getSegments() {
        List<Double> intervals = new ArrayList<>(segmentsNumber);
        for (double i = 0; i < right; i += right / segmentsNumber) {
            intervals.add(i);
        }
        return intervals;
    }

    public List<Integer> getProbabilityOfHittingTheSegment() {
        List<Double> segments = getSegments();
        List<Integer> pSegments = new ArrayList<>();
        for (int i = 1; i <= segmentsNumber; i++) {
            double segment;
            if (i == segmentsNumber) {
                segment = distribution.getRight();
            } else {
                segment = segments.get(i);
            }
            double prevSegment = segments.get(i - 1);
            pSegments.add((int) ((distribution.F(segment) - distribution.F(prevSegment)) * getHistSum()));
        }
        return pSegments;
    }
}
