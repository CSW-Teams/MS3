# Story 2
## Microtask 2.1


**AI API Inventory & Endpoint / SDK Discovery**

**Story 2 — Comunicazione con agenti AI e progettazione del protocollo**

**TOON/JSON**

**Ambito:** Direct Prompting con contesto (nessun MCP/RLM in questa versione)
**Dipendenze:** Story 1 completata
**Affinità in parallelo:** Nessuna
**Output atteso:** AI API assumptions list + endpoint/SDK discovery notes

**1. Obiettivo del microtask**

Il microtask 2.1 ha l’obiettivo di identificare e analizzare le **API degli agenti AI accessibili
tramite free plan pubblici** , al fine di:

```
● comprendere endpoint disponibili e contratti HTTP
● identificare limiti di payload e di utilizzo (rate limit, token quota)
● analizzare i meccanismi di autenticazione
● verificare i formati di input/output supportati
● produrre un artefatto informativo utilizzabile per l’implementazione
```
L’analisi è funzionale alla progettazione del **protocollo di comunicazione TOON/JSON** ,
che deve operare correttamente anche in presenza di vincoli tipici dei free plan.

**2. Criteri di selezione delle API**

Sono state considerate valide le API che soddisfano i seguenti criteri:

1. **Disponibilità tramite free plan / free tier pubblico**
2. **Documentazione ufficiale accessibile**
3. **Accesso tramite HTTP API**
4. **Utilizzabili per sviluppo e prototipazione**
5. **Limiti di utilizzo dichiarati o implicitamente applicati**

```
Nota: il termine gratuito è qui inteso come accesso senza pagamento iniziale ,
soggetto a limiti di utilizzo.
```
**3. AI API Assumptions List**

Le seguenti assunzioni guidano la progettazione e l’integrazione degli agenti AI:


1. **Le API AI operano in regime di free plan con limiti di utilizzo**
    Non si assume disponibilità gratuita illimitata.
2. **I limiti di quota e rate limit sono eventi normali di esecuzione**
    Il sistema deve prevedere la gestione di errori di tipo _rate limit exceeded_ (es. HTTP
    429).
3. **La comunicazione verso le API AI avviene tramite JSON** , in quanto formato
    nativamente supportato dagli endpoint HTTP dei provider.

```
Il formato .toon è utilizzato internamente al sistema come rappresentazione
token-oriented del contesto e dello scheduling, consentendo la minimizzazione dei
token e la generazione controllata dei payload JSON inviati agli agenti AI.
```
4. **Il protocollo TOON/JSON è indipendente dal provider AI**
    Il contesto e lo scheduling sono modellati in .toon, mentre le risposte degli agenti
    sono normalizzate in .json.
5. **L’autenticazione è basata su API key o Bearer token**
    Non sono previsti flussi OAuth user-centrici.
**4. Endpoint & SDK Discovery Notes**

**4.1 Google Gemini API**

**Provider:** Google
**Tipo:** API cloud per LLM
**Accesso:** Free plan con limiti
**Documentazione ufficiale:**
https://ai.google.dev/api?hl=it

**4.1.1 Endpoint principali**

**Generazione contenuto:**

POST https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent

**Streaming:**

POST ...:streamGenerateContent

**4.1.2 Autenticazione**

API key

x-goog-api-key: <API_KEY>

**4.1.3 Limiti del free plan (indicativi)**


```
● Rate limit (richieste/minuto)
● Token quota giornaliera/mensile
● Context window dipendente dal modello (fino a centinaia di migliaia o 1M token per
modelli recenti)
```
**4.1.4 Formati supportati**

```
Tipo Supporto
```
```
Input JSON
```
```
Output JSON
```
```
Streaming SSE
```
```
Multimodale SI
```
**4.2 Gemma (via Gemini API)**

**Tipo:** Modelli open-weight serviti tramite Gemini API
**Accesso:** Incluso nel free plan Gemini
**Documentazione:**
https://ai.google.dev/gemma/docs/core/gemma_on_gemini_api

