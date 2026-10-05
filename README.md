# CampusFlow

A Java-based college service and queue management system designed to manage students, staff, campus services, service tokens, and live queues.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Main application development |
| Java Servlets | Backend request handling |
| JSP | Dynamic web pages |
| Apache Tomcat | Java web application server |
| JDBC | Java-to-MySQL database connectivity |
| MySQL | Application database |
| Node.js | Live queue board and gateway |
| HTML5 | Frontend structure |
| CSS3 | Frontend styling |
| JavaScript | Frontend interaction |
| REST API | Communication between application components |
| JSON | Data exchange |
| TCP/IP Sockets | Queue communication |
| Git | Version control |
| GitHub | Code hosting |
| Visual Studio Code | Development environment |

## Features

- Student Registration and Login
- Staff Management
- Admin Dashboard
- College Service Management
- Token Generation
- Live Queue Management
- Student and Staff Queue Handling
- Queue Status Updates
- Service Availability Management
- Activity Tracking
- Authentication and Role Management
- MySQL Database Integration
- Node.js Live Queue Board
- TCP Client-Server Communication
- Notifications

## System Architecture

```text
                    CampusFlow
                        |
                        v
                  Web Browser
                        |
                 HTML / CSS / JS
                        |
                        v
                Java Servlets + JSP
                        |
                    Tomcat
                        |
              +---------+---------+
              |                   |
              v                   v
            JDBC            TCP Queue Server
              |                   |
              v                   v
            MySQL           Node.js Gateway
                                  |
                                  v
                           Live Queue Board
```
How the System Works
User Login
    ↓
Dashboard
    ↓
Select College Service
    ↓
Generate Token
    ↓
Join Queue
    ↓
Queue Management
    ↓
Staff Serves Token
    ↓
Queue Status Updated
    ↓
Live Queue Board


Database

CampusFlow uses MySQL as its database.

Database Information	Value
Database	campusflow
Database System	MySQL
Host	localhost
Port	3306
Java Application	localhost:8080
Node.js Gateway	localhost:3000
TCP Queue Server	localhost:9090

The database configuration is stored locally in:

config/db.properties

The actual db.properties file is excluded from GitHub using .gitignore.

A template is provided as:

config/db.properties.template
Main Components
Component	Role
Java Servlets	Handle backend requests and business logic
JSP	Generate dynamic web pages
Tomcat	Runs the Java web application
JDBC	Connects Java with MySQL
MySQL	Stores application data
Node.js	Provides gateway and live queue functionality
TCP Server	Handles queue communication
JavaScript	Handles frontend interactions
JSON	Transfers data between components
Project Structure
CampusFlow/
├── config/
├── database/
├── docs/
├── node-api/
├── src/
├── .gitignore
├── build.bat
├── build.sh
├── get-libs.bat
├── get-libs.sh
├── start-all.bat
├── start-all.sh
└── README.md
Important Files
File / Folder	Purpose
src/	Java source code and web application files
node-api/	Node.js gateway and live queue application
database/	MySQL database scripts
config/	Local database configuration
docs/	Project and viva documentation
build.bat	Windows build script
start-all.bat	Windows startup script
README.md	Project documentation
.gitignore	Prevents sensitive and generated files from being uploaded
Requirements
Java JDK
Apache Tomcat
MySQL Server
Node.js
Visual Studio Code
Setup
1. Create the MySQL Database

Create the database:

CREATE DATABASE campusflow;

Import the database/schema files provided in the database folder.

2. Configure Database Connection

Copy or use the provided template:

config/db.properties.template

Create your local:

config/db.properties

and enter your MySQL connection details.

Do not upload db.properties to GitHub because it may contain your database credentials.

3. Build the Java Application

On Windows, run:

build.bat

This builds the CampusFlow Java web application for Tomcat.

4. Start Tomcat

Deploy the generated CampusFlow application to Apache Tomcat and start the Tomcat server.

The Java application runs at:

http://localhost:8080/CampusFlow
5. Start the Node.js Gateway

Open a terminal inside:

node-api

Install dependencies:

npm install

Start the Node.js service:

npm start

The Node.js service runs on:

http://localhost:3000
Ports Used
Service	Port
MySQL	3306
Apache Tomcat / Java	8080
Node.js	3000
TCP Queue Server	9090
Security
Passwords are hashed before storage.
Database credentials are stored in a local configuration file.
config/db.properties is excluded using .gitignore.
Compiled Java files and generated build files are excluded from the repository.
Learning Outcomes

This project demonstrates practical implementation of:

Java Web Development
Servlets and JSP
JDBC and MySQL
CRUD Operations
Authentication and Sessions
Role-Based Access
MVC Architecture
TCP Socket Programming
Multithreading
REST APIs
JSON Communication
Node.js
Queue Management
Database Integration
Git and GitHub
GitHub

https://github.com/singh-builds/CampusFlow

Author

Aditya Rajesh Singh

GitHub: https://github.com/singh-builds


After pasting:

**Ctrl + S → Commit changes**

Use commit message:

```text
Improve CampusFlow README

Then click Commit changes.
