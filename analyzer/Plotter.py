import json
import matplotlib.pyplot as plt
import numpy as np

def primoGrafico():
    # Carica i dati dal file JSON
    with open("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json") as file:
        dati = json.load(file)
    # Estrai i valori delle colonne
    general_priority = [dati[str(i)]['general_priority'] for i in range(len(dati))]
    night_priority = [dati[str(i)]['night_priority'] for i in range(len(dati))]
    long_shift_priority = [dati[str(i)]['long_shift_priority'] for i in range(len(dati))]

    with open("statistic/vecchioScheduler/PerSchedule/mediaDiff.json") as file:
        dati2 = json.load(file)
    uffa_point = [dati2[str(i)]['uffaPoint'] for i in dati2]
    uffa_point = np.divide(uffa_point,5)
    # Crea il plot
    plt.figure(figsize=(10, 6))

    plt.subplot(1, 2, 1)  # Primo grafico
    # Plot dei dati generali
    plt.plot(general_priority, label='General Priority', color='blue')

    # Plot dei dati notturni
    plt.plot(night_priority, label='Night Priority', color='red')

    # Plot dei dati dei turni lunghi
    plt.plot(long_shift_priority, label='Long Shift Priority', color='green')

    # Aggiungi titolo e legenda
    plt.title('Andamento livelli di priorità')
    plt.xlabel('Indice')
    plt.ylabel('Priorità')
    #plt.ylim(0, max(max(general_priority), max(night_priority), max(long_shift_priority), max(uffa_point)))  # Imposta il limite dell'asse y
    plt.legend()

    plt.subplot(1, 2, 2)  # Secondo grafico
    plt.plot(uffa_point, label='Uffa point', color='red')
    #plt.ylim(0, max(max(general_priority), max(night_priority), max(long_shift_priority), max(uffa_point)))  # Imposta il limite dell'asse y
    # Aggiungi titolo e legenda
    plt.title('Andamento dei punti di uffa')
    plt.xlabel('Indice')
    plt.ylabel('Priorità')
    plt.legend()

    # Mostra il plot
    plt.show()
def secondoGrafico(nameFile,nameFile2):
    # Carica i dati dal file JSON
    with open(nameFile) as file:
        dati = json.load(file)
    # Estrai i valori delle colonne
    giornaliero = [dati[str(i)]['giornaliero'] for i in dati]
    pomeridiano = [dati[str(i)]['pomeridiano'] for i in dati]
    notturno = [dati[str(i)]['notturno'] for i in dati]
    lunga = [dati[str(i)]['lunga'] for i in dati]
    domeniche = [dati[str(i)]['domeniche'] for i in dati]
    # Crea il plot
    plt.figure(figsize=(10, 6))

    with open(nameFile2) as file:
        dati2 = json.load(file)
    giornaliero2 = [dati2[str(i)]['giornaliero'] for i in dati2]
    pomeridiano2 = [dati2[str(i)]['pomeridiano'] for i in dati2]
    notturno2 = [dati2[str(i)]['notturno'] for i in dati2]
    lunga2 = [dati2[str(i)]['lunga'] for i in dati2]
    domeniche2 = [dati2[str(i)]['domeniche'] for i in dati2]
    # Plot dei dati generali
    plt.subplot(1, 2, 1)  # Primo grafico
    plt.grid(True);
    plt.plot(giornaliero, label='Giornaliero', color='blue')
    # Plot dei dati notturni
    plt.plot(pomeridiano, label='Pomeridiano', color='red')
    # Plot dei dati dei turni lunghi
    plt.plot(notturno, label='Notturno', color='green')
    plt.plot(domeniche, label='Domeniche', color='orange')
    plt.plot(lunga, label='Lunghe', color='purple')
    # Aggiungi titolo e legenda
    plt.title('Andamento livelli di priorità del nuovo scheduler')
    plt.xlabel('Indice')
    plt.ylabel('Priorità')
    plt.legend()

    plt.subplot(1, 2, 2)  # Primo grafico
    plt.grid(True);
    plt.plot(giornaliero2, label='Giornaliero', color='blue')
    # Plot dei dati notturni
    plt.plot(pomeridiano2, label='Pomeridiano', color='red')
    # Plot dei dati dei turni lunghi
    plt.plot(notturno2, label='Notturno', color='green')
    plt.plot(domeniche2, label='Domeniche', color='orange')
    plt.plot(lunga2, label='Lunghe', color='purple')
    # Aggiungi titolo e legenda
    plt.title('Andamento degli uffaPoint del vecchio scheduler')
    plt.xlabel('Indice')
    plt.ylabel('UffaPoint')
    plt.legend()

# Mostra il plot
    plt.show()

primoGrafico()

nameFile="statistic/nuovoScheduler/PerSchedule/mediaDiff.json"
nameFile2="statistic/vecchioScheduler/PerSchedule/mediaDiff.json"
secondoGrafico(nameFile,nameFile2)

nameFile="statistic/nuovoScheduler/PerSchedule/media.json"
nameFile2="statistic/vecchioScheduler/PerSchedule/media.json"
secondoGrafico(nameFile,nameFile2)