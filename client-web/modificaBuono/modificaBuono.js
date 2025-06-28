document.addEventListener("DOMContentLoaded", async function () {
  const params = new URLSearchParams(location.search);
  const id = params.get("id");
  const form = document.getElementById("modificaBuonoForm");
  const msgDiv = document.getElementById("modifica-msg");
  const codiceFiscale = sessionStorage.getItem("codiceFiscale");

  if (!id) {
    msgDiv.textContent = "ID buono mancante nell'URL.";
    msgDiv.style.color = "#b00020";
    form.style.display = "none";
    return;
  }

  try {
    const response = await fetch(
      `http://localhost:8080/buono/${encodeURIComponent(id)}`
    );
    if (!response.ok) throw new Error("Errore nel recupero dati buono");
    const buono = await response.json();
    document.getElementById("valore").value = buono.valore;
    document.getElementById("tipologia").value = buono.tipologia;
  } catch (err) {
    msgDiv.textContent = "Errore: " + err.message;
    msgDiv.style.color = "#b00020";
    form.style.display = "none";
    return;
  }

  form.onsubmit = async function (e) {
    e.preventDefault();
    msgDiv.textContent = "";

    const valore = document.getElementById("valore").value.trim();
    const tipologia = document.getElementById("tipologia").value.trim();

    if (!valore || isNaN(valore) || Number(valore) <= 0) {
      msgDiv.textContent = "Inserisci un importo valido (numero positivo).";
      msgDiv.style.color = "#b00020";
      return;
    }

    const buonoModificato = {
      id: id,
      valore: Number(valore),
      tipologia: tipologia,
    };

    try {
      const response = await fetch(
        `http://localhost:8080/buono/${encodeURIComponent(
          codiceFiscale
        )}/${encodeURIComponent(id)}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(buonoModificato),
        }
      );

      const text = await response.text();

      if (response.ok && !text.startsWith("ERROR")) {
        window.location.href = "../buoni";
      } else {
        msgDiv.textContent = "Errore: " + JSON.parse(text).message;
        msgDiv.style.color = "#b00020";
      }
    } catch (err) {
      msgDiv.textContent = "Errore di rete: " + err.message;
      msgDiv.style.color = "#b00020";
    }
  };
});