**4.2.1 Modelli rilevanti**

```
● gemma-7b
● gemma-3-27b-it
```
**4.2.2 Endpoint e limiti**

```
● Endpoint: identico a Gemini (generateContent)
● Autenticazione: API key Gemini
● Limiti: stessi del free plan Gemini
● Formato: JSON
```
**4.3 Grok (xAI)**

**Provider:** xAI
**Tipo:** API cloud LLM
**Accesso:** Free plan con limiti
**Documentazione ufficiale:**
https://docs.x.ai/docs/api-reference


**4.3.1 Endpoint principale**

POST https://api.x.ai/v1/chat/completions

**4.3.2 Autenticazione**

Authorization: Bearer <XAI_API_KEY>

**4.3.3 Limiti free plan**

```
● Rate limit (RPM)
● Token per richiesta e per periodo
● Concurrency limitata
```
**4.3.4 Formati supportati**

⚠ **Grok/xAI** : endpoint/auth/formato ok, ma la parte “free plan con limiti” **va riformulata**
perché la doc ufficiale parla di **crediti da caricare**.

```
● La guida ufficiale “Getting Started” dice esplicitamente che per usare l’API devi
caricare crediti (“load it with credits”).
https://docs.x.ai/docs/tutorial?utm_source=chatgpt.com
● xAI in passato ha annunciato un programma beta con free credits mensili , ma era
legato a un periodo (“throughout the end of the year” nel 2024).
https://x.ai/news/api?utm_source=chatgpt.com
```
**4.4 Llama-70B via Groq**

**Provider:** Groq
**Tipo:** API cloud per inference su modelli open-source
**Accesso:** Free plan con limiti
**Documentazione ufficiale:**
https://console.groq.com/docs

**4.4.1 Modello**

```
Tipo Supporto
```
```
Input JSON (OpenAI-compatible)
```
```
Output JSON
```
```
Streaming SSE
```

```
● llama3-70b-8192
(Modello open-source Llama, classe 70B)
```
**4.4.2 Endpoint principale**

POST https://api.groq.com/openai/v1/chat/completions

**4.4.3 Autenticazione**

Authorization: Bearer <GROQ_API_KEY>

**4.4.4 Limiti del free plan**

```
● Rate limit (RPM)
● Token per minuto (TPM)
● Context window: ~8k token
● Nessuna garanzia di disponibilità illimitata nel tempo
```
**4.4.5 Formati supportati**

**5. Implicazioni progettuali per TOON/JSON**

L’uso di API con free plan limits implica che il protocollo TOON/JSON debba supportare:

```
● scheduling delle richieste
● gestione dei rate limit
● retry con backoff
● separazione tra:
○ contesto e istruzioni di sistema (.toon)
○ risposta strutturata dell’agente (.json)
```
La **dual-channel communication** non è quindi solo una scelta formale, ma una risposta
diretta ai vincoli operativi delle API analizzate.

**6. Output del microtask**

Il microtask 2.1 produce i seguenti artefatti informativi:

```
Tipo Supporto
```
```
Input JSON (OpenAI-compatible)
```
```
Output JSON
```
```
Streaming SSE
```

```
● AI API Assumptions List
● Endpoint / SDK discovery notes , comprensive di:
○ URL ufficiali
○ endpoint REST
○ meccanismi di autenticazione
○ limiti di utilizzo
○ formati supportati
```
Tali informazioni sono sufficienti per il passaggio all’implementazione e per la progettazione
degli adapter AI.

Le API AI accessibili tramite free plan (Gemini, Gemma, Grok e Llama-70B via Groq) sono
state identificate e analizzate in termini di endpoint, autenticazione, limiti e formati supportati.
I vincoli introdotti dai free plan sono stati considerati esplicitamente nella progettazione del
protocollo TOON/JSON, garantendo robustezza e portabilità dell’architettura.


## Microtask 2.2

## Define system knowledge base payload

