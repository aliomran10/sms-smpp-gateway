# 💬 Twilio SMS Client Portal

A modern **Jakarta EE 10 / Servlet 6.0** web application that allows users to register, authenticate via SMS OTP, manage profiles, send SMS messages using the Twilio API, and view a search-friendly log of sent SMS messages. It also features a fully-functional admin panel for managing customers and tracking platform-wide usage statistics.

---

## ✨ Features

### 👤 User Portal
- **Secure Authentication**: Register and log in. Every registration initiates a Twilio SMS OTP verification flow.
- **Dynamic Dashboard**: Navigate between profile settings, messaging, and search logs.
- **SMS Composition**: Send customized messages using your Twilio credentials.
- **SMS Logs & Management**: Full-featured lookup with text keywords and datetime range filtering. Includes message deletion.
- **Profile Customization**: Update personal info and configure your individual Twilio API credentials dynamically.

### 🛡️ Admin Portal (`/admin/*`)
- **Customer Directory**: View, edit, add, and delete customer accounts.
- **Activity Breakdown**: Platform-wide metrics (total customers, total sent messages, messages sent today).
- **Interactive Analytics**: Table showing individual sent/received counts, last-active timestamps, and daily breakdown graphs.

---

## 🛠️ Technology Stack

- **Backend**: Java 17+, Jakarta EE 10 (Servlet API, JSP, JSTL)
- **Database**: PostgreSQL (Neon Serverless configuration)
- **APIs**: Twilio SDK (version 10.1.0)
- **Build System**: Apache Maven
- **UI Design System**: Dark-theme Glassmorphism with pure Vanilla CSS, responsive layouts, and interactive micro-animations.

---

## 🚀 Getting Started

### Prerequisites
1. **Java JDK 17 or higher** installed.
2. **Apache Maven 3.x** installed.
3. **Apache Tomcat 10.1+** (essential for Jakarta EE 10 compatibility; Tomcat 9 and older will fail).

---

### Run from NetBeans (Recommended)
1. Launch **NetBeans IDE**.
2. Select **File → Open Project** and choose the project directory.
3. NetBeans will parse it as a Maven Web application automatically.
4. Open **Tools → Servers** and make sure a **Tomcat 10.1+** instance is registered.
5. Right-click the project folder in the panel and select **Run** (or press `F6`).

---

### Run from Command Line / Terminal

#### 1. Navigate to the project root:
```bash
cd "/home/ali/Desktop/ITI 9 Months/Projects/Twilio-SMS-Client-Project"
```

#### 2. Clean and build the package:
Using NetBeans-bundled Maven or your system's global `mvn`:
```bash
/home/ali/Downloads/netbeans-30-bin/netbeans/java/maven/bin/mvn clean package
```
This builds `target/twilio.war`.

#### 3. Start Tomcat 10 and deploy the WAR:
```bash
# Copy the compiled WAR to Tomcat's webapps directory
cp target/twilio.war /home/ali/Downloads/apache-tomcat-10.1.57/webapps/

# Boot up the Tomcat server
/home/ali/Downloads/apache-tomcat-10.1.57/bin/startup.sh
```

#### 4. Open the Web App:
Navigate to:
```
http://localhost:8080/twilio/
```

---

## ⚙️ Configuration & Credentials

### Database Connection
The application database runs on Neon Serverless PostgreSQL. The connection lifecycle listener is configured inside `DBConnectionInitializer.java`.

### Twilio Integration
To enable SMS delivery and Registration OTP flows:
1. Log in to the application.
2. Click **Profile** (or go to `http://localhost:8080/twilio/profile`).
3. Fill in your Twilio settings:
   - **Account SID**
   - **Auth Token**
   - **Sender Number** (The Twilio phone number)
4. Submit the form to save credentials.

> ⚠️ **Note:** If using a Twilio trial account, you can only send SMS messages to phone numbers that are verified in your Twilio Console (under *Phone Numbers → Verified Caller IDs*).
