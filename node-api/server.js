/**
 * CampusFlow Node.js gateway (no npm packages, Node 18+).
 * Why Node is here:
 *   1. Public REST/JSON API in front of the Java backend. It validates input, caches for 3 seconds
 *      (many display screens will not overload Tomcat) and adds /api/board, which joins several Java calls into one.
 *   2. Serves the live "Queue Board" page for the college hall screen.
 *   3. /api/tcp/queue/:id talks to the Java TCP server directly (Node as a TCP client).
 * All real business logic and the database stay in Java.
 */
const http = require("http");
const net = require("net");
const fs = require("fs");
const path = require("path");

const PORT = Number(process.env.PORT || 3000);
const JAVA_API = (process.env.JAVA_API || "http://localhost:8080/CampusFlow").replace(/\/$/, "");
const TCP_HOST = process.env.TCP_HOST || "localhost";
const TCP_PORT = Number(process.env.TCP_PORT || 9090);
const CACHE_MS = 3000;

const cache = new Map();

async function javaGet(apiPath) {
  const hit = cache.get(apiPath);
  if (hit && Date.now() - hit.time < CACHE_MS) return hit.value;
  const res = await fetch(JAVA_API + apiPath, { signal: AbortSignal.timeout(5000) });
  const value = { status: res.status, body: await res.json() };
  cache.set(apiPath, { time: Date.now(), value });
  return value;
}

function send(res, status, data) {
  res.writeHead(status, { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" });
  res.end(JSON.stringify(data));
}

function tcpQueue(serviceId) {
  return new Promise((resolve, reject) => {
    const socket = net.createConnection({ host: TCP_HOST, port: TCP_PORT });
    let data = "";
    socket.setTimeout(4000, () => { socket.destroy(); reject(new Error("TCP timeout")); });
    socket.on("connect", () => socket.write(`QUEUE ${serviceId}\nQUIT\n`));
    socket.on("data", (chunk) => (data += chunk));
    socket.on("error", reject);
    socket.on("close", () => {
      try { resolve(JSON.parse(data.split("\n")[0])); } catch (e) { reject(new Error("Bad TCP reply")); }
    });
  });
}

async function board() {
  const services = await javaGet("/api/services");
  const queues = await Promise.all(services.body.map((s) => javaGet(`/api/queue/${s.id}`)));
  return {
    appUrl: JAVA_API + "/",
    updated: new Date().toISOString(),
    services: services.body.map((s, i) => ({ id: s.id, name: s.name, prefix: s.prefix, waiting: queues[i].body.waiting, nowServing: queues[i].body.nowServing })),
  };
}

const TYPES = { ".html": "text/html; charset=utf-8", ".css": "text/css", ".js": "text/javascript" };

function serveStatic(req, res) {
  const name = req.url === "/" || req.url === "/board" ? "board.html" : path.basename(req.url.split("?")[0]);
  const file = path.join(__dirname, "public", name);
  fs.readFile(file, (err, content) => {
    if (err) return send(res, 404, { error: "Not found" });
    res.writeHead(200, { "Content-Type": TYPES[path.extname(file)] || "application/octet-stream" });
    res.end(content);
  });
}

const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, "http://localhost");
  const parts = url.pathname.split("/").filter(Boolean); // ["api","queue","1"]
  if (req.method !== "GET") return send(res, 405, { error: "Only GET is supported" });

  try {
    if (parts[0] !== "api") return serveStatic(req, res);

    if (parts[1] === "health") {
      let java = "down";
      try { java = (await javaGet("/api/services")).status === 200 ? "up" : "error"; } catch (e) { /* stays down */ }
      return send(res, 200, { node: "up", java, javaApi: JAVA_API });
    }
    if (parts[1] === "services" && parts.length === 2) { const r = await javaGet("/api/services"); return send(res, r.status, r.body); }
    if (parts[1] === "queue" && /^\d+$/.test(parts[2] || "")) { const r = await javaGet(`/api/queue/${parts[2]}`); return send(res, r.status, r.body); }
    if (parts[1] === "token" && /^[A-Za-z]{2,5}-\d+$/.test(parts[2] || "")) { const r = await javaGet(`/api/token/${parts[2].toUpperCase()}`); return send(res, r.status, r.body); }
    if (parts[1] === "board") return send(res, 200, await board());
    if (parts[1] === "tcp" && parts[2] === "queue" && /^\d+$/.test(parts[3] || "")) return send(res, 200, await tcpQueue(parts[3]));
    return send(res, 404, { error: "Unknown endpoint" });
  } catch (e) {
    return send(res, 502, { error: "Java backend not reachable. Is Tomcat running?", detail: e.message });
  }
});

server.listen(PORT, () => {
  console.log(`CampusFlow Node gateway running:  http://localhost:${PORT}/`);
  console.log(`Java backend expected at:         ${JAVA_API}`);
  console.log(`Java TCP server expected at:      ${TCP_HOST}:${TCP_PORT}`);
});
