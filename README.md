# SMPP SMS Gateway Project

This project is a Jakarta EE web application that lets users register, log in, manage their profile, and send SMS through an SMPP simulator.

## Features

- User registration and login
- OTP-based verification flow
- Profile management
- SMS sending through an SMPP connection
- Admin panel for customer management and statistics

## Technology Stack

- Java 17+
- Jakarta EE 10 / Servlet 6
- Maven
- PostgreSQL
- jSMPP
- Tomcat 10.1+

## SMPP Simulator Settings

The servlet is configured to connect to the simulator with these values:

- Host: `127.0.0.1`
- Port: `2776`
- System ID: `username`
- Password: `pass1234`
- Address range: `6666`
- Bind type: `TRANSCIEVER`

## Prerequisites

Install the following before running the project:

- Java JDK 17 or higher
- Apache Maven
- Apache Tomcat 10.1+

## Run the SMPP Simulator

Open a terminal and start the simulator:

```bash
cd /home/ali/Desktop/ITI 9 Months/Projects/Smpp/restcomm-smpp-simulator
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 ./bin/run.sh
```

## Build the Application

From the project root:

```bash
cd /home/ali/Desktop/ITI 9 Months/Projects/Smpp/sms-smpp-gateway
mvn -DskipTests package
```

This creates the WAR file at:

```text
target/twilio.war
```

## Deploy and Run

1. Copy the WAR file to Tomcat's `webapps` directory:

```bash
cp target/twilio.war /path/to/tomcat/webapps/
```

2. Start Tomcat:

```bash
/path/to/tomcat/bin/startup.sh
```

3. Open the application in your browser:

```text
http://localhost:8080/twilio/
```

## Notes

- The application uses a shared database connection initialized during startup.
- If login or SMS sending fails, confirm that the simulator is running and that the database connection is available.

