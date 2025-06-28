# Progetto Sistemi Distribuiti 2024-2025 - MuzNet

## CartaCulturaGiovani

CartaCulturaGiovani è un sistema digitale che permette ai giovani utenti di registrarsi, ricevere un contributo economico e gestire buoni digitali per l’acquisto di prodotti e servizi culturali (come cinema, concerti, libri, musei, eventi culturali, ecc.). L’applicazione consente la creazione, la modifica e la consultazione dei buoni, il monitoraggio del saldo e dei contributi spesi, e offre funzionalità di analisi aggregate per amministratori e utenti.

---

## Componenti del gruppo

* Simone Monzardo (910008) <s.monzardo1@campus.unimib.it>
* Davide Casati (909547) <d.casati18@campus.unimib.it>
* Gabriele Furlan (909389) <g.furlan7@campus.unimib.it>

## Compilazione ed esecuzione

Sia il server Web sia il database sono applicazioni Java da gestire con Maven. <br> All'interno delle rispettive cartelle si può trovare il file `pom.xml` in cui è presenta la configurazione di Maven per il progetto. Si presuppone l'utilizzo della macchina virtuale di laboratorio, per cui nel `pom.xml` è specificato l'uso di Java 21.

Il server Web e il database sono dei progetti Java che utilizano Maven per gestire le dipendenze, la compilazione e l'esecuzione.

### Client Web

Per avviare il client Web è necessario utilizzare l'estensione "Live Preview" su Visual Studio Code, come mostrato durante il laboratorio. Tale estensione espone un server locale con i file contenuti nella cartella `client-web`.

In alternativa si puó accedere al client web da un browser all'indirizzo: http://127.0.0.1:3000/client-web/home/


**Attenzione**: è necessario configurare CORS in Google Chrome come mostrato nel laboratorio.

### Server Web

Il server Web utilizza Jetty e Jersey. Si può avviare eseguendo `mvn jetty:run` all'interno della cartella `server-web`. Espone le API REST all'indirizzo `localhost` alla porta `8080`.

### Database

Il database è una semplice applicazione Java. Si possono utilizzare i seguenti comandi Maven:

* `mvn clean`: per ripulire la cartella dai file temporanei,
* `mvn compile`: per compilare l'applicazione,
* `mvn exec:java`: per avviare l'applicazione (presuppone che la classe principale sia `Main.java`). Si pone in ascolto all'indirizzo `localhost` alla porta `3030`.

#### Attenzione!!

Non eseguire `mvn exec:java` per avviare il databse provocherà delle risposte `500 - internal server error` da parte del web server.