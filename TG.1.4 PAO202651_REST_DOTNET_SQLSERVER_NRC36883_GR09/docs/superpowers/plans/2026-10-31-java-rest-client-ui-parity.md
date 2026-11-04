# Java REST Client UI Parity Implementation Plan

**Goal:** Make the four .NET client interfaces follow the corresponding Java REST client screens and flows while keeping the .NET REST API integration.

**Architecture:** Keep each client's existing UI framework and data service. Port the Java REST reference's shared visual language, screen structure, labels, table columns, and operation flow into the web, WinForms, console, and MAUI clients.

**Tech Stack:** ASP.NET Core static web UI, WinForms, .NET console, .NET MAUI, Java REST reference.

## Tasks

1. **Web client:** Compare login, dashboard tabs, filters, account/movement tables, and create-movement dialog against Java REST; adjust markup, styles, and interactions while preserving `/api` calls.
2. **WinForms client:** Align login, navigation sidebar, account and movement panels, table columns/order, and operation dialog with Java REST colors, spacing, and labels.
3. **MAUI client:** Align login, account list, account-scoped movement screen, app bars, colors, and movement form with the Java Compose screens.
4. **Console client:** Align banner, login prompts, menu numbering, table headings, colors, and newest-first movement ordering with the existing client flow.
5. **Verification:** Build the web, WinForms, console, and API solution; inspect the web routes/assets and API data endpoints; report the MAUI workload limitation if its platform workloads are unavailable.

## Reference values

- Primary navy: `#0B1F3A`.
- Secondary navy: `#173F66`.
- Main background: `#F4F7FB`.
- Account columns: `Cuenta`, `Cliente`, `Moneda`, `Estado`, `Saldo`.
- Movement columns: `Cuenta`, `Nro`, `Fecha`, `Tipo`, `Importe`, `Saldo`.
- Mobile movements are scoped to the selected account and support deposit, withdrawal, and transfer.
- Movement lists place the newest movement first.
