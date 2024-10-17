package randomgenerator.service;

import randomgenerator.distributions.Distribution;

import java.util.ArrayList;
import java.util.List;

public class Histogram {
    private final List<Integer> histogramValues;
    private final int segmentsNumber;
    private final double left;
    private final double right;
    private final Distribution distribution;

    public Histogram(int segmentsNumber, Distribution distribution) {
        this.segmentsNumber = segmentsNumber;
        this.distribution = distribution;
        this.left = distribution.getLeft();
        this.right = distribution.getRight();
        this.histogramValues = new ArrayList<>(segmentsNumber);
        for (int i = 0; i < segmentsNumber; i++) {
            histogramValues.add(0);
        }
    }

    public List<Integer> getHistValues() {
        return histogramValues;
    }

    public void addToHist(Double value) {
        int partIndex = (int) (((value - left) / (right - left)) * segmentsNumber);
        histogramValues.set(partIndex, histogramValues.get(partIndex)+1);
    }

    public void addListToHist(List<Double> values) {
        for (double value : values) {
            addToHist(value);
        }
    }

    public void clearHist() {
        for (int i = 0; i < segmentsNumber; i++) {
            histogramValues.set(i, 0);
        }
    }

    public int getHistSum() {
        int sum = 0;
        for (int part : histogramValues) {
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
        double increment = (right - left) / segmentsNumber;
        for (double segment = 0; segment < right; segment += increment) {
            intervals.add(segment);
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
            pSegments.add((int) ((distribution.cdf(segment) - distribution.cdf(prevSegment)) * getHistSum()));
        }
        return pSegments;
    }
}
