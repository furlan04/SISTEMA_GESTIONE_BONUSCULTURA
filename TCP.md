# Progetto Sistemi Distribuiti 2024-2025 - MuzNet - TCP e Connessione al Database

Il database del progetto CartaCulturaGiovani è un database key-value custom, progettato e implementato tramite socket TCP. Il database accetta connessioni TCP sulla porta 3030 e gestisce i dati in memoria tramite una mappa Java (`Map<String, String>`). I dati vengono inizializzati da un file di configurazione (`initData.ini`) e sono organizzati secondo chiavi.

## 1. Panoramica protocollo

- **Tipo:** testuale  
- **Porta Utilizzata:** 3030  

## 2. Struttura dei Messaggi
`comando chiave valore`
### 2.2. Protocollo testuale

struttura dei messaggi testuali scambiati:

- **Encoding:** UTF-8
- **Fine linea:** LF
- **Delimitatori Messaggio:** newline

**Esempi:**
```
get utente:RSSLCA80A01H501X:nome
set utente:RSSLCA80A01H501X:nome Luca
```

#### Comandi

| Comando | Parametri         | Descrizione                                | Esempio                |
|---------|-------------------|--------------------------------------------|------------------------|
| GET     | chiave    | Restituice il valore associato alla chiave               | `GET chiave`   |
| SET     | chiave, valore           | Imposta il valore alla chiave | `SET chiave valore`      |
| DELETE    | chiave                  | Elimina il record associata alla chiave  | `DELETE chiave`                 |
| EXISTS    | chiave                   | Restituisce "true" se esiste un record associato alla chiave, "false" altrimenti  | `EXISTS chiave`                 |
| GETALLKEYS    |                   | Restituisce una stringa con tutte le chiavi del database divise da un ";" | `GETALLKEYS`                 |


## 3. Gestione degli Errori

Struttura delle risposte di errore: `ERROR: <descrizione>`
Esempi di messaggi di errore:
  - `ERROR: command not found!`
  - `ERROR: Command cannot be null or empty`
  - `ERROR: Key not found: <key>`
  - `ERROR: Error reading init file: <error>`
  - `ERROR: Empty command`


## 4. Scambio di Esempio

```
Client: GET key
Server: value
Client: SET key value
Server: true
Client: SET key new_value
Server: old_value
```