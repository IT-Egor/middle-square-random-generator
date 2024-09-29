package randomgenerator;

import java.util.ArrayList;
import java.util.List;

public class Histogram {
    private List<Integer> histogram;
    private int segments;
    private double left;
    private double right;

    public Histogram(int segments, double left, double right) {
        this.segments = segments;
        this.left = left;
        this.right = right;
        this.histogram = new ArrayList<>(segments);
        for (int i = 0; i < segments; i++) {
            histogram.add(0);
        }
    }

    public List<Integer> getHist() {
        return histogram;
    }

    public void addToHist(Double value) {
        int partIndex = (int) (((value - left) / (right - left)) * segments);
        histogram.set(partIndex, histogram.get(partIndex)+1);
    }

    public void addListToHist(List<Double> values) {
        for (double value : values) {
            addToHist(value);
        }
    }

    public void clearHist() {
        for (int i = 0; i < segments; i++) {
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
        return (double) getHistSum() / segments;
    }
}
