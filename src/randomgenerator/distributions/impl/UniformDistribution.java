package randomgenerator.distributions.impl;

import randomgenerator.distributions.Distribution;

public class UniformDistribution implements Distribution {
    private final double left = 0.;
    private final double right = 1.;

    @Override
    public double f(double x) {
        if (x < left || x > right) {
            return 0;
        } else {
            return 1. / (right - left);
        }
    }

    @Override
    public double F(double x) {
        if (x < left) {
            return 0;
        } else if (x > right) {
            return 1;
        } else {
            return (x - left) / (right - left);
        }
    }

    @Override
    public double x(double F) {
        return 0;
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
