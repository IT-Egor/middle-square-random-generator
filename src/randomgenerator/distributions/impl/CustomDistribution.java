package randomgenerator.distributions.impl;

import randomgenerator.distributions.Distribution;

public class CustomDistribution implements Distribution {

    private final double left = 0.;
    private final double right = 6.;

    @Override
    public double f(double x) {
        if (0 <= x && x <= 1) {
            return 2./9 * x;
        } else if (1 < x && x <= 4) {
            return 2./9;
        } else if (4 < x && x <= 6) {
            return 1./9;
        } else {
            return 0;
        }
    }

    @Override
    public double F(double x) {
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
    public double x(double F) {
        if (0 <= F && F <= 1./9) {
            return 3 * Math.sqrt(F);
        } else if (1./9 < F && F <= 7./9) {
            return 9./2 * F + 1./2;
        } else if (7./9 < F && F <= 1) {
            return 9 * F - 3;
        } else if (F > 1) {
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
