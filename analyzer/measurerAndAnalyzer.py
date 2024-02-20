import pandas as pd
import pg8000
import requests
import datetime
import calendar
import json
import time
numeroMesi=48
user="sprintfloyd"
psw="sprintfloyd"

def funcMedia(dizionario,keys):
    # Conta il numero totale di elementi nel dizionario
    total={}
    num_elements = len(dizionario)
    for k in keys:
        total[k]=0
    # Somma i valori di "g", "p", e "n" su tutti gli elementi
    for item in dizionario.values():
        for k in keys:
            total[k] += item[k]
    avarage={}
    # Calcola le medie
    for k in keys:
        avarage[k]=float(total[k]/num_elements)
    return avarage

def funcMax(dizionario,keys):
    max={}
    for k in keys:
        max[k]=0
    # Somma i valori di "g", "p", e "n" su tutti gli elementi
    for item in dizionario.values():
        for k in keys:
            if max[k] < item[k] :
                max[k]=float(item[k])
    return max
def funcMin(dizionario,keys):
    idCasuale = list(dizionario.keys())[0]	#per avere la certezza di inizializzare le variabili di minimo con un valore non minore del minimo effettivo, si prende il valore relativo a uno dei medici scelto (pseudo-)casualmente.
    min={}
    for k in keys:
        min[k]=float(dizionario.get(idCasuale)[k]);
    # Somma i valori di "g", "p", e "n" su tutti gli elementi
    for item in dizionario.values():
        for k in keys:
            if min[k] > item[k] :
                min[k]=float(item[k])
    return min

def computazioneTotale(name):
# Configura la connessione al database
    conn = pg8000.connect(
        user=user,
        password=psw,
        host="127.0.0.1",
        port=5432,
        database="ms3"
    )
    keys=["giornaliero","pomeridiano","notturno","domeniche","lunga","general_priority","night_priority","long_shift_priority"]
    allUser = pd.read_sql("SELECT ms3_system_user_id FROM Doctor", con=conn)	#tutti gli utenti del sistema
    oggettoDaSalvare=computazione(allUser,keys)
    filesalv=open("./statistic/"+name+"/ToTSchedule/result_totale.json","w+")	#per le statistiche globali si crea un unico file json che riassume tutto (vedere commenti righe 143->148)
    json.dump(oggettoDaSalvare, filesalv, indent=2)

    allUser=pd.read_sql("SELECT ms3_system_user_id FROM Doctor WHERE seniority=0", con=conn)
    oggettoDaSalvare=computazione(allUser,keys)
    filesalv=open("./statistic/"+name+"/ToTSchedule/result_totalePerSeniority0.json","w+")	#per le statistiche globali si crea un unico file json che riassume tutto (vedere commenti righe 143->148)
    json.dump(oggettoDaSalvare, filesalv, indent=2)

    allUser=pd.read_sql("SELECT ms3_system_user_id FROM Doctor WHERE seniority=1", con=conn)
    oggettoDaSalvare=computazione(allUser,keys)
    filesalv=open("./statistic/"+name+"/ToTSchedule/result_totalePerSeniority1.json","w+")	#per le statistiche globali si crea un unico file json che riassume tutto (vedere commenti righe 143->148)
    json.dump(oggettoDaSalvare, filesalv, indent=2)

    allUser=pd.read_sql("SELECT ms3_system_user_id FROM Doctor WHERE seniority=2", con=conn)
    oggettoDaSalvare=computazione(allUser,keys)
    filesalv=open("./statistic/"+name+"/ToTSchedule/result_totalePerSeniority2.json","w+")	#per le statistiche globali si crea un unico file json che riassume tutto (vedere commenti righe 143->148)
    json.dump(oggettoDaSalvare, filesalv, indent=2)
