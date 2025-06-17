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
    .then((response) => {
      if (!response.ok) throw new Error("Errore nella risposta dal server");
      return response.json();
    })
    .then((data) => renderBuono(data))
    .catch((err) => showError(err.message));
}

/**
 * Mostra i dati del buono nella pagina.
 */
function renderBuono(buono) {
  const container = document.getElementById("buono-container");

  // Determina se il buono è già stato consumato
  const isConsumed = !(
    buono.dataConsumo === null || buono.dataConsumo === undefined
  );

  // Crea il contenuto per la data di consumo o il bottone
  const consumoContent = isConsumed
    ? `<p><strong>Data Consumo:</strong> ${formatDate(buono.dataConsumo)}</p>`
    : `<button id="consuma-buono" class="button-consume" data-id="${buono.id}">
         <span class="button-icon">🎫</span>
         Consuma Buono
       </button>`;

  // Mostra il bottone elimina solo se NON è consumato
  const modifyButtonHtml = !isConsumed
    ? `<button id="modify-buono" class="button-modify">
         <span class="button-icon">✏️</span>
         Modifica
       </button>`
    : "";

  const deleteButtonHtml = !isConsumed
    ? `<button id="delete-buono" class="button-delete">
         <span class="button-icon">🗑️</span>
         Elimina Buono
       </button>`
    : "";

  container.innerHTML = `
    <div class="buono">
      <h3>${capitalize(buono.tipologia)}</h3>
      <p><strong>Data Creazione:</strong> ${formatDate(buono.dataCreazione)}</p>
      ${consumoContent}
      <p><strong>Valore:</strong> €${buono.valore.toFixed(2)}</p>
      <div class="button-group">
        ${modifyButtonHtml}
        ${deleteButtonHtml}
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
    const deleteBtn = document.getElementById("delete-buono");
    if (deleteBtn) {
      deleteBtn.addEventListener("click", () => deleteBuono(buono.id));
    }
    // Event listener per modifica
    const modifyBtn = document.getElementById("modify-buono");
    if (modifyBtn) {
      modifyBtn.addEventListener("click", () => {
        window.location.href = `../modificaBuono/index.html?id=${encodeURIComponent(
          buono.id
        )}`;
      });
    }
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

  const url = `http://localhost:8080/buono/${encodeURIComponent(
    buonoId
  )}/consuma`;

  fetch(url, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) throw new Error(response.text());
      return response.json();
    })
    .then((data) => {
      // Ricarica i dati del buono per mostrare la data di consumo aggiornata
      fetchBuono(buonoId);
      showSuccessMessage("Buono consumato con successo!");
    })
    .catch((err) => {
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

function deleteBuono(buonoId) {
  const cf = sessionStorage.getItem("codiceFiscale");
  if (!cf) {
    showError("Codice fiscale non trovato. Effettua il login.");
    return;
  }

  showConfirm("Sei sicuro di voler eliminare questo buono?", () => {
    fetch(
      `http://localhost:8080/buono/${encodeURIComponent(
        cf
      )}/${encodeURIComponent(buonoId)}`,
      { method: "DELETE" }
    )
      .then((response) => {
        if (!response.ok)
          throw new Error("Errore durante l'eliminazione del buono");
        window.location.href = "../buoni";
      })
      .catch((err) => showError(err.message));
  });
}

/**
 * Mostra un popup di conferma personalizzato.
 */
function showConfirm(message, onConfirm) {
  // Rimuovi eventuali popup già presenti
  const oldPopup = document.getElementById("custom-confirm-popup");
  if (oldPopup) oldPopup.remove();

  const popup = document.createElement("div");
  popup.id = "custom-confirm-popup";
  popup.style.position = "fixed";
  popup.style.top = "0";
  popup.style.left = "0";
  popup.style.width = "100vw";
  popup.style.height = "100vh";
  popup.style.background = "rgba(0,0,0,0.35)";
  popup.style.display = "flex";
  popup.style.alignItems = "center";
  popup.style.justifyContent = "center";
  popup.style.zIndex = "9999";

  popup.innerHTML = `
    <div style="background:#fff; padding:32px 28px; border-radius:16px; box-shadow:0 8px 32px rgba(0,0,0,0.18); text-align:center; min-width:260px;">
      <p style="font-size:1.1rem; margin-bottom:24px;">${message}</p>
      <button id="popup-confirm" style="margin-right:18px; padding:8px 24px; border-radius:8px; border:none; background:#f44336; color:#fff; font-weight:bold; cursor:pointer;">Elimina</button>
      <button id="popup-cancel" style="padding:8px 24px; border-radius:8px; border:none; background:#b0bec5; color:#2d3a4b; font-weight:bold; cursor:pointer;">Annulla</button>
    </div>
  `;

  document.body.appendChild(popup);

  document.getElementById("popup-confirm").onclick = function () {
    popup.remove();
    onConfirm();
  };
  document.getElementById("popup-cancel").onclick = function () {
    popup.remove();
  };
}
