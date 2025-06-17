document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("buonoForm");
  const msgDiv = document.getElementById("buono-msg");
  const codiceFiscale = sessionStorage.getItem("codiceFiscale");

  if (!codiceFiscale) {
    msgDiv.textContent = "Utente non autenticato.";
    msgDiv.style.color = "#b00020";
    form.style.display = "none";
    return;
  }

  form.onsubmit = async function (e) {
    e.preventDefault();
    msgDiv.textContent = "";

    const valore = document.getElementById("valore").value.trim();
    const tipologia = document.getElementById("tipologia").value.trim();

    // --- Controllo valore valido ---
    if (!valore || isNaN(valore) || Number(valore) <= 0) {
      msgDiv.textContent = "Inserisci un importo valido (numero positivo).";
      msgDiv.style.color = "#b00020";
      return;
    }

    const buono = {
      valore: valore,
      tipologia: tipologia,
    };

    try {
      const response = await fetch(
        `http://localhost:8080/buono/${encodeURIComponent(codiceFiscale)}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(buono),
        }
      );

      const text = await response.text();

      if (response.ok && !text.startsWith("ERROR")) {
        // Reindirizza alla pagina dei buoni
        window.location.href = "../buoni";
      } else {
        let errorMsg = text;
        if (
          text.includes(
            "Buono value must be greater than zero or exceeds user's remaining balance"
          )
        ) {
          errorMsg =
            "Errore: Il valore del buono deve essere maggiore di zero e non superare il saldo rimanente dell'utente.";
        }
        msgDiv.textContent = errorMsg;
        msgDiv.style.color = "#b00020";
      }
    } catch (err) {
      msgDiv.textContent = "Errore di rete: " + err.message;
      msgDiv.style.color = "#b00020";
    }
  };
});
