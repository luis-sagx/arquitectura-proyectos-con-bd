# EurekaBank REST Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a parallel REST version of EurekaBank in `TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09/`, preserving the SOAP project's four client interfaces and behavior.

**Architecture:** Jakarta EE JAX-RS WAR provides JSON endpoints and reuses the JDBC/service/repository layering. Separate Spring Boot web, Swing, console, and Android clients call that API over HTTP. UI source is derived from SOAP; REST reference supplies API and client-integration conventions.

**Tech Stack:** Java 21 / Jakarta EE 10 / JAX-RS / Maven, Spring Boot / Thymeleaf, Swing, Android Kotlin / Jetpack Compose, MySQL 8.4, JSON over HTTP.

**Spec:** `docs/superpowers/specs/2026-10-30-eurekabank-rest-design.md`

## Global Constraints

- New project root: `TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09/`.
- Module directories: `03. DBB/mysql/init/`, `04. SERVIDOR/WS_EurekaBank_RESTJAVA_GR09/`, `04. CLIWEB/CLIWEB_EuBank_RESTJAVA_GR09/`, `04. CLIESC/CLIESC_EuBank_RESTJAVA_GR09/`, `04. CLICON/CLICON_EuBank_RESTJAVA_GR09/`, `04. CLIMOV/CLIMOV_EurekaBank_RESTJAVA_GR09/`, `05. DOCUMENTACION/`.
- Keep Java package root `ec.edu.monster` and preserve SOAP project files.
- Keep the SOAP user-facing screens, navigation, labels, assets and banking features.
- REST resource base path `/api/eurekabank`; JSON endpoints: `POST /login`, `GET /cuentas`, `GET /movimientos?cuenta=...`, `POST /movimientos`, `GET /tipos`.
- Keep transaction persistence atomic for transfers; use the existing MySQL schema and seed data.
- Centralize configurable API/database URLs; do not retain a hardcoded LAN address from reference.
- Do not log passwords or SOAP/REST request payloads containing credentials.

## Review Focus

- Login rejects missing user/password and returns the agreed JSON result: cover in server endpoint review.
- Zero, negative, nonnumeric, and missing movement amounts must not write ledger rows: cover in server validation review.
- Transfers to the same account, inactive/missing accounts, and insufficient balance must roll back both ledger sides: cover in repository/transaction review.
- Empty account filter returns all movements only where the existing dashboard requires it; filtered queries must encode the account safely: cover in web/client contract review.
- API base URLs must be configurable and Android emulator defaults must point to the host correctly: cover in each client's configuration review.

---

### Task 1: Create the parallel project skeleton and database setup

**Files:**
- Create: `TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09/03. DBB/mysql/init/01-eurekabank-schema.sql`
- Create: `TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09/03. DBB/mysql/init/02-eurekabank-data.sql`
- Create: `TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09/docker-compose.mysql.yml`
- Create: each agreed module's `pom.xml`, Gradle wrapper/configuration, and source/resource roots based on the matching SOAP module and REST reference.

**Interfaces:**
- Produces the standalone project directory and local MySQL initialization matching the existing schema.

- [x] Create the requested directory hierarchy and copy only source/config/assets needed from SOAP; exclude `target/`, generated code, local secrets, and `local.properties`.
- [x] Copy the current MySQL schema and dataset and adapt Compose paths, container naming, and env-file defaults for the new root.
- [x] Set artifact/application names to the approved REST module names while retaining `ec.edu.monster` packages.
- [x] Inspect the resulting tree and verify SOAP working-tree files remain unchanged.

### Task 2: Implement REST server and JSON contract

**Files:**
- Create: `04. SERVIDOR/WS_EurekaBank_RESTJAVA_GR09/pom.xml`
- Create: `src/main/java/ec/edu/monster/wsrest/EurekaBank.java`
- Create/adapt: `src/main/java/ec/edu/monster/{db,dao,service,model}/**`
- Create/adapt: `src/main/webapp/WEB-INF/{web.xml,beans.xml}` and `src/main/resources/META-INF/persistence.xml`

