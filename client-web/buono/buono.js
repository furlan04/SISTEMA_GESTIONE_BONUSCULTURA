// buono.js

document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(location.search);
  const id = params.get("id");
  if (!id) return showError("ID buono mancante nell'URL.");

  fetchBuono(id);
});

/**
 * Effettua la fetch per ottenere i dati del buono.
 */
function fetchBuono(id) {
  const url = `http://localhost:8080/buono/${encodeURIComponent(id)}`;

  fetch(url)
    .then(response => {
      if (!response.ok) throw new Error("Errore nella risposta dal server");
      return response.json();
    })
    .then(data => renderBuono(data))
    .catch(err => showError(err.message));
}

/**
 * Mostra i dati del buono nella pagina.
 */
function renderBuono(buono) {
  const container = document.getElementById("buono-container");
  
  // Determina se il buono è già stato consumato
  const isConsumed = !(buono.dataConsumo === null || buono.dataConsumo === undefined);
  
  // Crea il contenuto per la data di consumo o il bottone
  const consumoContent = isConsumed 
    ? `<p><strong>Data Consumo:</strong> ${formatDate(buono.dataConsumo)}</p>`
    : `<button id="consuma-buono" class="button-consume" data-id="${buono.id}">
         <span class="button-icon">🎫</span>
         Consuma Buono
       </button>`;

  container.innerHTML = `
    <div class="buono">
      <h3>${capitalize(buono.tipologia)}</h3>
      <p><strong>Data Creazione:</strong> ${formatDate(buono.dataCreazione)}</p>
      ${consumoContent}
      <p><strong>Valore:</strong> €${buono.valore.toFixed(2)}</p>
      <div class="button-group">
        <a href="../buoni" class="button-back">
          <span class="button-icon">←</span>
          Torna ai Buoni
        </a>
      </div>
    </div>
  `;

  // Aggiungi event listener per il bottone consuma se presente
  if (!isConsumed) {
    setupConsumaButton(buono.id);
  }
}

/**
 * Configura il bottone per consumare il buono.
 */
function setupConsumaButton(buonoId) {
  const btn = document.getElementById("consuma-buono");
  if (btn) {
    btn.addEventListener("click", () => consumaBuono(buonoId));
  }
}

/**
 * Effettua la chiamata API per consumare il buono.
 */
function consumaBuono(buonoId) {
  const btn = document.getElementById("consuma-buono");
  const cf = sessionStorage.getItem("codiceFiscale");
  
  if (!cf) {
    showError("Codice fiscale non trovato. Effettua il login.");
    return;
  }

  // Disabilita il bottone durante la chiamata
  btn.disabled = true;
  btn.innerHTML = `<span class="button-icon">⏳</span> Consumando...`;

  const url = `http://localhost:8080/buono/${encodeURIComponent(buonoId)}/consuma`;
  
  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      codiceFiscale: cf
    })
  })
  .then(response => {
    if (!response.ok) throw new Error("Errore durante il consumo del buono");
    return response.json();
  })
  .then(data => {
    // Ricarica i dati del buono per mostrare la data di consumo aggiornata
    fetchBuono(buonoId);
    showSuccessMessage("Buono consumato con successo!");
  })
  .catch(err => {
    showError(err.message);
    // Ripristina il bottone in caso di errore
    btn.disabled = false;
    btn.innerHTML = `<span class="button-icon">🎫</span> Consuma Buono`;
  });
}

/**
 * Mostra un messaggio di successo.
 */
function showSuccessMessage(msg) {
  const container = document.getElementById("buono-container");
  const successDiv = document.createElement("div");
  successDiv.className = "success-message";
  successDiv.innerHTML = `
    <p style="color: #4caf50; text-align: center; padding: 10px; background: #e8f5e8; border-radius: 8px; margin: 10px 0;">
      ✅ ${msg}
    </p>
  `;
  container.insertBefore(successDiv, container.firstChild);
  
  // Rimuovi il messaggio dopo 3 secondi
  setTimeout(() => {
    if (successDiv.parentNode) {
      successDiv.parentNode.removeChild(successDiv);
    }
  }, 3000);
}

/**
 * Mostra un messaggio di errore.
 */
function showError(msg) {
  const container = document.getElementById("buono-container");
  container.innerHTML = `
    <div class="error-message">
      <p style="color: #f44336; text-align: center; padding: 20px; background: #ffeaea; border-radius: 8px; border-left: 4px solid #f44336;">
        ❌ Errore: ${msg}
      </p>
      <div class="button-group">
        <a href="../buoni" class="button-back">
          <span class="button-icon">←</span>
          Torna ai Buoni
        </a>
      </div>
    </div>`;
}

/**
 * Formatta la data secondo convenzione italiana.
 */
function formatDate(isoDate) {
  const dt = new Date(isoDate);
  return dt.toLocaleDateString("it-IT");
}

/**
 * Capitalizza la prima lettera della stringa.
 */
function capitalize(str) {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1);
}