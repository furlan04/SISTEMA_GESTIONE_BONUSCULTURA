document.addEventListener("DOMContentLoaded", () => {
  const nav = document.getElementById("nav-links");
  const cf = sessionStorage.getItem("codiceFiscale");

  nav.innerHTML = "";

  if (cf) {
    // Utente loggato
    nav.innerHTML = `
      <li><a href="../buoni">Buoni</a></li>
      <li><a href="#" id="logout">Logout</a></li>
    `;
    document.getElementById("logout").addEventListener("click", e => {
      e.preventDefault();
      sessionStorage.removeItem("codiceFiscale");
      location.reload();
    });
  } else {
    // Utente non loggato
    nav.innerHTML = `
      <li><a href="../login">Login</a></li>
    `;
  }
});