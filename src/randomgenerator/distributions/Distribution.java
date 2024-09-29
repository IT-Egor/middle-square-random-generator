package randomgenerator.distributions;

public interface Distribution {
    double f(double x);

    double F(double x);

    double x(double F);
}