**Interfaces:**
- Consumes: MySQL schema and seed data from Task 1.
- Produces: JSON resource `/api/eurekabank` with login, accounts, movements, and types methods matching the design contract.

- [x] Port connection configuration, models, login repository, account/movement queries and transactional movement logic from SOAP into the REST module.
- [x] Implement JAX-RS methods and HTTP status mapping for `POST /login`, `GET /cuentas`, `GET /movimientos`, `POST /movimientos`, and `GET /tipos`.
- [x] Keep JSON property names compatible with all four clients; make input validation and transfer errors return structured `ResultadoOperacion` data.
- [x] Verify the WAR compiles with the configured JDK/Maven command and review route/model consistency.

### Task 3: Adapt the web client

**Files:**
- Create: `04. CLIWEB/CLIWEB_EuBank_RESTJAVA_GR09/pom.xml`
- Create/adapt: `src/main/java/ec/edu/monster/{controladores,servicios,model}/**`
- Create/adapt: `src/main/resources/{application.properties,templates,static}/**`

**Interfaces:**
- Consumes: Task 2 REST JSON endpoints through `EuBankService`.
- Produces: same login and dashboard screens/routes as SOAP web client.

- [x] Copy the SOAP web templates, assets, controllers, and application bootstrap preserving visible behavior.
- [x] Replace generated SOAP proxy calls with configurable HTTP JSON calls to the REST endpoints.
- [x] Preserve login, account list, movement filters, movement type list, and deposit/withdraw/transfer flows.
- [x] Build the web module and inspect route/payload compatibility against Task 2.

### Task 4: Adapt desktop and console clients

**Files:**
- Create/adapt all Java source, resources, and Maven configuration under `04. CLIESC/CLIESC_EuBank_RESTJAVA_GR09/`.
- Create/adapt all Java source, resources, and Maven configuration under `04. CLICON/CLICON_EuBank_RESTJAVA_GR09/`.

**Interfaces:**
- Consumes: Task 2 JSON endpoints via each client's `ec.edu.monster.servicios.EuBankService`.
- Produces: existing Swing views and console login/dashboard flows with the same supported operations.

- [x] Preserve Swing forms, view classes, images, and controller flow; replace SOAP service invocation with HTTP/JSON.
- [x] Preserve console prompts, menu actions, and output; replace SOAP service invocation with HTTP/JSON.
- [x] Make each API URL configurable without changing folder names or package roots.
- [x] Build both Maven modules and inspect the calls and DTO mappings.

### Task 5: Adapt Android client

**Files:**
- Create: `04. CLIMOV/CLIMOV_EurekaBank_RESTJAVA_GR09/` Android Gradle project.
- Create/adapt: `app/src/main/java/ec/edu/monster/**`, manifest, resources, and Gradle configuration.

**Interfaces:**
- Consumes: Task 2 REST JSON endpoints through the Android service layer.
- Produces: same Compose login, accounts, movements, and new-movement screens/navigation as SOAP Android client.

- [x] Copy Android UI/navigation/resources from SOAP, excluding machine-local files and generated/build outputs.
- [x] Replace SOAP/kSOAP networking with HTTP/JSON integration; configure API URL for emulator and override builds.
- [x] Preserve callback/thread behavior and present existing success/error states; ensure credentials and full payloads are not logged.
- [ ] Assemble the debug APK and inspect the resulting APK. Blocked locally: Android SDK is not installed or configured.

### Task 6: Documentation and end-to-end review

**Files:**
- Create: project-root `README.md`
- Create: `05. DOCUMENTACION/DOCKER-MYSQL.md`
- Create: `05. DOCUMENTACION/API-REST.md`
- Review: all modules created above.

**Interfaces:**
- Documents setup, API contract, module launch order, and configurable URLs for all clients.

- [x] Document database startup, WAR deployment, API paths/JSON payloads, client startup, and local URL overrides.
- [x] Review the five modules together for route/model compatibility, package names, and preserved client features.
- [x] Review Git diff/status to confirm no changes landed in the SOAP project and no generated secrets/build outputs were included.
- [x] Report build commands run, outcomes, and any environment-limited verification.
