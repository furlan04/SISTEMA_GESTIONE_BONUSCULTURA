# Progetto Sistemi Distribuiti 2024-2025 - MuzNet - API REST


**Attenzione**: l'unica rappresentazione ammessa è in formato JSON. Pertanto vengono assunti gli header `Content-Type: application/json` e `Accept: application/json`.

---

## `/utente`

### GET `/utente/{cf}`

**Descrizione**: Restituisce le informazioni dell’utente con codice fiscale `{cf}`.

**Parametri**:

- `{cf}`: Codice fiscale dell’utente.

**Header**:

- Nessuno oltre a quelli per JSON.

**Body richiesta**:

- Nessuno.

**Risposta**:

- Oggetto JSON con i dati dell’utente.

**Codici di stato restituiti**:

- 200 OK: successo.
- 404 Not Found: utente non trovato.
- 500 Server Error: errore lato server

---

### POST `/utente`

**Descrizione**: Crea un nuovo utente.

**Parametri**:

- Nessuno.

**Header**:

- Nessuno.

**Body richiesta**:

- Oggetto JSON con i dati dell’utente.

**Risposta**:

- Oggetto JSON dell’utente creato.

**Codici di stato restituiti**:

- 201 Created: successo.
- 400 Bad Request: errore nei dati inviati.
- 500 Server Error: errore lato server 

---

### GET `/utente/{cf}/saldo`

**Descrizione**: Restituisce le informazioni sul saldo rimanente dell’utente con codice fiscale `{cf}`.

**Parametri**:

- `{cf}`: Codice fiscale dell’utente.

**Header**:

- Nessuno.

**Body richiesta**:

- Nessuno.

**Risposta**:

- Oggetto JSON con le informazioni sul saldo.

**Codici di stato restituiti**:

- 200 OK: successo.
- 404 Not Found: utente non trovato.
- 500 Server Error: errore lato server

---

## `/analitica`

### GET `/analitica`

**Descrizione**: Restituisce le statistiche globali del sistema.

**Parametri**:

- Nessuno.

**Header**:

- Nessuno.

**Body richiesta**:

- Nessuno.

**Risposta**:

- Oggetto JSON con i campi:
  - `utentiRegistrati`
  - `buoniTotali`
  - `buoniConsumati`
  - `buoniNonConsumati`
  - `contributiSpesi`
  - `contributiAssegnati`
  - `contributiDisponibili`

**Codici di stato restituiti**:

- 200 OK: successo.
- 500 Server Error: errore lato server

---

## `/buoni`

### GET `/buoni/{cf}`

**Descrizione**: Restituisce la lista dei buoni associati all’utente con codice fiscale `{cf}`.

**Parametri**:

- `{cf}`: Codice fiscale dell’utente.

**Header**:

- Nessuno.

**Body richiesta**:

- Nessuno.

**Risposta**:

- Array JSON di oggetti buono.

**Codici di stato restituiti**:

- 200 OK: successo.
- 404 Not Found: utente o buoni non trovati.
- 500 Server Error: errore lato server

---

## `/buono`

### GET `/buono/{id}`

**Descrizione**: Restituisce le informazioni di un buono dato il suo id.

**Parametri**:

- `{id}`: ID del buono.

**Header**:

- Nessuno.

**Body richiesta**:

- Nessuno.

**Risposta**:

- Oggetto JSON con i dati del buono.

**Codici di stato restituiti**:

- 200 OK: successo.
- 404 Not Found: buono non trovato.
- 500 Server Error: errore lato server
---

### POST `/buono/{cf}`

**Descrizione**: Crea un nuovo buono per l’utente con codice fiscale `{cf}`.

**Parametri**:

- `{cf}`: Codice fiscale dell’utente.

**Header**:

- Nessuno.

**Body richiesta**:

- Oggetto JSON con i dati del buono.

**Risposta**:

- Oggetto JSON del buono creato.

**Codici di stato restituiti**:

- 201 Created: successo.
- 400 Bad Request: errore nei dati inviati o saldo insufficiente.
- 500 Server Error: errore lato server

---

### DELETE `/buono/{cf}/{id}`

**Descrizione**: Elimina il buono con id `{id}` associato all’utente `{cf}`.

**Parametri**:

- `{cf}`: Codice fiscale dell’utente.
- `{id}`: ID del buono.

**Header**:

- Nessuno.

**Body richiesta**:

- Nessuno.

**Risposta**:

- Oggetto JSON del buono eliminato.

**Codici di stato restituiti**:

- 200 OK: successo.
- 400 Bad Request: utente o buono non valido.
- 404 Not Found: buono o utente non trovato.
- 500 Server Error: errore lato server

---

### PUT `/buono/{id}/consuma`

**Descrizione**: Segna come consumato il buono con id `{id}`.

**Parametri**:

- `{id}`: ID del buono.

**Header**:

- Nessuno.

**Body richiesta**:

- Nessuno.

**Risposta**:

- Oggetto JSON del buono aggiornato.

**Codici di stato restituiti**:

- 200 OK: successo.
- 404 Not Found: buono non trovato.
- 500 Server Error: errore lato server

---

### PUT `/buono/{cf}/{id}`

**Descrizione**: Modifica il buono con id `{id}` dell’utente `{cf}`.

**Parametri**:

- `{cf}`: Codice fiscale dell’utente.
- `{id}`: ID del buono.

**Header**:

- Nessuno.

**Body richiesta**:

- Oggetto JSON con i nuovi dati del buono.

**Risposta**:

- Oggetto JSON del buono aggiornato.

**Codici di stato restituiti**:

- 200 OK: successo.
- 400 Bad Request: errore nei dati inviati o saldo insufficiente.
- 404 Not Found: utente o buono non
- 500 Server Error: errore lato server

### L'errore: `500 Server Error: errore lato server` può essere spesso provocato da una mancata connessione al database, controlla che il database stia funzionando correttamente!

## Esempi di oggetti JSON

Di seguito sono riportati alcuni esempi di oggetti JSON utilizzati nelle richieste e risposte.

### Utente

```json
{
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@email.com",
  "codiceFiscale": "RSSMRA80A01H501U"
}
```

### Buono

```json
{
  "id": "12",
  "valore": 50,
  "tipologia": "buono cultura",
  "dataCreazione": "2025-06-20",
  "dataConsumo": null
}
```

### Buoni
```json
[
  {
    "dataConsumo": "2025-06-16Z",
    "dataCreazione": "2025-06-13Z",
    "id": "3",
    "tipologia": "cinema",
    "valore": 20
  },
  {
    "dataConsumo": "2025-06-28Z",
    "dataCreazione": "2025-06-12Z",
    "id": "4",
    "tipologia": "tre",
    "valore": 23
  }
]
```


### Statistiche analitiche

```json
{
  "utentiRegistrati": 1000,
  "buoniTotali": 5000,
  "buoniConsumati": 3000,
  "buoniNonConsumati": 2000,
  "contributiSpesi": 15000,
  "contributiAssegnati": 20000,
  "contributiDisponibili": 5000
}
```

### Saldo

```json
{
  "saldo": 437,
  "saldo_consumato": 40,
  "saldo_non_consumato": 23
}
```