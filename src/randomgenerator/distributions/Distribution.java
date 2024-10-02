package randomgenerator.distributions;

public interface Distribution {
    //probabilityDensityFunction
    double pdf(double x);

    //cumulativeDistributionFunction
    double cdf(double x);

    //quantileFunction
    double qf(double cdf);

    double getLeft();

    double getRight();
}