This task’s goal is to define the necessary elements of the context to be passed to the agent.
**Assumptions** : doctors’ feedback are coherent with their declared conditions and state the
truth (i.e., a feedback saying “I received too many holidays in the last period” is assumed to
be true) thus not requiring any additional check.

Here follows the elements to be included.

**The Current Schedule (Schedule and ConcreteShift):**

```
● What it represents: The existing plan, including all assigned shifts, dates, and the
specific doctors currently assigned to each task.
● Why it is needed: It serves as the "baseline" or starting point for the AI to identify
which specific assignments are causing dissatisfaction and require modification.
```
**Schedule Feedbacks (ScheduleFeedback):**

```
● What it represents: Qualitative and quantitative data (comments and scores)
provided by doctors regarding specific concrete shifts.
● Why it is needed: This is the primary driver for the rescheduling. The AI needs to
understand which assignments are problematic and what the specific complaints are
to propose better alternatives.
```
**Doctors' Fairness Priorities (DoctorUffaPriority):**

```
● What it represents: Tracking of "Uffa points" across different categories: General,
Long Shift, and Night priorities.
● Why it is needed: Adjustments must not only satisfy feedback but also maintain
fairness. The AI uses these priorities to ensure that "favors" done for one doctor don't
unfairly increase the workload or undesirable shifts for another.
```
**System Constraints (Constraint subclasses):**

```
● What it represents: The fundamental rules of the system, such as
ConstraintMaxOrePeriodo (maximum hours), ConstraintTurniContigui
(rest periods between shifts), and ConstraintUbiquita (preventing a doctor from
being in two places at once).
● Why it is needed: To prevent the AI from creating an "illegal" schedule. Any shift
swap suggested to satisfy feedback must still pass the verifyConstraint logic to
ensure safety and labor law compliance.
```
**Doctor Conditions (Condition, PermanentCondition, TemporaryCondition):**


```
● What it represents: Specific medical or personal attributes of a doctor that limit their
assignability, such as pregnancy, specific disabilities, or temporary leave.
● Why it is needed: The AI must know these limitations to avoid moving a doctor into
a shift they are legally or physically unable to perform, which might have been the
root cause of the negative feedback in the first place.
```
**The Global Doctor Pool and Seniority (Doctor and Seniority):**

```
● What it represents: The full list of available staff and their professional level (e.g.,
Structured vs. Specialist).
● Why it is needed: To perform a swap or replacement, the AI needs to know who
else is available and whether they have the required Seniority to fulfill the specific
roles defined in the Shift requirements.
```
**Medical Service and Task Requirements (MedicalService and Task):**

```
● What it represents: The definitions of the hospital services and the specific tasks
(e.g., Ward, Emergency Room) that must be covered during a shift.
● Why it is needed: Different tasks require different skills or numbers of personnel.
The AI needs this to ensure that the adjusted schedule still meets the minimum
staffing levels for every hospital department.
```
**Note** : it may happen that a doctor leaves feedback commenting something like “I received
too many nights in the last period”, including the schedule and one or more previous ones.
By assumption, feedbacks are coherent and state the truth, so there is no need of providing
the agent with the older feedbacks or schedules. The agent will simply trust what it’s
provided.


## **Microtask 2.3: Specifica TOON Request & JSON**

**Response**

Bisogna definire un protocollo di interscambio dati tra il Backend Java di MS3 e l'Agente AI.

