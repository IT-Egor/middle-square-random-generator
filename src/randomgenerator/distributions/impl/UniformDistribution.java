package randomgenerator.distributions.impl;

import randomgenerator.distributions.Distribution;

public class UniformDistribution implements Distribution {
    private final double left = 0.;
    private final double right = 1.;
    private final double height = 1. / (right - left);

    @Override
    public double pdf(double x) {
        if (x < left || x > right) {
            return 0;
        } else {
            return height;
        }
    }

    @Override
    public double cdf(double x) {
        if (x < left) {
            return 0;
        } else if (x > right) {
            return 1;
        } else {
            return (x - left) / (right - left);
        }
    }

    @Override
    public double qf(double cdf) {
        if (cdf < 0 || cdf > 1) {
            return 0;
        } else {
            return cdf * (right - left) + left;
        }
    }

    @Override
    public double getLeft() {
        return left;
    }

    @Override
    public double getRight() {
        return right;
    }
}
