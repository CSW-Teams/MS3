import pandas as pd
import pg8000
import requests
import datetime
import calendar
import json
import sys

name="sprintfloyd"
psw="sprintfloyd"

def funcMedia(dizionario):
    # Conta il numero totale di elementi nel dizionario
    total_g = 0
    total_p = 0
    total_n = 0
    total_d = 0
    total_l = 0
    num_elements = len(dizionario)
    # Somma i valori di "g", "p", e "n" su tutti gli elementi
    for item in dizionario.values():
        total_g += item["giornaliero"]
        total_p += item["pomeridiano"]
        total_n += item["notturno"]
        total_d += item["domeniche"]
        total_l += item["lunga"]
    # Calcola le medie
    average_g = total_g / num_elements
    average_p = total_p / num_elements
    average_n = total_n / num_elements
    average_d = total_d / num_elements
    average_l = total_l / num_elements

    return {"giornaliero":float(average_g),"pomeridiano":float(average_p),"notturno":float(average_n),"domeniche":float(average_d),"lunga":float(average_l)}

def funcMax(dizionario):
    mg=0;
    mp=0;
    mn=0;
    md=0
    ml=0
    for item in dizionario.values():
        if(mg<item["giornaliero"]):
            mg=item["giornaliero"]
        if(mp<item["pomeridiano"]):
            mp=item["pomeridiano"]
        if(mn<item["notturno"]):
            mn=item["notturno"]
        if(md<item["domeniche"]):
            md=item["domeniche"]
        if(ml<item["lunga"]):
            ml=item["lunga"]
    return {"giornaliero":float(mg),"pomeridiano":float(mp),"notturno":float(mn),"domeniche":float(md),"lunga":float(ml)}

def funcMin(dizionario):
    idCasuale= list(dizionario.keys())[0]
    mg=dizionario.get(idCasuale)["giornaliero"];
    mp=dizionario.get(idCasuale)["pomeridiano"];
    mn=dizionario.get(idCasuale)["notturno"];
    md=dizionario.get(idCasuale)["domeniche"];
    ml=dizionario.get(idCasuale)["lunga"];
    for item in dizionario.values():
        if(mg>item["giornaliero"]):
            mg=item["giornaliero"]
        if(mp>item["pomeridiano"]):
            mp=item["pomeridiano"]
        if(mn>item["notturno"]):
            mn=item["notturno"]
        if(md>item["domeniche"]):
            md=item["domeniche"]
        if(ml>item["lunga"]):
            ml=item["lunga"]
    return {"giornaliero":float(mg),"pomeridiano":float(mp),"notturno":float(mn),"domeniche":float(md),"lunga":float(ml)}
