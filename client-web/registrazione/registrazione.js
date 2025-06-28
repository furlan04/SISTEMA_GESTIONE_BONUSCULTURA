document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("registrazioneForm");

  // Crea o seleziona il div per mostrare gli errori
  let errorDiv = document.getElementById("registrazione-error");
  if (!errorDiv) {
    errorDiv = document.createElement("div");
    errorDiv.id = "registrazione-error";
    errorDiv.className = "msg";
    form && form.parentNode.insertBefore(errorDiv, form.nextSibling);
  }

  if (form) {
    form.addEventListener("submit", async function (e) {
      e.preventDefault();

      errorDiv.textContent = "";

      const formData = new FormData(form);
      const data = {
        nome: formData.get("nome"),
        cognome: formData.get("cognome"),
        email: formData.get("email"),
        codiceFiscale: formData.get("codiceFiscale"),
      };

      try {
        const response = await fetch("http://localhost:8080/utente", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(data),
        });

        const text = await response.text();

        if (response.ok && !text.startsWith("ERROR")) {
          // Salva nome e codice fiscale per la navbar della home
          sessionStorage.setItem("codiceFiscale", data.codiceFiscale);
          sessionStorage.setItem("nome", data.nome);
          window.location.href = "../home";
        } else {
          let message = "Errore nella registrazione.";
          try {
            message = JSON.parse(text).message || message;
          } catch {}
          errorDiv.textContent = message;
        }
      } catch (err) {
        errorDiv.textContent = "Errore di rete: " + err.message;
      }
    });
  }
});
