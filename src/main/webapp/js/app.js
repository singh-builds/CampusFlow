// CampusFlow front-end: sidebar toggle + live token tracking through the REST API.
const ctx = document.body.dataset.ctx || "";

document.getElementById("menuBtn")?.addEventListener("click", () => document.getElementById("sidebar").classList.toggle("open"));

// Live tracking: every row with data-token asks GET /api/token/{number} and updates itself.
async function refreshTokens() {
  for (const row of document.querySelectorAll("[data-token]")) {
    try {
      const res = await fetch(`${ctx}/api/token/${encodeURIComponent(row.dataset.token)}`);
      if (!res.ok) continue;
      const t = await res.json();
      const badge = row.querySelector(".js-status"), pos = row.querySelector(".js-pos");
      if (badge) { badge.textContent = t.status; badge.className = `badge badge-status js-status st-${t.status}`; }
      if (pos) pos.textContent = t.status === "WAITING" ? t.position : "-";
      if (row.dataset.status && row.dataset.status !== t.status) location.reload(); // status changed -> reload page
    } catch (e) { /* network problem: try again next time */ }
  }
}
if (document.querySelector("[data-token]")) setInterval(refreshTokens, 8000);

// Staff queue page reloads by itself so new students appear.
if (document.body.dataset.autorefresh) setInterval(() => { if (!document.querySelector(".modal.show")) location.reload(); }, 15000);
