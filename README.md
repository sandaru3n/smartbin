# SmartBin - Waste Management System

A comprehensive web application for managing waste collection with separate portals for Residents, Waste Collectors, and Waste Management Authority.

## Features

### Core Features
- **Home Page**: Landing page with navigation to different user portals
- **User Registration**: Sign up page for all user types (Resident, Collector, Authority)
- **Role-Based Login**: Three separate login pages:
  - `/resident/login` - For residents
  - `/collector/login` - For waste collectors
  - `/authority/login` - For waste management authority
- **Dashboard**: Personalized dashboards for each user role
- **PostgreSQL Database**: Persistent data storage with JPA/Hibernate
- **Security**: Password encryption using BCrypt
- **Modern UI**: Beautiful, responsive design with gradient themes

### Resident Features
- **Bin Management**: View all waste bins and their status
- **Nearby Bin Search**: Find bins near your location
- **Bin Status Updates**: Update bin fill levels
- **Bin Alerts**: View bins that need attention
- **Reports**: View waste management statistics

### Collector Features
- **Route Management**: View assigned collection routes
- **QR Code Scanning**: Scan bin QR codes to unlock and collect waste
- **Collection Tracking**: Track collection progress and completion
- **Performance Metrics**: View collection statistics and efficiency
- **GPS Navigation**: Navigate to assigned bins using GPS

### Authority Features
- **System Monitoring**: Real-time monitoring of all bins and collections
- **Route Dispatch**: Assign and dispatch collectors to collection routes
- **Bin Management**: Manage bin alerts and status
- **Collector Management**: Manage collectors and assign them to regions
- **Report Generation**: Generate comprehensive reports:
  - Collection Reports
  - Performance Reports
  - Bin Status Reports
  - Overdue Bins Reports
  - System Overview Reports

## Technology Stack

- **Backend**: Spring Boot 3.5.6
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with BCrypt
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 21

## Prerequisites

Before running the application, ensure you have:

1. **Java 21** or higher installed
2. **Maven** installed
3. **PostgreSQL** database server installed and running
4. **IDE** (Optional: IntelliJ IDEA, Eclipse, or VS Code)

## Database Setup

1. **Install PostgreSQL** (if not already installed)
   - Download from: https://www.postgresql.org/download/

2. **Create Database**:
   ```sql
   CREATE DATABASE smartbin_db;
   ```

3. **Configure Database Credentials**:
   - The application uses `postgres` user with password `postgres` by default
   - If you get "password authentication failed" error, try one of these solutions:
   
   **Option A: Set PostgreSQL password to 'postgres'**:
   ```sql
   ALTER USER postgres PASSWORD 'postgres';
   ```
   
   **Option B: Update application.properties with your credentials**:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/smartbin_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
   
   **Option C: Create a new user**:
   ```sql
   CREATE USER smartbin_user WITH PASSWORD 'smartbin123';
   GRANT ALL PRIVILEGES ON DATABASE smartbin_db TO smartbin_user;
   ```

## Installation & Running

### Option 1: Using Maven Command Line

1. **Clone or navigate to the project directory**:
   ```bash
   cd d:\SmartBin\smartbin
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

### Option 2: Using IDE

1. **Import the project** as a Maven project in your IDE
2. **Wait for Maven** to download dependencies
3. **Run** the `SmartbinApplication.java` main class

### Option 3: Using JAR file

1. **Build the JAR**:
   ```bash
   mvn clean package
   ```

2. **Run the JAR**:
   ```bash
   java -jar target/smartbin-0.0.1-SNAPSHOT.jar
   ```

## Accessing the Application

Once the application is running, open your browser and navigate to:

- **Home Page**: http://localhost:8081
- **Sign Up**: http://localhost:8081/signup
- **Resident Login**: http://localhost:8081/resident/login
- **Collector Login**: http://localhost:8081/collector/login
- **Authority Login**: http://localhost:8081/authority/login

## User Roles

The application supports three user roles:

1. **RESIDENT**: For residents to manage their waste collection
2. **COLLECTOR**: For waste collectors to manage routes and pickups
3. **AUTHORITY**: For waste management authorities to oversee operations

## Sample Data

The application automatically creates sample users on first startup. You can use these credentials to test the system:

### Resident Accounts
- **Email**: john.resident@smartbin.com | **Password**: password123
- **Email**: sarah.resident@smartbin.com | **Password**: password123
- **Email**: michael.resident@smartbin.com | **Password**: password123

### Collector Accounts
- **Email**: david.collector@smartbin.com | **Password**: password123
- **Email**: emma.collector@smartbin.com | **Password**: password123
- **Email**: james.collector@smartbin.com | **Password**: password123

### Authority Accounts
- **Email**: admin.authority@smartbin.com | **Password**: password123
- **Email**: lisa.authority@smartbin.com | **Password**: password123
- **Email**: robert.authority@smartbin.com | **Password**: password123

> **Note**: Sample data is only inserted if the database is empty. To reset and reload sample data, drop the database and recreate it.

## Usage Guide

### 1. Quick Start (Using Sample Data)

1. Run the application
2. Navigate to the appropriate login page for your role
3. Use any of the sample credentials above
4. Click "Sign In" to access the dashboard

### 2. Create a New Account

1. Go to http://localhost:8081/signup
2. Fill in the registration form:
   - Full Name
   - Email Address (must be unique)
   - Password
   - Phone Number
   - Address (optional)
   - Select your role (Resident/Collector/Authority)
3. Click "Sign Up"
4. You'll be redirected to the appropriate login page

### 3. Login

1. Navigate to your role-specific login page
2. Enter your email and password
3. Click "Sign In"
4. Upon successful login, you'll be redirected to your dashboard

### 3. Dashboard Features

Each role has a dedicated dashboard with role-specific features:

- **Resident Dashboard**: Collection schedules, waste reports, statistics
- **Collector Dashboard**: Routes, status updates, performance metrics
- **Authority Dashboard**: System overview, user management, reports

## Project Structure

```
smartbin/
├── src/
│   ├── main/
│   │   ├── java/com/sliit/smartbin/smartbin/
│   │   │   ├── config/          # Security configuration
│   │   │   ├── controller/      # Web controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── model/           # Entity models
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── SmartbinApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf HTML templates
│   │       │   ├── home.html
│   │       │   ├── signup.html
│   │       │   ├── resident/
│   │       │   ├── collector/
│   │       │   └── authority/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Database Schema

