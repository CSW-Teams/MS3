# Analisi Opzioni AI "Zero-Cost" per MS3

Questo documento mette a confronto le due migliori opzioni gratuite attualmente disponibili per l'integrazione dell'AI nel backend Java.

## 1. Google Gemini API (Tramite Google AI Studio)

* **Modello Consigliato:**
* `Gemma 3 12/27B` (economico)
* `Gemini 2/2.5/3 Flash` (più intelligente).


* **Piano Gratuito (Free Tier):**
* **Costo:** Gratis.
> *Nota: Accettando il free tier, i dati potrebbero essere usati per il training.*


* **Limiti per Gemma 3 12/27B (Rate Limits):** Circa **30 Richieste al Minuto (RPM)** e **14.4k Richieste al Giorno**.
* **Token (Context Window):** Fino a **15k token** di contesto.
* **Limiti per Gemini 2/2.5/3 Flash (Rate Limits):** Circa **10 Richieste al Minuto (RPM)** e **20 Richieste al Giorno**.
* **Token (Context Window):** Fino a **250k token** di contesto.

* **Semplicità:** Molto alta. L'API restituisce JSON ben formattati e facili da parsare in Java.
* **Dove guardare:** [Google AI Studio](https://aistudio.google.com/), nella sezione Get API Key per generare la propria chiave e nella sezione dashboard/utilizzo e fatturazione per vedere il confronto tra i modelli.
* **Docs:** [Docs Google API](https://ai.google.dev/gemini-api/docs?hl=it)

---

## 2. Groq (Llama 3 & Mistral)

Groq non crea modelli, ma offre un'infrastruttura hardware (LPU) velocissima per far girare modelli open-source.

* **Modello Consigliato:**
* `llama-3.1-8b-instant` (leggero).
* `llama-3.3-70b-versatile` (molto potente).


* **Piano Gratuito:**
* **Costo:** Attualmente in beta gratuita.
* **Limiti:** Variano, ma generalmente permettono ~30 Richieste al Minuto e migliaia di token al giorno. Per i modelli indicati sono 30 Richieste al Minuto e, rispettivamente, 14.4K richieste al giorno e 1k richieste al giorno. I TPM (token per minute) sono rispettivamente 6K e 12k.


* **Semplicità:** Compatibile al 100% con l'API di **OpenAI**.
* **Dove guardare:** [Groq Console](https://console.groq.com/) nella sezione API Keys per generare la propria chiave e nella sezione docs/rate limits per vedere il confronto tra i modelli.
* **Docs:** [Docs Groq API](https://console.groq.com/) nella sezione Docs.

---
