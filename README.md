# Campus Lost and Found Management System

A simple web application for reporting and recovering belongings lost or found on a campus.
Built with **Java 17, Spring Boot, Spring JDBC (JdbcTemplate), Thymeleaf (HTML + CSS) and PostgreSQL**.

## Features
- Report a lost item and report a found item
- Browse and search lost and found items
- Submit a claim for an available found item (claims start as `PENDING`)
- Admin page to approve or reject claims
  - Approve: claim becomes `APPROVED`, the found item becomes `CLAIMED`, other pending claims for it become `REJECTED`
  - Reject: claim becomes `REJECTED`
- No login, sign-up or authentication (by design)

## Database (exactly three tables)
| Table | Purpose | Status values |
|-------|---------|---------------|
| `lost_items`  | Items reported as lost  | `REPORTED` |
| `found_items` | Items reported as found | `AVAILABLE`, `CLAIMED` |
| `claims`      | Claims on found items (FK `found_item_id`) | `PENDING`, `APPROVED`, `REJECTED` |

The tables are created automatically on startup from `src/main/resources/schema.sql` (same as `database/schema.sql`).

## Requirements
- JDK 17 or newer
- Maven 3.8+
- PostgreSQL 13+

## Run it
1. Create the database:
   ```sql
   CREATE DATABASE lost_found_db;
   ```
   (or `createdb -U postgres lost_found_db`)
2. Set your PostgreSQL password in `src/main/resources/application.properties`
   (`spring.datasource.password`) or set the environment variables
   `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
3. Start the app:
   ```bash
   mvn spring-boot:run
   ```
4. Open <http://localhost:8080>

Optional demo data: `psql -U postgres -d lost_found_db -f database/sample-data.sql`

## Pages
| URL | What it does |
|-----|--------------|
| `/` | Home |
| `/lost`, `/lost/new` | Browse / report lost items |
| `/found`, `/found/new` | Browse / report found items |
| `/claim?itemId=..` | Claim form for a found item |
| `/admin` | Review claims (approve / reject) |

## Project structure
```
src/main/java/com/campus/lostfound/
  LostFoundApplication.java        starts the app
  controller/PageController.java   all web pages and form handlers
  repository/ItemRepository.java   all SQL (JdbcTemplate)
  model/Item.java, Claim.java      simple data classes
src/main/resources/
  application.properties           database settings
  schema.sql                       the three tables
  templates/                       Thymeleaf HTML pages
  static/style.css                 the only stylesheet
database/                          schema.sql and sample-data.sql
```

## Note
Because no authentication was requested, the `/admin` page is open to anyone who can reach the app.
Run it on a trusted/local network, or add authentication before using it in production.
