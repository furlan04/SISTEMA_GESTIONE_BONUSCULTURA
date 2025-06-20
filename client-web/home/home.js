document.addEventListener("DOMContentLoaded", () => {
  const nav = document.getElementById("nav-links");
  const cf = sessionStorage.getItem("codiceFiscale");

  nav.innerHTML = "";

  if (cf) {
    nav.innerHTML = `
      <li><a href="../buoni">Buoni</a></li>
      <li><a href="#" id="logout">Logout</a></li>
    `;
    document.getElementById("logout").addEventListener("click", (e) => {
      e.preventDefault();
      sessionStorage.removeItem("codiceFiscale");
      window.location.href = "../home";
    });
  } else {
    nav.innerHTML = `
      <li><a href="../login">Login</a></li>
    `;
  }

  fetch("http://localhost:8080/analitica")
    .then((res) => res.json())
    .then((data) => {
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
      const tbody = document.querySelector("#analitica-table tbody");
      tbody.innerHTML = `<tr><td colspan="2">Errore nel caricamento delle statistiche.</td></tr>`;
    });
});
