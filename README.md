\# SchoolTrack – Student Management System



A web-based Student Management System built with Java 21, Spring Boot, Spring Security, Spring Data JPA, MySQL and Thymeleaf.



SchoolTrack provides a centralized application for managing school operations, including students, parents, attendance, buses, routes, users, roles, permissions and multiple school tenants.



\## Features



\### School Management

\- Student management

\- Parent management

\- Class and section management

\- Academic year management

\- Blood group information



\### Transport Management

\- Bus management

\- Route management

\- Bus capacity validation



\### Attendance

\- Attendance management

\- Bulk attendance operations

\- Attendance dashboard



\### Administration and Security

\- Secure login with Spring Security

\- Role management

\- Permission management

\- User management

\- Super Admin functionality

\- Multi-tenant school management

\- Tenant selection and tenant context handling



\## Technology Stack



| Technology | Purpose |

|---|---|

| Java 21 | Programming language |

| Spring Boot 4.1.2 | Backend framework |

| Spring MVC | Web application |

| Spring Security | Authentication and authorization |

| Spring Data JPA | Persistence |

| Hibernate | ORM |

| MySQL | Relational database |

| Thymeleaf | Server-side templates |

| HTML, CSS, JavaScript | Frontend |

| Maven | Build and dependency management |



\## Application Screenshots



Screenshots will be added here after the final UI review.



Suggested screenshots:

\- Login page

\- Main dashboard

\- Student management

\- Parent management

\- Attendance management

\- Bus and route management

\- Role and permission management

\- Tenant management



\## Project Structure



```text

mgt/

├── src/

│   ├── main/

│   │   ├── java/student/mgt/

│   │   │   ├── config/

│   │   │   ├── controller/

│   │   │   ├── dao/

│   │   │   ├── daoImpl/

│   │   │   ├── dto/

│   │   │   ├── entity/

│   │   │   ├── exception/

│   │   │   ├── mapper/

│   │   │   ├── service/

│   │   │   ├── serviceimpl/

│   │   │   ├── util/

│   │   │   └── validator/

│   │   └── resources/

│   │       ├── static/

│   │       ├── templates/

│   │       ├── application.properties

│   │       └── application-prod.properties

│   └── test/

├── .gitignore

├── mvnw

├── mvnw.cmd

├── pom.xml

└── README.md

```



\## Prerequisites



Install the following software:



\- Java Development Kit (JDK) 21

\- MySQL 8 or a compatible MySQL server

\- Git



The project includes Maven Wrapper scripts, so a separate Maven installation is not required.



\## Run Locally



\### 1. Clone the repository



```bash

git clone https://github.com/AniketShendge0722/student.git

cd student

```



\### 2. Create the MySQL database



Open MySQL and execute:



```sql

CREATE DATABASE student;

```



\### 3. Configure local database credentials



Create this local configuration file:



`src/main/resources/application-local.properties`



Example:



```properties

spring.datasource.url=jdbc:mysql://localhost:3306/student?createDatabaseIfNotExist=true\&useSSL=false\&serverTimezone=Asia/Kolkata

spring.datasource.username=root

spring.datasource.password=YOUR\_LOCAL\_DB\_PASSWORD

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver



spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true



spring.thymeleaf.cache=false



app.security.admin.username=admin

app.security.admin.password=YOUR\_INITIAL\_ADMIN\_PASSWORD

```



Replace the example values with your own local credentials.



\*\*Security:\*\* Never commit `application-local.properties` or share your database password.



\### 4. Build and test



Windows PowerShell:



```powershell

.\\mvnw.cmd clean test

```



\### 5. Start the application



```powershell

.\\mvnw.cmd spring-boot:run

```



Open:



http://localhost:8080



The default Spring profile is `local`, as configured in `application.properties`.



\## Production Configuration



The production profile is `prod`.



The following environment variables must be configured by the hosting provider:



| Variable | Description |

|---|---|

| `SPRING\_PROFILES\_ACTIVE` | Set to `prod` |

| `DB\_URL` | Production JDBC database URL |

| `DB\_USERNAME` | Production database username |

| `DB\_PASSWORD` | Production database password |

| `APP\_ADMIN\_USERNAME` | Initial admin username configuration |

| `APP\_ADMIN\_PASSWORD` | Initial admin password configuration |



Example JDBC URL:



```text

jdbc:mysql://YOUR\_DATABASE\_HOST:3306/YOUR\_DATABASE\_NAME?sslMode=REQUIRED

```



Use the connection details supplied by your database provider.



Never put production credentials in source code, README files, Git commits or screenshots.



The application currently uses Hibernate schema updates. Review and establish a controlled database migration strategy before relying on this configuration for a production system.



Existing administrator accounts must be preserved. Do not assume changing the initial admin environment variables will reset an existing account.



\## Build a JAR



```powershell

.\\mvnw.cmd clean package

```



The packaged application will be generated in:



```text

target/

```



\## Deployment



Cloud deployment instructions and the verified live application URL will be added after selecting and testing a suitable hosting and database provider.



The deployment must provide:

\- Java 21 runtime support

\- A persistent MySQL-compatible database

\- Secure environment variables

\- HTTPS

\- Database backup and recovery options



Free hosting services may have resource limits, idle sleep, usage credits or other restrictions.



\## Security Notes



\- Local configuration files must not be committed.

\- Production secrets must be stored as hosting environment variables.

\- Do not commit database backups or private keys.

\- Use strong administrator passwords.

\- Restrict production database access.

\- Back up production data regularly.

\- Review authorization and tenant isolation before public production use.



\## Author



\*\*GitHub:\*\* \[AniketShendge0722](https://github.com/AniketShendge0722)



\## Repository



\[SchoolTrack – Student Management System](https://github.com/AniketShendge0722/student)



\---



SchoolTrack is an ongoing project. Features, documentation and deployment instructions will be updated as development progresses.
