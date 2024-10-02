package randomgenerator.distributions.impl;

import randomgenerator.distributions.Distribution;

public class CustomDistribution implements Distribution {
    private final double left = 0.;
    private final double right = 6.;
    private final double height = 2./9;

    @Override
    public double pdf(double x) {
        if (0 <= x && x <= 1) {
            return height * x;
        } else if (1 < x && x <= 4) {
            return height;
        } else if (4 < x && x <= 6) {
            return height / 2;
        } else {
            return 0;
        }
    }

    @Override
    public double cdf(double x) {
        if (0 <= x && x <= 1) {
            return 1./9 * x * x;
        } else if (1 < x && x <= 4) {
            return 2./9 * x - 1./9;
        } else if (4 < x && x <= 6) {
            return 1./9 * x + 1./3;
        } else if (x > 6) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public double qf(double cdf) {
        if (0 <= cdf && cdf <= 1./9) {
            return 3 * Math.sqrt(cdf);
        } else if (1./9 < cdf && cdf <= 7./9) {
            return 9./2 * cdf + 1./2;
        } else if (7./9 < cdf && cdf <= 1) {
            return 9 * cdf - 3;
        } else if (cdf > 1) {
            return 0;
        } else {
            return 0;
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
