# CampusFlow - Smart College Service & Queue Management System

Students take digital tokens, staff call them, admin manages everything.
The database has **no fake data**. You create the first admin yourself on the /setup page.

## What runs where

| Part | Technology | Port | Started by |
|---|---|---|---|
| Web app (login, dashboards, pages, REST, TCP server) | Java Servlets + JSP on **Tomcat** | 8080 | start-all script |
| TCP queue server | Java (inside the web app) | 9090 | starts with the web app |
| Live Queue Board + REST gateway | **Node.js** | 3000 | start-all script |
| Data | **MySQL** (Workbench to view) | 3306 | you |

Important: Servlets and JSP are part of your Java syllabus, so they need Tomcat. There is no way around it.
But you do NOT need to do anything by hand: `start-all` builds the project, puts it in Tomcat, starts Tomcat and starts Node.

## One-time setup (about 20 minutes)

1. **Install** (if missing): JDK 25 (you have it), Node.js 18 or newer, MySQL Server + Workbench.
2. **Tomcat 10.1**: download the "Core: zip" from tomcat.apache.org (version 10.1.x), unzip it, e.g. `C:\tomcat`.
3. **Environment variables** (Windows: Start menu > "Edit the system environment variables"):
   - `JAVA_HOME` = your JDK 25 folder
   - `CATALINA_HOME` = `C:\tomcat` (your Tomcat folder)
   Close and reopen the terminal after setting them.
4. **Database**: in Workbench open `database/schema.sql` and run it. Check: `USE campusflow; SHOW TABLES;` shows 6 tables, all empty.
5. **Libraries**: double-click `get-libs.bat` (needs internet). It puts 7 jar files in `lib/`
   (MySQL Connector/J, JSTL, JavaMail). If curl fails, download them manually from search.maven.org, names are inside the script.
6. **Config**: copy `config/db.properties.template` to `config/db.properties`, then type your MySQL `db.user` and `db.password`.

## Run

Open a terminal in the CampusFlow folder:

    start-all.bat        (Linux/Mac: ./start-all.sh)

Wait about 10 seconds, then open:
- App: http://localhost:8080/CampusFlow/
- Queue Board: http://localhost:3000/

Stop: close the Node window (Ctrl+C) and run `%CATALINA_HOME%\bin\shutdown.bat`.
After you change any Java or JSP file, run `build.bat` again (Tomcat reloads the new war by itself).

## First use (in this order)

1. Open the app. It sends you to **/setup**. Create the administrator (your real name, email, password).
2. Log in as **Staff / Admin**. Go to **Services** and add real services, e.g. name "Exam Section", prefix "EX".
   Tokens will look like `EX-101`, `EX-102`...
3. Go to **Staff** and add a staff member for each service (choose role Staff and the service).
4. Students open **Register**, then log in, click **Get token**.
5. Staff logs in, opens **My Queue**, clicks **Call next**, then **Complete** or **Skip**.
6. Students see position and status update live. Notifications appear in the bell page.
7. Open the Queue Board on port 3000 (for the college hall screen).

## Test checklist

- `http://localhost:3000/api/health` shows `"node":"up","java":"up"`
- `http://localhost:8080/CampusFlow/api/services` shows your services as JSON
- Student tries `/CampusFlow/admin/dashboard` -> "403" page (authorization works)
- Two students take tokens at once -> numbers are different (transaction + lock works)
- TCP demo with many clients:
  `java -cp build\WEB-INF\classes com.campusflow.network.QueueClient localhost 9090 1 10`
  (the last number is the service id from the services table)

## REST endpoints

| URL | Meaning |
|---|---|
| GET /api/services | active services + waiting count (Java: port 8080/CampusFlow, Node: port 3000) |
| GET /api/queue/{serviceId} | waiting count + now serving |
| GET /api/token/{EX-101} | token status + position |
| GET /api/student/me | logged-in student's active tokens (Java only, needs login) |
| GET /api/board | (Node only) all queues in one call |
| GET /api/tcp/queue/{serviceId} | (Node only) asks the Java TCP server |
| GET /api/health | (Node only) is Node and Java alive |

## JavaMail (optional)

Emails are OFF by default. To turn on: in `config/db.properties` set `mail.enabled=true` and fill host, port, user, password
(Gmail needs an "App Password"). Students get an email for token created / called / completed / skipped.

## Troubleshooting

- **"db.properties not found"**: you did not create `config/db.properties`, then run `build.bat` again.
- **"Cannot reach the database"**: MySQL not running, or wrong user/password in `db.properties`.
- **Page shows 404 at /CampusFlow/**: Tomcat is still starting (wait), or the war did not deploy (check `%CATALINA_HOME%\logs\catalina.*.log`).
- **COMPILE FAILED**: read the first error line; the most common cause is an empty `lib/` folder or wrong CATALINA_HOME.
- **Pages look plain (no colours)**: Bootstrap loads from the internet (CDN). Connect to the internet.
- **Port 8080 or 3000 busy**: for Node use `set PORT=3001` before starting. For Tomcat edit `conf/server.xml`.
- **Node says "Java backend not reachable"**: Tomcat is not running yet. If your Tomcat port is not 8080, set `JAVA_API=http://localhost:PORT/CampusFlow`.

## Project map

    database/schema.sql              tables only, no data
    config/db.properties.template    your DB + TCP + mail settings
    src/main/java/com/campusflow/
      model/       beans (Student, Staff, Service, Token, ...)
      dao/         SQL only (PreparedStatement, ResultSet)
      service/     business rules + transactions
      controller/  servlets (Setup, Login, Register, Student, Staff, Admin, Api)
      filter/      AuthFilter (login + role check), EncodingFilter
      network/     QueueServer (TCP + ExecutorService), QueueClient (demo)
      listener/    starts the TCP server with the web app
      util/        DBConnection (JDBC), PasswordUtil, Json, MailUtil
    src/main/webapp/WEB-INF/views/   JSP pages (only reachable through servlets)
    src/main/webapp/css, js          styles and live-update script
    node-api/                        Node.js gateway + live board (no npm install needed)
    docs/VIVA.md                     where to find every viva topic in the code