The application automatically creates the following table:

**users**:
- id (Primary Key)
- name
- email (Unique)
- password (Encrypted)
- phone
- address
- role (RESIDENT/COLLECTOR/AUTHORITY)
- created_at
- updated_at

## API Endpoints

### Web Pages (GET)
- `GET /` - Home page
- `GET /home` - Home page
- `GET /signup` - Sign up page
- `GET /resident/login` - Resident login page
- `GET /collector/login` - Collector login page
- `GET /authority/login` - Authority login page
- `GET /resident/dashboard` - Resident dashboard (requires login)
- `GET /collector/dashboard` - Collector dashboard (requires login)
- `GET /authority/dashboard` - Authority dashboard (requires login)
- `GET /logout` - Logout

### Resident Endpoints
- `GET /resident/bins` - View all bins
- `GET /resident/bin/{id}` - View bin details
- `GET /resident/search-bins` - Search nearby bins
- `GET /resident/reports` - View reports
- `POST /resident/bin/{id}/update-status` - Update bin status

### Collector Endpoints
- `GET /collector/routes` - View assigned routes
- `GET /collector/route/{id}` - View route details
- `GET /collector/scan-qr` - QR code scanner
- `GET /collector/bin/{id}/collect` - Collect bin
- `GET /collector/collections` - View collections
- `GET /collector/performance` - View performance
- `POST /collector/route/{id}/start` - Start route
- `POST /collector/route/{id}/complete` - Complete route
- `POST /collector/bin/{id}/collect` - Process collection

### Authority Endpoints
- `GET /authority/bins` - Manage bins
- `GET /authority/dispatch` - Dispatch collectors
- `GET /authority/routes` - Manage routes
- `GET /authority/collectors` - Manage collectors
- `GET /authority/reports` - Generate reports
- `POST /authority/dispatch` - Process dispatch
- `POST /authority/bin/{id}/alert` - Set bin alert
- `POST /authority/bin/{id}/clear-alert` - Clear bin alert
- `POST /authority/reports/collection` - Generate collection report
- `POST /authority/reports/performance` - Generate performance report
- `POST /authority/reports/bin-status` - Generate bin status report
- `POST /authority/reports/overdue` - Generate overdue bins report

### Form Submissions (POST)
- `POST /signup` - User registration
- `POST /resident/login` - Resident login
- `POST /collector/login` - Collector login
- `POST /authority/login` - Authority login

## Troubleshooting

### Database Connection Issues
- **Password Authentication Failed**: 
  - Try: `ALTER USER postgres PASSWORD 'postgres';` in PostgreSQL
  - Or update credentials in `application.properties`
- **Database doesn't exist**: Run `CREATE DATABASE smartbin_db;`
- **PostgreSQL not running**: Start PostgreSQL service
- **Port 5432 not accessible**: Check PostgreSQL configuration

### Application Startup Issues
- **JAVA_HOME not set**: Set `JAVA_HOME` to your Java installation directory
- **Port 8081 in use**: Change `server.port` in `application.properties`
- **Maven not found**: Use Maven Wrapper: `.\mvnw.cmd` (Windows) or `./mvnw` (Linux/Mac)

### Common Error Solutions
1. **"password authentication failed for user postgres"**:
   ```sql
   ALTER USER postgres PASSWORD 'postgres';
   ```

2. **"JAVA_HOME environment variable is not defined correctly"**:
   ```bash
   # Windows
   set JAVA_HOME=C:\Program Files\Java\jdk-24
   
   # Linux/Mac
   export JAVA_HOME=/usr/lib/jvm/java-24-openjdk
   ```

3. **"Ambiguous mapping" error**: This was fixed by removing duplicate controller mappings

### Port Already in Use
- If port 8081 is already in use, change it in `application.properties`:
  ```properties
  server.port=8082
  ```

### Maven Build Errors
- Clean Maven cache: `mvn clean`
- Update dependencies: `mvn dependency:resolve`

### Reset Sample Data
If you want to reload sample data:
1. Drop the database:
   ```sql
   DROP DATABASE smartbin_db;
   ```
2. Recreate the database:
   ```sql
   CREATE DATABASE smartbin_db;
   ```
3. Restart the application - sample data will be automatically inserted

## Development Notes

- The application uses **BCrypt** for password hashing
- **Spring Security** is configured to allow public access to home, signup, and login pages
- **Hibernate** is set to `update` mode, which automatically creates/updates tables
- **Session management** is used to track logged-in users

## Future Enhancements

- Email verification for new accounts
- Password reset functionality
- Role-based access control for dashboard features
- Real-time notifications
- Analytics and reporting modules
- Mobile responsive improvements

## Support

For issues or questions, please contact the development team.

## License

This project is developed for educational purposes.

---

**Version**: 0.0.1-SNAPSHOT  
**Last Updated**: October 2025

