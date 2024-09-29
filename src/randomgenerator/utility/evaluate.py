import matplotlib.pyplot as plt
import numpy as np
from scipy import stats
import sys

observed_frequencies = [int(x) for x in sys.argv[1].split(", ")]
chi2_statistic, p_value = stats.chisquare(observed_frequencies)

print(f"Chi-square statistics: {chi2_statistic}")
print(f"P: {p_value}")

x = np.arange(1, len(observed_frequencies)+1)
y = np.array([sum(observed_frequencies)/len(observed_frequencies)]*len(observed_frequencies))

plt.bar(x, observed_frequencies)
plt.plot(x, y, color="red")
plt.title(f"P = {p_value}")
plt.show()