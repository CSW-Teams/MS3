import pandas as pd
from scipy.stats import ttest_ind
from scipy.stats import ks_2samp
def func_test_t():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json")
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiffLevel.json")
    KS_confronto={}
    for k in dfNuovo.columns:
        KS_confronto[k]= ks_2samp(dfNuovo[k], dfVecchio[k])
        print("Test t su "+k+":")
        print("Statistiche t:", KS_confronto[k][0])
        print("Valore p:", KS_confronto[k][1])
def func_test_KS():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json")
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiffLevel.json")
    t_confronto={}
    for k in dfNuovo.columns:
        t_confronto[k]= ttest_ind(dfNuovo[k], dfVecchio[k])
        print("Test t su "+k+":")
        print("Statistiche t:", t_confronto[k][0])
        print("Valore p:", t_confronto[k][1])

func_test_t()
func_test_KS()