def computazione(AllUser,keys):
    # Configura la connessione al database
    conn = pg8000.connect(
        user=user,
        password=psw,
        host="127.0.0.1",
        port=5432,
        database="ms3"
    )
    dictApp={}
    for ms3_id in AllUser["ms3_system_user_id"]:
        queryShiftTime="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE ds.doctor_ms3_system_user_id="+str(ms3_id)+" and shift.time_slot="
        count0 = pd.read_sql(queryShiftTime+"0", con=conn) #quanti turni giornalieri
        count1 = pd.read_sql(queryShiftTime+"1", con=conn) #quanti turni pomeridiani
        count2 = pd.read_sql(queryShiftTime+"2", con=conn) #quanti turni notturni
        querySunday="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE ds.doctor_ms3_system_user_id="+str(ms3_id)+" and cs.date%7=3"
        count3 = pd.read_sql(querySunday, con=conn)	#quanti turni domenicali
        queryDouble="SELECT count(*) as c from (SELECT count(*) as ctemp FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE ds.doctor_ms3_system_user_id="+str(ms3_id)+" and ds.concrete_shift_doctor_status=0 group by date having count(*)>=2) as temporary"
        count4 = pd.read_sql(queryDouble, con=conn)	#quanti turni lunghi
        queryLevel="SELECT night_priority,long_shift_priority,general_priority FROM doctor_uffa_priority WHERE doctor_ms3_system_user_id="+str(ms3_id)
        result5=pd.read_sql(queryLevel,conn)

        dictApp[ms3_id] = {
            "giornaliero":count0["c"].values[0],
            "pomeridiano":count1["c"].values[0],
            "notturno":count2["c"].values[0],
            "domeniche":count3["c"].values[0],
            "lunga":count4["c"].values[0],
            "general_priority":result5["general_priority"].values[0],
            "night_priority":result5["night_priority"].values[0],
            "long_shift_priority":result5["long_shift_priority"].values[0]
        }
    dictFinMedia=funcMedia(dictApp,keys)
    dictFindMax=funcMax(dictApp,keys)
    dictFindMin=funcMin(dictApp,keys)
    #calcolo differenza
    for item in dictApp.values():
        for k in keys:
            item[k] = abs(item[k]-dictFinMedia[k]);
    #calcolo media differenza
    dictFinDiffMedia=funcMedia(dictApp,keys)
    dictFinDiffMax=funcMax(dictApp,keys)
    dictFinDiffMin=funcMin(dictApp,keys)
    oggettoDaSalvare={
        "media_asseganzioni":dictFinMedia,
        "max_assegnazioni":dictFindMax,
        "min_assegnazioni":dictFindMin,
        "discostamento_medio_media_asseganzioni":dictFinDiffMedia,
        "discostamento_max_media_asseganzioni":dictFinDiffMax,
        "discostamento_min_media_asseganzioni":dictFinDiffMin,
    }
    return oggettoDaSalvare

def computazionePerSchedule(name):
    # Configura la connessione al database
    conn = pg8000.connect(
        user=user,
        password=psw,
        host="127.0.0.1",
        port=5432,
        database="ms3"
    )
    #crea i file in cui memorizzare le statistiche per schedule
    mediaFile=open("./statistic/"+name+"/PerSchedule/media.json","w+")		#media == numero medio di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo (e.g. turno notturno)
    mediaDiffFile=open("./statistic/"+name+"/PerSchedule/mediaDiff.json","w+")	#mediaDiff == media delle differenze tra il numero di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo e @media
    maxFile=open("./statistic/"+name+"/PerSchedule/max.json","w+")			#max == numero massimo di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo
    maxDiffFile=open("./statistic/"+name+"/PerSchedule/maxDiff.json","w+")		#maxDiff == differenza massima tra il numero di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo e @media
    minFile=open("./statistic/"+name+"/PerSchedule/min.json","w+")			#min == numero minimo di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo
    minDiffFile=open("./statistic/"+name+"/PerSchedule/minDiff.json","w+")		#minDiff == differenza minima tra il numero di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo e @media

    AllUser = pd.read_sql("SELECT ms3_system_user_id,name,lastname FROM Doctor", con=conn)	#tutti gli utenti del sistema
    AllSchedule = pd.read_sql("SELECT Schedule_id FROM Schedule", con=conn)			#tutti gli schedule che sono stati messi in piedi con generaSchedulazioni()
    #dizionari da cui verranno creati i json che popoleranno i file appena creati
    dictFinMedia={}
    dictFinDiffMedia={}
    dictFinDiffMax={}
    dictFindMax={}
    dictFindMin={}
    dictFinDiffMin={}
    keys=["giornaliero","pomeridiano","notturno","domeniche","lunga"]
    for schedule_id in AllSchedule["schedule_id"]:
        dictApp={}
        for ms3_id in AllUser["ms3_system_user_id"]:
            queryShiftTime="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE scs.schedule_schedule_id="+str(schedule_id)+" and ds.doctor_ms3_system_user_id="+str(ms3_id)+" and shift.time_slot="
            count0 = pd.read_sql(queryShiftTime+"0", con=conn) #quanti turni giornalieri
            count1 = pd.read_sql(queryShiftTime+"1", con=conn) #quanti turni pomeridiani
            count2 = pd.read_sql(queryShiftTime+"2", con=conn) #quanti turni notturni

            querySunday="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE scs.schedule_schedule_id="+str(schedule_id)+" and ds.doctor_ms3_system_user_id="+str(ms3_id)+" and cs.date%7=3"

            count3 = pd.read_sql(querySunday, con=conn)	#quanti turni domenicali

            queryDouble="SELECT count(*) as c from (SELECT count(*) as ctemp FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE scs.schedule_schedule_id="+str(schedule_id)+" and ds.doctor_ms3_system_user_id="+str(ms3_id)+" and ds.concrete_shift_doctor_status=0 group by date having count(*)>=2) as  temporary";

            count4 = pd.read_sql(queryDouble, con=conn)	#quanti turni lunghi

            dictApp[ms3_id] = {
                "giornaliero":count0["c"].values[0],
                "pomeridiano":count1["c"].values[0],
                "notturno":count2["c"].values[0],
                "domeniche":count3["c"].values[0],
                "lunga":count4["c"].values[0],
            }
        dictFinMedia[schedule_id]=funcMedia(dictApp,keys)
        dictFindMax[schedule_id]=funcMax(dictApp,keys)
        dictFindMin[schedule_id]=funcMin(dictApp,keys)

        #calcolo differenza
        for item in dictApp.values():
            for k in keys:
                item[k] = abs(item[k]-dictFinMedia[schedule_id][k]);
        #calcolo media differenza
        dictFinDiffMedia[schedule_id]=funcMedia(dictApp,keys)
        dictFinDiffMax[schedule_id]=funcMax(dictApp,keys)
        dictFinDiffMin[schedule_id]=funcMin(dictApp,keys)

    json.dump(dictFinMedia, mediaFile, indent=2)
    json.dump(dictFinDiffMedia, mediaDiffFile, indent=2)
    json.dump(dictFinDiffMax, maxDiffFile, indent=2)
    json.dump(dictFindMax, maxFile, indent=2)
    json.dump(dictFindMin, minFile, indent=2)
    json.dump(dictFinDiffMin, minDiffFile, indent=2)

