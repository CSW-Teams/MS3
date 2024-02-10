import pandas as pd
from scipy.stats import ttest_ind
from scipy.stats import ks_2samp
from scipy.stats import shapiro
def func_test_t():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiffLevel.json").T
    KS_confronto={}
    for k in dfNuovo.columns:
        KS_confronto[k]= ks_2samp(dfNuovo[k], dfVecchio[k])
        print("Test t su "+k+":")
        print("Statistiche t:", KS_confronto[k][0])
        print("Valore p:", KS_confronto[k][1])
def func_test_KS():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiffLevel.json").T
    t_confronto={}
    for k in dfNuovo.columns:
        t_confronto[k]= ttest_ind(dfNuovo[k], dfVecchio[k])
        print("Test t su "+k+":")
        print("Statistiche t:", t_confronto[k][0])
        print("Valore p:", t_confronto[k][1])
def func_test_sw():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json").T
    # Esegui il test di Shapiro-Wilk
    print(dfNuovo.columns)
    for k in dfNuovo.columns:
        print("colonnna",k)
        statistica_sw, p_valore = shapiro(dfNuovo[k])
        # Stampa il risultato del test
        print("Statistiche di Shapiro-Wilk:", statistica_sw)
        print("Valore p:", p_valore)
        # Interpretazione del risultato
        if p_valore > 0.05:
            print("La distribuzione sembra essere gaussiana (normale).")
        else:
            print("La distribuzione non sembra essere gaussiana (non normale).")
#func_test_t()
#func_test_KS()
func_test_sw()



