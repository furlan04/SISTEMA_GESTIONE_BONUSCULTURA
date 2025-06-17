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

    const valore = document.getElementById("valore").value;
    const tipologia = document.getElementById("tipologia").value;

    // Costruisci il JSON come richiesto da BuonoResource.java
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
        msgDiv.textContent = "Errore: " + text;
        msgDiv.style.color = "#b00020";
      }
    } catch (err) {
      msgDiv.textContent = "Errore di rete: " + err.message;
      msgDiv.style.color = "#b00020";
    }
  };
});
