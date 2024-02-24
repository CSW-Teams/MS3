import pandas as pd
from scipy.stats import ttest_ind
from scipy.stats import ks_2samp
from scipy.stats import shapiro
from scipy.stats import chisquare

#il t-test sfrutta una Student per confrontare le medie di due v.a. supponendo però che siano v.a. gaussiane
def func_test_t():
    dfNuovo = pd.read_json("statistic/nuovoScheduler2/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiff.json").T
    t_confronto={}
    for k in dfNuovo.columns:
        t_confronto[k]= ttest_ind(dfNuovo[k], dfVecchio["uffaPoint"],equal_var = False)
        print("\nTest t su "+str(k)+":")
        print("Statistiche t:", t_confronto[k][0])
        print("Valore p:", t_confronto[k][1])

#il KS test confronta le medie di due v.a. supponendo che siano v.a. con la stessa varianza
def func_test_KS():
    dfNuovo = pd.read_json("statistic/nuovoScheduler2/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiff.json").T
    t_confronto={}
    for k in dfNuovo.columns:
        t_confronto[k]= ks_2samp(dfNuovo[k], dfVecchio["uffaPoint"])
        print("\nTest KS su "+k+":")
        print("Statistiche ks:", t_confronto[k][0])
        print("Valore p:", t_confronto[k][1])

#il SW test stabilisce la natura di una variabile aleatoria (confrontandola e.g. con la gaussiana)
def func_test_sw():
    dfNuovo = pd.read_json("statistic/nuovoScheduler2/PerSchedule/mediaDiffLevel.json").T
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

#il SW test stabilisce la natura di una variabile aleatoria (confrontandola e.g. con l'uniforme)
def func_test_q2():
    dfNuovo = pd.read_json("statistic/nuovoScheduler2/PerSchedule/mediaDiffLevel.json").T
    dfVecchio = pd.read_json("statistic/vecchioScheduler/PerSchedule/mediaDiff.json").T
    statistica_sw, p_valore = chisquare(dfVecchio["uffaPoint"])
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
        statistica_sw, p_valore = chisquare(dfNuovo[k])
        # Stampa il risultato del test
        print("\nTesti chisquare per colonna ",str(k))
        print("Statistiche di chisquare: ", statistica_sw)
        print("Valore p: ", p_valore)
        # Interpretazione del risultato
        if p_valore > 0.05:
            print("La distribuzione sembra essere uniforme")
        else:
            print("La distribuzione non sembra non essere uniforme.")

func_test_t()   #t-test
func_test_KS()  #test di Kolmogorov-Smirnov
func_test_sw()  #test di Shapiro-Will
func_test_q2()  #test del Chi-square
