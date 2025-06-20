document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("registrazioneForm");
  if (form) {
    form.addEventListener("submit", async function (e) {
      e.preventDefault();

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

        if (response.ok) {
          // Reindirizza a ../home
          window.location.href = "../home";
        } else {
          const text = await response.text();
          alert("Errore nella registrazione: " + text);
        }
      } catch (err) {
        alert("Errore di rete: " + err.message);
      }
    });
  }
});
