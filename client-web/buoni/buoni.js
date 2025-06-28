document.addEventListener("DOMContentLoaded", () => {
  const codiceFiscale = sessionStorage.getItem("codiceFiscale");

  if (!codiceFiscale) {
    alert("Codice fiscale non trovato nel sessionStorage.");
    return;
  }

  mostraInfoSaldo(codiceFiscale);

  fetch(`http://localhost:8080/buoni/${codiceFiscale}`)
    .then((response) => {
      if (!response.ok) throw new Error("Errore nella richiesta API");
      return response.json();
    })
    .then((data) => {
      mostraBuoni(data);
    })
    .catch((error) => {
      console.error("Errore:", error);
      document.getElementById("buoni-lista").innerText =
        "Non è ancora stato creato nessun buono.";
    });
});

function mostraInfoSaldo(codiceFiscale) {
  fetch(
    `http://localhost:8080/utente/${encodeURIComponent(codiceFiscale)}/saldo`
  )
    .then((response) => {
      if (!response.ok) throw new Error("Errore nel recupero del saldo");
      return response.json();
    })
    .then((data) => {
      document.getElementById(
        "saldo-rimanente"
      ).innerHTML = `<strong>Saldo rimanente:</strong> €${Number(
        data.saldo
      ).toFixed(2)}`;
      document.getElementById(
        "contributo-consumato"
      ).innerHTML = `<strong>Contributo usato per buoni consumati:</strong> €${Number(
        data.saldo_consumato
      ).toFixed(2)}`;
      document.getElementById(
        "contributo-non-consumato"
      ).innerHTML = `<strong>Contributo usato per buoni non consumati:</strong> €${Number(
        data.saldo_non_consumato
      ).toFixed(2)}`;
    })
    .catch((error) => {
      document.getElementById("saldo-rimanente").innerHTML =
        "Errore nel recupero del saldo rimanente.";
      document.getElementById("contributo-consumato").innerHTML =
        "Errore nel recupero del contributo consumato.";
      document.getElementById("contributo-non-consumato").innerHTML =
        "Errore nel recupero del contributo non consumato.";
    });
}

function mostraBuoni(buoni) {
  const lista = document.getElementById("buoni-lista");

  if (!buoni.length) {
    lista.innerHTML = "<p>Nessun buono disponibile.</p>";
    return;
  }

  buoni.sort((a, b) => new Date(b.dataCreazione) - new Date(a.dataCreazione));

  buoni.forEach((buono) => {
    const div = document.createElement("div");
    div.className = "buono";
    div.innerHTML = `
      <h3>${capitalize(buono.tipologia)}</h3>
      <p><strong>Data Creazione:</strong> ${formatDate(buono.dataCreazione)}</p>
      <p><strong>Data Consumo:</strong> ${
        buono.dataConsumo
          ? formatDate(buono.dataConsumo)
          : "Buono non consumato"
      }</p>
      <p><strong>Valore:</strong> €${buono.valore.toFixed(2)}</p>
      <a href="../buono/?id=${buono.id}" class="button">Visualizza Dettagli</a>
    `;
    lista.appendChild(div);
  });
}

function formatDate(isoDate) {
  const date = new Date(isoDate);
  return date.toLocaleDateString("it-IT");
}

function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}