def computazioneTotale(name):
    conn = pg8000.connect(
        user=name,
        password=psw,
        host="127.0.0.1",
        port=5432,
        database="ms3"
    )
    AllUser = pd.read_sql("SELECT ms3_system_user_id,name,lastname FROM Doctor", con=conn)
    dictApp={}
    for ms3_id in AllUser["ms3_system_user_id"]:
        queryShiftTime="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE ds.doctor_ms3_system_user_id="+str(ms3_id)+" and shift.time_slot="
        count0 = pd.read_sql(queryShiftTime+"0", con=conn) #quanti giornalieri
        count1 = pd.read_sql(queryShiftTime+"1", con=conn) #quanti pomeridiani
        count2 = pd.read_sql(queryShiftTime+"2", con=conn) #quanti notturnix

        querySunday="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE ds.doctor_ms3_system_user_id="+str(ms3_id)+" and cs.date%7=3"

        count3 = pd.read_sql(querySunday, con=conn)

        queryDouble="SELECT count(*) as c from (SELECT count(*) as ctemp FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE ds.doctor_ms3_system_user_id="+str(ms3_id)+" and ds.concrete_shift_doctor_status=0 group by date having count(*)>=2) as temporary";

        count4 = pd.read_sql(queryDouble, con=conn)

        dictApp[ms3_id]={
                            "giornaliero":count0["c"].values[0],
                            "pomeridiano":count1["c"].values[0],
                            "notturno":count2["c"].values[0],
                            "domeniche":count3["c"].values[0],
                            "lunga":count4["c"].values[0]
                        }
    dictFinMedia=funcMedia(dictApp)
    dictFindMax=funcMax(dictApp)
    dictFindMin=funcMin(dictApp)

    #caloclo differenza
    for item in dictApp.values():
        item["giornaliero"] = abs(item["giornaliero"]-dictFinMedia  ["giornaliero"]);
        item["pomeridiano"] = abs(item["pomeridiano"]-dictFinMedia["pomeridiano"]);
        item["notturno"] = abs(item["notturno"]-dictFinMedia["notturno"]);
        item["domeniche"] = abs(item["domeniche"]-dictFinMedia["domeniche"]);
        item["lunga"] = abs(item["lunga"]-dictFinMedia["lunga"]);
    #calcolo media differenza
    dictFinDiffMedia=funcMedia(dictApp)
    dictFinDiffMax=funcMax(dictApp)
    dictFinDiffMin=funcMin(dictApp)
    oggettoDaSalvare={
            "media_asseganzioni":dictFinMedia,
            "max_assegnazioni":dictFindMax,
            "min_assegnazioni":dictFindMin,
            "discostamento_medio_media_asseganzioni":dictFinDiffMedia,
            "discostamento_max_media_asseganzioni":dictFinDiffMax,
            "discostamento_min_media_asseganzioni":dictFinDiffMin,
        }
    filesalv=open("./statistic/"+name+"/result_totale.json","w+")
    json.dump(oggettoDaSalvare, filesalv, indent=2)
def computazionePerSchedule(name):
    # Configura la connessione al database
    conn = pg8000.connect(
        user=name,
        password=psw,
        host="127.0.0.1",
        port=5432,
        database="ms3"
    )
    mediaFile=open("./statistic/"+name+"/media.json","w+")
    mediaDiffFile=open("./statistic/"+name+"/mediaDiff.json","w+")
    maxFile=open("./statistic/"+name+"/max.json","w+")
    maxDiffFile=open("./statistic/"+name+"/maxDiff.json","w+")
    minFile=open("./statistic/"+name+"/min.json","w+")
    minDiffFile=open("./statistic/"+name+"/minDiff.json","w+")

    AllUser = pd.read_sql("SELECT ms3_system_user_id,name,lastname FROM Doctor", con=conn)
    AllSchedule = pd.read_sql("SELECT Schedule_id FROM Schedule", con=conn)
    dictFinMedia={}
    dictFinDiffMedia={}
    dictFinDiffMax={}
    dictFindMax={}
    dictFindMin={}
    dictFinDiffMin={}
    for schedule_id in AllSchedule["schedule_id"]:
        dictApp={}
        for ms3_id in AllUser["ms3_system_user_id"]:
            queryShiftTime="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE scs.schedule_schedule_id="+str(schedule_id)+" and ds.doctor_ms3_system_user_id="+str(ms3_id)+" and shift.time_slot="
            count0 = pd.read_sql(queryShiftTime+"0", con=conn) #quanti giornalieri
            count1 = pd.read_sql(queryShiftTime+"1", con=conn) #quanti pomeridiani
            count2 = pd.read_sql(queryShiftTime+"2", con=conn) #quanti notturnix

            querySunday="SELECT count(*) as c FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE scs.schedule_schedule_id="+str(schedule_id)+" and ds.doctor_ms3_system_user_id="+str(ms3_id)+" and cs.date%7=3"

            count3 = pd.read_sql(querySunday, con=conn)

            queryDouble="SELECT count(*) as c from (SELECT count(*) as ctemp FROM doctor_assignment as ds join concrete_shift as cs on ds.concrete_shift_id=cs.concrete_shift_id join shift on cs.shift_shift_id=shift.shift_id join schedule_concrete_shifts as scs on scs.concrete_shifts_concrete_shift_id=cs.concrete_shift_id WHERE scs.schedule_schedule_id="+str(schedule_id)+" and ds.doctor_ms3_system_user_id="+str(ms3_id)+" and ds.concrete_shift_doctor_status=0 group by date having count(*)>=2) as  temporary";

            count4 = pd.read_sql(queryDouble, con=conn)

            dictApp[ms3_id]={
                                "giornaliero":count0["c"].values[0],
                                "pomeridiano":count1["c"].values[0],
                                "notturno":count2["c"].values[0],
                                "domeniche":count3["c"].values[0],
                                "lunga":count4["c"].values[0]
                            }
        dictFinMedia[schedule_id]=funcMedia(dictApp)
        dictFindMax[schedule_id]=funcMax(dictApp)
        dictFindMin[schedule_id]=funcMin(dictApp)

        #caloclo differenza
        for item in dictApp.values():
            item["giornaliero"] = abs(item["giornaliero"]-dictFinMedia[schedule_id]["giornaliero"]);
            item["pomeridiano"] = abs(item["pomeridiano"]-dictFinMedia[schedule_id]["pomeridiano"]);
            item["notturno"] = abs(item["notturno"]-dictFinMedia[schedule_id]["notturno"]);
            item["domeniche"] = abs(item["domeniche"]-dictFinMedia[schedule_id]["domeniche"]);
            item["lunga"] = abs(item["lunga"]-dictFinMedia[schedule_id]["lunga"]);
        #calcolo media differenza
        dictFinDiffMedia[schedule_id]=funcMedia(dictApp)
        dictFinDiffMax[schedule_id]=funcMax(dictApp)
        dictFinDiffMin[schedule_id]=funcMin(dictApp)

    json.dump(dictFinMedia, mediaFile, indent=2)
    json.dump(dictFinDiffMedia, mediaDiffFile, indent=2)
    json.dump(dictFinDiffMax, maxDiffFile, indent=2)
    json.dump(dictFindMax, maxFile, indent=2)
    json.dump(dictFindMin, minFile, indent=2)
    json.dump(dictFinDiffMin, minDiffFile, indent=2)
