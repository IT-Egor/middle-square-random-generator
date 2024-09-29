from scipy import stats
import sys
import matplotlib.pyplot as plt
import numpy as np

# https://stackoverflow.com/questions/11725115/p-value-from-chi-sq-test-statistic-in-python

observed_frequencies = [int(x) for x in sys.argv[1].split(", ")]
expected_frequencies = [int(x) for x in sys.argv[2].split(", ")]
observe_length = len(observed_frequencies)

chi2 = sum([(observed_frequencies[i] - expected_frequencies[i])**2 / expected_frequencies[i] for i in range(observe_length)])
p_value = 1 - stats.chi2.cdf(chi2, observe_length - 1)

print(f"Chi-square statistics: {chi2}")
print(f"P-value: {p_value}")

x = np.arange(1, len(observed_frequencies)+1)

width = 0.35
plt.bar(x - width/2, observed_frequencies, width=width, color="black", label="Observed")
plt.bar(x + width/2, expected_frequencies, width=width, color="red", label="Expected")
plt.title(f"P = {p_value}")
plt.legend()
plt.show()