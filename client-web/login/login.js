window.onload = function () {
  const form = document.getElementById("login-form");
  const errorDiv = document.getElementById("login-error");
  form.onsubmit = async function (e) {
    e.preventDefault();

    errorDiv.textContent = "";

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
        errorDiv.textContent = JSON.parse(text).message;
      } else {
        sessionStorage.setItem("codiceFiscale", codiceFiscale);
        window.location.href = "../home";
      }
    } catch (err) {
      errorDiv.textContent = "Errore di rete: " + err.message;
    }
  };
};
