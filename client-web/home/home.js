document.addEventListener("DOMContentLoaded", () => {
  const nav = document.getElementById("nav-links");
  const cf = sessionStorage.getItem("codiceFiscale");
  const nome = sessionStorage.getItem("nome");

  nav.innerHTML = "";

  if (cf) {
    nav.innerHTML = `
      <li><a href="../buoni">Buoni</a></li>
      <li><a href="#" id="logout">Logout</a></li>
    `;
    document.getElementById("logout").addEventListener("click", (e) => {
      e.preventDefault();
      sessionStorage.removeItem("codiceFiscale");
      sessionStorage.removeItem("nome");
      window.location.href = "../home";
    });

    mostraInfoUtente(cf); // <--- aggiungi questa chiamata
  } else {
    nav.innerHTML = `
      <li><a href="../login">Login</a></li>
    `;
  }

  fetch("http://localhost:8080/analitica")
    .then((res) => res.json())
    .then((data) => {
      // Se uno dei campi principali è undefined/null, mostra solo il messaggio di errore
      if (
        !data ||
        data.utenti_registrati === undefined ||
        data.buoni_totali === undefined ||
        data.buoni_consumati === undefined ||
        data.buoni_non_consumati === undefined ||
        data.contributi_spesi === undefined ||
        data.contributi_assegnati === undefined ||
        data.contributi_disponibili === undefined
      ) {
        const table = document.getElementById("analitica-table");
        table.outerHTML = `<div class="analitica-error">Non è stato possibile recuperare le statistiche di sistema.</div>`;
        return;
      }
      const tbody = document.querySelector("#analitica-table tbody");
      tbody.innerHTML = `
        <tr><th>Utenti registrati</th><td>${data.utenti_registrati}</td></tr>
        <tr><th>Buoni totali</th><td>${data.buoni_totali}</td></tr>
        <tr><th>Buoni consumati</th><td>${data.buoni_consumati}</td></tr>
        <tr><th>Buoni non consumati</th><td>${
          data.buoni_non_consumati
        }</td></tr>
        <tr><th>Contributi spesi</th><td>€${Number(
          data.contributi_spesi
        ).toFixed(2)}</td></tr>
        <tr><th>Contributi assegnati</th><td>€${Number(
          data.contributi_assegnati
        ).toFixed(2)}</td></tr>
        <tr><th>Contributi disponibili</th><td>€${Number(
          data.contributi_disponibili
        ).toFixed(2)}</td></tr>
      `;
    })
    .catch(() => {
      const table = document.getElementById("analitica-table");
      table.outerHTML = `<div class="analitica-error">Non è stato possibile recuperare le statistiche di sistema.</div>`;
    });
});

// Funzione per mostrare le info utente
function mostraInfoUtente(cf) {
  fetch(`http://localhost:8080/utente/${encodeURIComponent(cf)}`)
    .then((res) => {
      if (!res.ok) throw new Error("Utente non trovato");
      return res.json();
    })
    .then((utente) => {
      // Se la risposta è una stringa JSON, fai il parse
      if (typeof utente === "string") utente = JSON.parse(utente);
      const infoDiv = document.getElementById("utente-info");
      infoDiv.innerHTML = `
        <div class="utente-info-box">
          <strong>Nome:</strong> ${utente.nome}<br>
          <strong>Cognome:</strong> ${utente.cognome}<br>
          <strong>Email:</strong> ${utente.email}
        </div>
      `;
      // Salva il nome anche nel sessionStorage per la navbar
      sessionStorage.setItem("nome", utente.nome);
    })
    .catch(() => {
      const infoDiv = document.getElementById("utente-info");
      infoDiv.innerHTML = `<div class="utente-info-box">Errore nel caricamento dati utente.</div>`;
    });
}
