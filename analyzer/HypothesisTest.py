import pandas as pd
from scipy.stats import ttest_ind
from scipy.stats import ks_2samp
from scipy.stats import shapiro
def func_test_t():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiff.json").T
    t_confronto={}
    for k in dfNuovo.columns:
        t_confronto[k]= ttest_ind(dfNuovo[k], dfVecchio["uffaPoint"],equal_var = False)
        print("\nTest t su "+str(k)+":")
        print("Statistiche t:", t_confronto[k][0])
        print("Valore p:", t_confronto[k][1])
def func_test_KS():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiff.json").T
    t_confronto={}
    for k in dfNuovo.columns:
        t_confronto[k]= ks_2samp(dfNuovo[k], dfVecchio["uffaPoint"])
        print("\nTest KS su "+k+":")
        print("Statistiche ks:", t_confronto[k][0])
        print("Valore p:", t_confronto[k][1])

def func_test_sw():
    dfNuovo = pd.read_json("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiff.json").T
    statistica_sw, p_valore = shapiro(dfVecchio["uffaPoint"])
    print("\nTest di SW sugli uffa point")
    print("Statistiche di Shapiro-Wilk:", statistica_sw)
    print("Valore p:", p_valore)
    # Interpretazione del risultato
    if p_valore > 0.05:
        print("La distribuzione sembra essere gaussiana (normale).")
    else:
        print("La distribuzione non sembra essere gaussiana (non normale).")
    # Esegui il test di Shapiro-Wilk
    for k in dfNuovo.columns:
        statistica_sw, p_valore = shapiro(dfNuovo[k])
        # Stampa il risultato del test
        print("\nTesti di S-W sulla colonna ",str(k))
        print("Statistiche di Shapiro-Wilk:", statistica_sw)
        print("Valore p:", p_valore)
        # Interpretazione del risultato
        if p_valore > 0.05:
            print("La distribuzione sembra essere gaussiana (normale).")
        else:
            print("La distribuzione non sembra essere gaussiana (non normale).")
func_test_t()
func_test_KS()
func_test_sw()



