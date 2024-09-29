from scipy import stats
import sys
import matplotlib.pyplot as plt
import numpy as np

# https://stackoverflow.com/questions/11725115/p-value-from-chi-sq-test-statistic-in-python

# observed_frequencies = [109, 206, 383, 525, 694, 901, 1006, 1142, 1262, 1284, 1246, 1219, 1341, 1274, 1210, 1251, 1286, 1320, 1268, 1240, 1283, 1218, 1265, 1238, 1243, 1299, 1277, 1315, 1252, 1290, 1278, 1262, 1244, 861, 669, 635, 618, 652, 619, 666, 678, 552, 607, 628, 616, 624, 641, 595, 617, 699]
# expected_frequencies = [76, 228, 380, 533, 685, 837, 990, 1142, 1261, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 1269, 846, 634, 634, 634, 634, 634, 634, 634, 634, 634, 634, 634, 634, 634, 634, 634, 634]
observed_frequencies = [int(x) for x in sys.argv[1].split(", ")]
expected_frequencies = [int(x) for x in sys.argv[2].split(", ")]
observe_length = len(observed_frequencies)

chi2 = sum([(observed_frequencies[i] - expected_frequencies[i])**2 / expected_frequencies[i] for i in range(observe_length)])
p_value = 1 - stats.chi2.cdf(chi2, observe_length - 1)

x = np.arange(1, len(observed_frequencies)+1)

width = 0.35
plt.bar(x - width/2, observed_frequencies, width=width, color="black", label="Observed")
plt.bar(x + width/2, expected_frequencies, width=width, color="red", label="Expected")
plt.title(f"P = {p_value}")
plt.legend()
plt.show()