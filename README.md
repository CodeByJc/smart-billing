# Smart Billing & Inventory Management System

Smart Billing is a Java web application for product management, billing, invoice generation, and stock tracking. It uses Spring MVC, JSP, JDBC, MySQL, and Apache Tomcat.

## Features

- Admin login and logout
- Dashboard with product and invoice summary
- Product CRUD with search and stock tracking
- Billing with multi-item invoice creation
- Invoice history and PDF invoice support
- MySQL-backed storage with JDBC

## Tech Stack

- Java 17
- Spring MVC 6.1
- JSP / JSTL
- JDBC
- MySQL 8
- Apache Tomcat 10.1
- Maven
- Bootstrap 5

## Prerequisites

Make sure these are installed before running the project:

- JDK 17 or newer
- Maven 3.8+
- MySQL 8+
- Apache Tomcat 10.1 is not required separately if you use the Cargo Maven plugin

## Project Structure

```text
src/main/java/com/smartbilling/
├── config/      Spring and web configuration
├── controller/  Web controllers
├── dao/         JDBC data access layer
├── filter/      Authentication filter
├── model/       POJO classes
├── service/     Business logic
└── util/        Database and helper utilities

src/main/webapp/
├── WEB-INF/views/  JSP pages
├── css/            Stylesheets
├── js/             JavaScript files
└── images/         Static assets
```

## Setup

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd smart-billing
```

If you already have the folder open in VS Code, you can skip the clone step.

### 2. Create the MySQL database

Import the schema file:

```bash
mysql -u root -p < schema.sql
```

This creates the `smart_billing` database, tables, and sample data including the default admin user.

If you prefer to run the SQL manually, the schema file already contains:

- `CREATE DATABASE IF NOT EXISTS smart_billing;`
- `USE smart_billing;`

### 3. Configure database credentials

Edit [src/main/resources/db.properties](src/main/resources/db.properties) and set your local MySQL username and password:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/smart_billing
db.username=your_mysql_user
db.password=your_mysql_password
```

Important:

- Keep the database name as `smart_billing`
- Do not add a trailing slash to the JDBC URL

## Run

### Option 1: Run with Cargo Maven

This is the easiest way to start the application locally:

```bash
mvn clean package cargo:run -DskipTests
```

When the server starts successfully, open:

```text
http://localhost:8081/smart-billing/
```

### Option 2: Build the WAR only

If you only want the deployable file:

```bash
mvn clean package -DskipTests
```

The WAR is created at:

```text
target/smart-billing.war
```

You can then deploy that WAR to your own Tomcat 10.1 server.

## Default Login

The schema inserts a default admin account:

| Username | Password |
| -------- | -------- |
| admin    | admin123 |

## Useful Maven Commands

```bash
mvn clean compile
mvn test
mvn clean package -DskipTests
mvn clean package cargo:run -DskipTests
```

## Troubleshooting

### Port already in use

If Cargo fails because a port is busy, update the ports in [pom.xml](pom.xml):

- `cargo.servlet.port` controls the web port
- `cargo.rmi.port` controls the Cargo/Tomcat control port

Current working values in this project are:

- Web port: `8081`
- RMI port: `11099`

### Unknown database error

If login fails with an unknown database error, check [src/main/resources/db.properties](src/main/resources/db.properties) and confirm the URL is exactly:

```properties
db.url=jdbc:mysql://localhost:3306/smart_billing
```

### 500 error on login

If you see a 500 during login, make sure the project was built with parameter metadata enabled. This project already configures the Maven compiler plugin with `<parameters>true</parameters>`.

## Notes

- The application uses JDBC directly, not Hibernate.
- Authentication is session-based.
- Uploaded or generated runtime files under `target/` are build artifacts and should not be committed.

## License

This project is for educational use.
