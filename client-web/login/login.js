window.onload = function () {
  const form = document.getElementById("login-form");
  form.onsubmit = async function (e) {
    e.preventDefault();

    const codiceFiscale = document.getElementById("codiceFiscale").value;

    try {
      const response = await fetch(
        `http://localhost:8080/utente/${encodeURIComponent(codiceFiscale)}`,
        {
          method: "GET",
        }
      );

      const text = await response.text();

      if (!response.ok || text.startsWith("ERROR")) {
        alert("Login fallito. " + text);
      } else {
        sessionStorage.setItem("codiceFiscale", codiceFiscale);
        window.location.href = "../home";
      }
    } catch (err) {
      alert("Errore di rete: " + err.message);
    }
  };
};