def esegui_richiesta_post(data_inizio, data_fine):
    url = "http://localhost:3000/api/schedule/generation"
    headers= { 'Content-Type': 'application/json' }
    print(data_inizio,data_fine)
    parametri = {
        "initialDay": data_inizio.day,
        "initialMonth": data_inizio.month,
        "initialYear": data_inizio.year,
        "finalDay": data_fine.day,
        "finalMonth": data_fine.month,
        "finalYear": data_fine.year
    }
    try:
        response = requests.post(url,headers=headers, data=json.dumps(parametri))

        if response.status_code == 200 or response.status_code == 202 :
            print("Richiesta POST effettuata con successo!")
            print("Risposta dal server:", response.text)
        else:
            print(f"Errore nella richiesta POST. Codice di stato: {response.status_code}")
            print("Dettagli dell'errore:", response.text)
    except requests.exceptions.RequestException as e:
        print(f"Errore nella connessione al server: {e}")

def generaSchedulazioni():
    data_attuale = datetime.datetime.now()
    deltaDay=calendar.monthrange(data_attuale.year,data_attuale.month)[1]
    mese_successivo = data_attuale.replace(day=1) + datetime.timedelta(days=deltaDay)
    print(data_attuale)
    print(mese_successivo)
    for _ in range(24):
        data_inizio = mese_successivo
        data_fine = mese_successivo.replace(day=(calendar.monthrange(mese_successivo.year,mese_successivo.month)[1]))
        # Chiamata alla funzione
        esegui_richiesta_post(data_inizio, data_fine)
        # Passa al mese successivo
        deltaDay=calendar.monthrange(mese_successivo.year,mese_successivo.month)[1]
        mese_successivo = mese_successivo.replace(day=1) + datetime.timedelta(days=deltaDay)

if __name__ == "__main__":
    #generaSchedulazioni()
    computazionePerSchedule(sys.argv[1])
    computazioneTotale(sys.argv[1])