def esegui_richiesta_post(data_inizio, data_fine,alg):
    url = "http://localhost:3000/api/schedule/generation"
    headers= { 'Content-Type': 'application/json' }
    parametri = {	#campi della struttura json che compone la richiesta http da inviare al servizio di backend che genera la schedulazione; questi campi corrispondono ai parametri della funzione.
        "initialDay": data_inizio.day,
        "initialMonth": data_inizio.month,
        "initialYear": data_inizio.year,
        "finalDay": data_fine.day,
        "finalMonth": data_fine.month,
        "finalYear": data_fine.year,
        "algorithm":alg
    }
    try:
        response = requests.post(url,headers=headers, data=json.dumps(parametri))	#recupero della risposta del server
        if response.status_code == 200 or response.status_code == 202 :	#caso in cui la chiamata al servizio è andata a buon fine
            print("Richiesta POST effettuata con successo!")
            print("Risposta dal server:", response.text)
        else:	#caso in cui la chiamata al servizio non è andata a buon fine
            print(f"Errore nella richiesta POST. Codice di stato: {response.status_code}")
            print("Dettagli dell'errore:", response.text)
    except requests.exceptions.RequestException as e:
        print(f"Errore nella connessione al server: {e}")

def generaSchedulazioni(name,alg):
    # Configura la connessione al database
    conn = pg8000.connect(
        user=user,
        password=psw,
        host="127.0.0.1",
        port=5432,
        database="ms3"
    )

    fileTmp=open(name+"tmp.txt","w+")

    #vengono generate tutte schedulazioni di durata pari a un mese
    data_attuale = datetime.datetime.now()
    deltaDay=calendar.monthrange(data_attuale.year,data_attuale.month)[1]
    mese_successivo = data_attuale.replace(day=1) + datetime.timedelta(days=deltaDay)

    mediaFile=open("./statistic/"+name+"/PerSchedule/mediaLevel.json","w+")		#media == numero medio di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo (e.g. turno notturno)
    mediaDiffFile=open("./statistic/"+name+"/PerSchedule/mediaDiffLevel.json","w+")	#mediaDiff == media delle differenze tra il numero di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo e @media
    maxFile=open("./statistic/"+name+"/PerSchedule/maxLevel.json","w+")			#max == numero massimo di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo
    maxDiffFile=open("./statistic/"+name+"/PerSchedule/maxDiffLevel.json","w+")		#maxDiff == differenza massima tra il numero di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo e @media
    minFile=open("./statistic/"+name+"/PerSchedule/minLevel.json","w+")			#min == numero minimo di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo
    minDiffFile=open("./statistic/"+name+"/PerSchedule/minDiffLevel.json","w+")		#minDiff == differenza minima tra il numero di volte in cui ciascun dottore è stato assegnato a un turno di un particolare tipo e @media

    dictFinMedia={}
    dictFinDiffMedia={}
    dictFinDiffMax={}
    dictFindMax={}
    dictFindMin={}
    dictFinDiffMin={}

    keys=["general_priority","night_priority","long_shift_priority"]
    AllUser = pd.read_sql("SELECT ms3_system_user_id,name,lastname FROM Doctor", con=conn)	#tutti gli utenti del sistema
    for i in range(numeroMesi):
        data_inizio = mese_successivo
        data_fine = mese_successivo.replace(day=(calendar.monthrange(mese_successivo.year,mese_successivo.month)[1]))
        tempo_inizio = time.time()
        # Chiamata alla funzione del backend che si occupa di generare ciascuna schedulazione
        esegui_richiesta_post(data_inizio, data_fine,alg)
        tempo_fine = time.time()
        dictApp={}
        for ms3_id in AllUser["ms3_system_user_id"]:
            queryLevel="SELECT night_priority,long_shift_priority,general_priority FROM doctor_uffa_priority WHERE doctor_ms3_system_user_id="+str(ms3_id)
            result5=pd.read_sql(queryLevel,conn)

            dictApp[ms3_id] = {
                "general_priority":result5["general_priority"].values[0],
                "night_priority":result5["night_priority"].values[0],
                "long_shift_priority":result5["long_shift_priority"].values[0]
            }
        tempo_trascorso = tempo_fine - tempo_inizio
        fileTmp.write(f"Tempo di esecuzione: {tempo_trascorso} secondi\n")
        fileTmp.flush()
        # Passa al mese successivo
        deltaDay=calendar.monthrange(mese_successivo.year,mese_successivo.month)[1]
        mese_successivo = mese_successivo.replace(day=1) + datetime.timedelta(days=deltaDay)

        dictFinMedia[i]=funcMedia(dictApp,keys)
        dictFindMax[i]=funcMax(dictApp,keys)
        dictFindMin[i]=funcMin(dictApp,keys)
        #calcolo differenza
        for item in dictApp.values():
            for k in keys:
                item[k] = abs(item[k]-dictFinMedia[i][k]);
        #calcolo media differenza
        dictFinDiffMedia[i]=funcMedia(dictApp,keys)
        dictFinDiffMax[i]=funcMax(dictApp,keys)
        dictFinDiffMin[i]=funcMin(dictApp,keys)
    json.dump(dictFinMedia, mediaFile, indent=2)
    json.dump(dictFinDiffMedia, mediaDiffFile, indent=2)
    json.dump(dictFinDiffMax, maxDiffFile, indent=2)
    json.dump(dictFindMax, maxFile, indent=2)
    json.dump(dictFindMin, minFile, indent=2)
    json.dump(dictFinDiffMin, minDiffFile, indent=2)
