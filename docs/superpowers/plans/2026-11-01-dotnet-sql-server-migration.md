# .NET SQL Server Migration Implementation Plan

> Inline implementation plan for the requested migration of both .NET projects.

**Goal:** Move the .NET REST and SOAP servers from MySQL to SQL Server while keeping their current endpoints, client behavior, and sample EurekaBank data.

**Architecture:** Replace `MySqlConnector` with Microsoft's `Microsoft.Data.SqlClient`; translate the repositories to SQL Server syntax and transaction locking; provide idempotent SQL Server schema and seed scripts. Use local Windows integrated authentication by default and allow a `DB_CONNECTION` override.

**Tech Stack:** .NET 10, Microsoft.Data.SqlClient, SQL Server, sqlcmd.

**Spec:** User request: both .NET projects must use SQL Server instead of MySQL.

## Global Constraints

- Keep existing REST endpoints and SOAP contracts unchanged.
- Preserve data shapes, account operations, global movement ordering, and transaction atomicity.
- Do not include local credentials in committed files.
- Keep existing project root directory names.

## Review Focus

- Concurrent operations on one account: retain row-level serialization and avoid lost balance updates.
- Movement identity/order: maintain a global increasing ID used by both clients to show newest-first.
- Repeat initialization: SQL scripts must not duplicate seed rows.
- Local authentication: default to the installed Windows SQL Server instance while permitting SQL credentials through `DB_CONNECTION`.
- Amount/date mapping: preserve decimal precision and SQL `date` behavior.

## Tasks

1. Add idempotent SQL Server schema and seed scripts in both .NET projects; preserve all tables and sample rows and make `id_movimiento` an identity column.
2. Replace the REST and SOAP repository providers and translate MySQL-only SQL to SQL Server T-SQL without changing HTTP or SOAP contracts.
3. Replace MySQL connection setup and Docker instructions with SQL Server connection setup and schema initialization instructions.
4. Build both server projects and initialize/query the local SQL Server database when permissions allow.
