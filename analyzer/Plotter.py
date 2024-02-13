import json
import matplotlib.pyplot as plt

def primoGrafico():
    # Carica i dati dal file JSON
    with open("statistic/nuovoScheduler/PerSchedule/mediaDiffLevel.json") as file:
        dati = json.load(file)

    # Estrai i valori delle colonne
    general_priority = [dati[str(i)]['general_priority'] for i in range(len(dati))]
    night_priority = [dati[str(i)]['night_priority'] for i in range(len(dati))]
    long_shift_priority = [dati[str(i)]['long_shift_priority'] for i in range(len(dati))]

    # Crea il plot
    plt.figure(figsize=(10, 6))

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
    plt.legend()
    # Mostra il plot
    plt.show()
def secondoGrafico(nameFile):
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

    # Plot dei dati generali
    plt.plot(giornaliero, label='Giornaliero', color='blue')

    # Plot dei dati notturni
    plt.plot(pomeridiano, label='Pomeridiano', color='red')

    # Plot dei dati dei turni lunghi
    plt.plot(notturno, label='Notturno', color='green')

    plt.plot(domeniche, label='Domeniche', color='yellow')

    plt.plot(lunga, label='Lunghe', color='pink')
    # Aggiungi titolo e legenda
    plt.title('Andamento livelli di priorità')
    plt.xlabel('Indice')
    plt.ylabel('Priorità')
    plt.legend()
    # Mostra il plot
    plt.show()

primoGrafico()

nameFile="statistic/nuovoScheduler/PerSchedule/mediaDiff.json"
secondoGrafico(nameFile)

nameFile="statistic/nuovoScheduler/PerSchedule/media.json"
secondoGrafico(nameFile)