def func_delete():
    results = pd.read_sql("SELECT schedule_id as id FROM schedule", con=conn)	#tutti gli utenti del sistema
    url = "http://localhost:3000/api/schedule/id="
    for id in results["id"]:
        try:
            response = requests.delete(url+str(id))	#recupero della risposta del server
            if response.status_code == 200 or response.status_code == 202 :	#caso in cui la chiamata al servizio è andata a buon fine
                print("Richiesta POST effettuata con successo!")
                print("Risposta dal server:", response.text)
            else:	#caso in cui la chiamata al servizio non è andata a buon fine
                print(f"Errore nella richiesta POST. Codice di stato: {response.status_code}")
                print("Dettagli dell'errore:", response.text)
        except requests.exceptions.RequestException as e:
            print(f"Errore nella connessione al server: {e}")

if __name__ == "__main__":
    generaSchedulazioni("nuovoScheduler2",2)		#funzione che chiama il server dell'applicazione per generare le schedulazioni dei turni
    computazionePerSchedule("nuovoScheduler2") #funzione che calcola le statistiche di performance dell'algoritmo di scheduler per ciascuna schedulazione
    computazioneTotale("nuovoScheduler2")	#funzione che calcola le statistiche di performance dell'algoritmo di scheduler per tutte le schedulazioni nel complesso

    #generaSchedulazioni("vecchioScheduler",1)		#funzione che chiama il server dell'applicazione per generare le schedulazioni dei turni
    #computazionePerSchedule("vecchioScheduler") #funzione che calcola le statistiche di performance dell'algoritmo di scheduler per ciascuna schedulazione
    #computazioneTotale("vecchioScheduler")
