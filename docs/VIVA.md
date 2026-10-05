# Viva map: topic -> where to show it

| Topic | File | What to say (short) |
|---|---|---|
| JDBC, MySQL connection | util/DBConnection.java | Loads the driver (Connector/J), reads db.properties, `DriverManager.getConnection`. |
| PreparedStatement | any dao/*.java | `?` placeholders. Input is never joined into SQL, so no SQL injection. |
| ResultSet | dao/*DAO.java `map(...)` | Reads one row at a time into a bean. |
| CRUD | ServiceDAO, StudentDAO | Create = insert, Read = find*, Update = setActive/setStatus, Delete/cancel = TokenService.cancelToken (we keep history, so we mark status instead of deleting rows). |
| DAO | dao/ | Only SQL. The caller gives the Connection. |
| MVC | model / views (JSP) / controller (servlets) | Servlet -> Service -> DAO -> JDBC -> MySQL. JSP never touches SQL. JSPs are in WEB-INF so a browser cannot open them directly. |
| Servlet lifecycle | controller/BaseServlet + servlets | Tomcat: init() once, service() -> doGet/doPost for each request, destroy() at the end. One servlet object serves many threads. |
| JSP | WEB-INF/views | HTML + EL + JSTL. `c:out` prevents XSS. |
| Sessions | LoginServlet, LogoutServlet | `getSession`, attributes userId/role, `changeSessionId` after login, `invalidate` at logout, 30 min timeout. |
| Filters | filter/AuthFilter | Runs before /student, /staff, /admin. Not logged in -> login page. Wrong role -> 403. |
| Password handling | util/PasswordUtil | PBKDF2 with random salt, 120000 rounds. Only the hash is stored. |
| Transactions | service/TokenService.inTransaction | autoCommit=false; commit if all steps work; rollback on any error. Token + history + notification are saved together. |
| TCP, client/server | network/QueueServer, QueueClient | ServerSocket + Socket, text protocol: PING, QUEUE id, STATS, QUIT. |
| Multithreading | QueueServer.handle, QueueClient | One thread per connected client; the demo client starts many threads at once. |
| Synchronization | TokenService (lock per service + `FOR UPDATE`), QueueServer.countRequest | Two students can never get the same token number. |
| ExecutorService | QueueServer (fixed pool of 8), MailUtil (pool of 2) | Thread pool instead of creating a thread for each job. |
| REST, JSON | controller/ApiServlet, util/Json | GET endpoints return JSON. |
| Node.js | node-api/server.js | Gateway: validation, 3-second cache, /api/board joins many calls, TCP client, serves the live board. |
| JavaMail | util/MailUtil | jakarta.mail, sends in a background thread, off unless mail.enabled=true. |
| Row locks | ServiceDAO.findByIdForUpdate, TokenDAO.findNextWaitingForUpdate | `SELECT ... FOR UPDATE` blocks other transactions until commit. |

## Token numbers
`prefix + "-" + (101 + number of tokens already made for that service)`, inside the transaction while the service row is locked.