**1. TOON Request Schema (Input per l'Agente)**

Il backend deve serializzare lo stato attuale del sistema in un file .toon. Questo formato è
preferito al JSON per l'input perché l'indentazione e le tabelle di TOON aiutano l'LLM a
mantenere la gerarchia dei dati senza ripetere chiavi ridondanti.

**Sezione: Definizione del Contesto**

Utilizzeremo una struttura a blocchi per separare i dati globali da quelli specifici dei medici.

1. # Metadati della sessione
2. ctx:
3. period: "2026-01-01/2026-12-31"
4. mode: "rebalance_uffa" # Obiettivo: minimizzare code UFFA
5.
6. # Catalogo Turni (ConcreteShifts da coprire)
7. # Formato Tabellare per efficienza token
8. shifts[14]{id, slot, date, duration, req_str, req_jun}:
9. S_101, NIGHT, 2026-05-20, 720, 2, 1 # 2 medici "strutturati" e 1 medico "junior"
10. S_102, MORNING, 2026-05-21, 360, 1, 2 # 1 medico "strutturato" e 2 medici "junior"
11.
12. # Registro Medici (Derivato da doctors_seed.toon)
13. # Nota: La seniority è mappata come stringa per chiarezza semantica
14. doctors[100]:
15. - id: 1
16. role: STRUCTURED
17. priorities{gen, night, long}: 11, 13, 7
18. holidays_taken[1]: "CHRISTMAS_2025"
19. # Blocchi di indisponibilità (Story 1 - Preferences)
20. blocks[1]:
21. - start: 2026-08-03, end: 2026-08-09, slots: ["MORNING", "NIGHT"]
22.
23. # Vincoli di Business (Story 1 Logic)
24. active_constraints[3]{type, entity_type, entity_id, reason, params}:
25. HARD, DOCTOR, 5, REST_PERIOD, { "until": "2026-05-21T08:00:00Z" }
26. HARD, SHIFT, S_101, UNDERSTAFFED, { "missing": "1_JUNIOR" }
27. SOFT, DOCTOR, 7, PREFERENCE_AVOID, { "shift_id": "S_102" }


**Spiegazione del Contesto per active_constraints:**

Il backend genera questa tabella analizzando lo stato attuale del sistema, prima di inviare la
richiesta:

**1. Vincoli di Riposo Obbligatorio (Es. `ConstraintTurniContigui`)**
    ● Contesto: Il backend conosce la storia recente dei turni di ogni medico. Se un
       medico ha appena terminato un turno notturno, il sistema sa che è in un
       periodo di riposo forzato per X ore.
    ● Prima di generare il TOON, il backend cicla sui medici. Se un medico si trova
       in un periodo di riposo obbligatorio che si sovrappone al periodo dello
       schedule da generare, aggiunge una riga: HARD, DOCTOR, D_05,
       REST_PERIOD, { "until": "..." }. Questo dice all'AI: "Non considerare
       nemmeno questo medico per i turni che iniziano prima di questa data/ora".
**2. Vincoli di Indisponibilità (Es. Ferie, Malattia, Preferenze `AVOID`)**
    ● Contesto: Il database contiene le entità Holiday e Preference associate ai
       medici.
    ● Il serializer del backend legge queste entità. Se un medico è in ferie o ha
       espresso una preferenza AVOID per un determinato giorno/turno, viene
       aggiunta una riga. Una vacanza genera un vincolo HARD, mentre una
       preferenza genera un vincolo SOFT, segnalando all'AI che l'assegnazione è
       possibile ma "costosa".
**3. Stato Attuale dei Turni (Es. Sovraccarico/Sottodimensionato)**
    ● Contesto: Se la modalità è OPTIMIZE o REBALANCE, i turni hanno già delle
       assegnazioni.
    ● Il serializer del backend può confrontare il numero di medici attualmente
       assegnati a un turno con i requisiti di quel turno (req_str/req_jun).
          ○ Se un turno è sovradimensionato, genera un vincolo SOFT, SHIFT,
             S_101, OVERSTAFFED, .... Questo è un suggerimento per l'AI: "Puoi
             liberare una risorsa da qui".
          ○ Se un turno è sottodimensionato, genera un vincolo HARD, SHIFT,
             S_102, UNDERSTAFFED, .... Questo è un obiettivo primario per l'AI:
             "Devi trovare una soluzione qui".
**2. JSON Response Schema (Output dell'Agente)**

L'AI deve rispondere in formato JSON rigoroso. Questo permette al sistema Java di
utilizzare **Jackson** per mappare la risposta direttamente nei DTO di assegnazione e validare
il risultato contro il database.

**Sezione: Struttura della Risposta AI**

L'output deve includere non solo le assegnazioni, ma anche i metadati sul perché sono state
fatte certe scelte.

28. {
29. "status": "SUCCESS | PARTIAL_SUCCESS | FAILURE",
30. "metadata": {


31. "reasoning": "Copertura quasi totale. Impossibile assegnare turno S_105 per vincoli
    Hard.",
32. "optimality_score": 0.85,
33.
34. "metrics": {
35. "coverage_percent": 0.98,
36. "uffa_balance": {
37. "night_shift_std_dev": {
38. "initial": 40.1,
39. "final": 22.5
40. }
41. },
42. "soft_violations_count": 1
43. }
44. },
45.
46. "assignments": [
47. {
48. "shift_id": "S_101",
49. "doctor_id": 100,
50. "role_covered": "STRUCTURED", // Corrisponde al ruolo RAW ricevuto in input
51. "is_forced": false
52. },
53. {
54. "shift_id": "S_102",
55. "doctor_id": 45,
56. "role_covered": "SPECIALIST",
57. "is_forced": true,
58. "violation_note": "Superamento soft limite ore per garantire copertura."
59. }
60. ],
61.
62. "uncovered_shifts": [
63. {
64. "shift_id": "S_105",
65. "reason": "Nessun candidato disponibile che non violasse il vincolo di riposo
    obbligatorio."
66. }
67. ],
68.
69. "uffa_delta": [
70. { "doctor_id": 100, "queue": "gen", "points": 5 },
71. { "doctor_id": 45, "queue": "night", "points": 2 }
72. ]
73. }


**Divisione della struttura:**

```
● status : Un codice di stato riassuntivo che indica l'esito dell'operazione. Può essere
SUCCESS, PARTIAL_SUCCESS (se alcuni turni sono scoperti) o FAILURE.
○ Scopo: Permettere al backend di determinare se il processo ha avuto
successo e come procedere.
```
```
● metadata : Un contenitore per i dati qualitativi e quantitativi sulla soluzione proposta.
○ Scopo: Fornire spiegabilità e metriche di alto livello. Contiene reasoning (una
spiegazione testuale della strategia dell'AI), toon optimality_score (un
punteggio aggregato di qualità) e un oggetto metrics con indicatori chiave
come la percentuale di copertura (coverage_percent), l'equità (uffa_balance)
e il numero di forzature (soft_violations_count).
```
```
● assignments : Un array di oggetti, ciascuno rappresentante una singola
assegnazione di un medico a un turno.
○ Scopo: Elencare le azioni concrete che il backend deve applicare. I campi
is_forced e violation_note servono per gestire il "Constraint Relaxing",
permettendo all'AI di violare vincoli soft in modo controllato e trasparente. Il
campo role_covered specifica quale requisito di ruolo viene soddisfatto da
quell'assegnazione.
```
```
● uncovered_shifts : Una lista esplicita dei turni che l'AI non è riuscita a coprire.
○ Scopo: Fornire un feedback immediato e attivo sui buchi nello schedule,
includendo il motivo (reason) del fallimento.
```
```
● uffa_delta : Un elenco di modifiche incrementali proposte per gli UFFA points dei
medici.
○ Scopo: Permettere all'Agente AI di suggerire l'incremento del punteggio, che
il backend poi confermerà scrivendo nel database (Story 5). Il campo queue
garantisce che i punti vengano aggiunti al contatore corretto (generale,
notturno, ecc.).
```
**Output Artifact Finalizzato**

Questo documento dovrà servire come base per lo sviluppo del Backend (Story 5):

1. Il **ToonSerializer.java** (per la richiesta).
2. Il **JsonDeserializer.java** (per la risposta).

## Microtask 2.4 — Protocollo di comunicazione & istruzioni (piano)

**Obiettivo:** definire il flusso richiesta/risposta, timeouts, retry, istruzioni di esecuzione per agenti AI (input `.toon`, output esclusivamente `.json`), e produrre **flow chart** + **tassonomia errori**.

**Precondizioni:** Microtask 2.1–2.3 completati.  
**Affinità in parallelo:** Story 5 (backend orchestration).  
**Output atteso:** Protocol flow chart + error taxonomy.

### Piano operativo (step-by-step)

1. **Raccogliere i vincoli delle microtask precedenti (2.1–2.3).**  
   - Confermare requisiti TOON/JSON, contesto e vincoli di input/output già definiti.  
   - Obiettivo: evitare incoerenze tra schema TOON, schema JSON e protocollo.
2. **Definire il flusso end-to-end del protocollo (request/response).**  
   - Provider-agnostic.  
   - Preflight validation obbligatoria su `.toon` prima della chiamata all’agente.  
   - “Partial success” trattato come **errore**.  
   - Output: **flow chart** leggibile in Markdown (Mermaid/diagramma testuale).
3. **Formalizzare istruzioni di esecuzione per l’agente.**  
   - Prompt unico e unificato (stessa regola per tutti i provider).  
   - Input “schedule + feedback” in `.toon`; istruzioni operative passate solo via **context**.  
   - Output **solo JSON** (nessun testo, nessun markdown).  
   - Forzare `response_format` JSON (flag/provider-specific).
4. **Definire timeouts configurabili (draft iniziale).**  
   - Tutti i timeout devono essere impostabili in configurazione.  
   - Proposta iniziale (da rivedere):  
     - **Connect timeout:** 5s  
     - **Read timeout:** 60s  
     - **Total timeout:** 90s
5. **Definire retry policy (draft iniziale).**  
   - Tutti gli errori sono ritentabili.  
   - **Max retry:** 3 tentativi.  
   - **Backoff:** fisso **0ms** (cap massimo 30s, pronto per futuri backoff esponenziali).  
   - Inserire correlation/request ID dove supportato.
6. **Costruire la tassonomia degli errori.**  
   - Separare **Transport** vs **Application/Schema**.  
   - Includere “partial success” tra gli errori.  
   - Mantenere i vincoli/violazioni come categoria separata.
7. **Validare l’allineamento con Story 5 (orchestrazione).**  
   - Verificare che il flusso e la tassonomia supportino fallback e gestione errori backend.

---

### Protocol Flow Chart (bozza)

```mermaid
flowchart TD
  A[Genera .toon da schedule + feedback] --> B[Preflight validation .toon]
  B -- invalid --> E[Errore: input non valido]
  B -- valid --> C[Chiamata agent AI (payload JSON con .toon)]
  C --> D[Response JSON]
  D -- invalid JSON --> F[Errore: response schema/parse]
  D -- PARTIAL_SUCCESS --> G[Errore: partial success]
  D -- FAILURE --> H[Errore: failure dichiarato]
  D -- SUCCESS --> I[Persistenza e pipeline backend]
```

---

### Tassonomia Errori (proposta iniziale)

**A) Transport/Network**
1. Timeout di connessione (connect timeout).  
2. Timeout di lettura (read timeout).  
3. Timeout totale richiesta (total timeout).  
4. Errori di rete (DNS, TLS, reset).  
5. Rate limit / 429.

**B) Application/Schema**
1. JSON non valido o malformato.  
2. JSON valido ma non conforme allo schema (missing fields, types).  
3. Response `status = FAILURE`.  
4. Response `status = PARTIAL_SUCCESS` (trattato come errore).  
5. Violazioni vincoli (hard/soft) oltre soglia ammessa.

**C) Business/Domain**
1. Incoerenza con vincoli di business (es. assegnazioni impossibili).  
2. Output privo di assegnazioni o metriche essenziali.

---

### Fallback suggerito (draft)

Se tutte le chiamate AI falliscono: **ritornare lo schedule standard** e segnalare l’errore in metadati/log (nessun blocco totale del flusso).


## Microtask 2.5
