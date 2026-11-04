#nullable enable
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using CLIESC_EuBank_SOAP_DOTNET_GR09.Models;

namespace CLIESC_EuBank_SOAP_DOTNET_GR09.Services;

public sealed class EuBankApiService
{
    public string? LastError { get; private set; }

    public EuBankApiService(HttpClient? httpClient = null) { }

    public async Task<bool> LoginAsync(LoginRequest request)
    {
        try
        {
            var response = await SoapTransport.CallAsync("LoginService.svc", SoapTransport.LoginNamespace,
                "ILoginService", "Login", SoapTransport.Argument(SoapTransport.LoginNamespace, "usuario", request.usuario),
                SoapTransport.Argument(SoapTransport.LoginNamespace, "clave", request.clave));
            return bool.TryParse(SoapTransport.Result(response, "LoginResult").Value, out var success) && success;
        }
        catch (Exception ex) { LastError = ex.Message; return false; }
    }

    public async Task<List<Account>> GetAllAccountsAsync()
    {
        try
        {
            var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
                "IMovimientosService", "GetDatosCuentas");
            return SoapTransport.Items(response, "GetDatosCuentasResult", "CuentaData").Select(item => new Account
            {
                codigo = SoapTransport.Text(item, "Codigo"), nombreCliente = SoapTransport.Text(item, "NombreCliente"),
                emailCliente = SoapTransport.Text(item, "EmailCliente"), telefonoCliente = SoapTransport.Text(item, "TelefonoCliente"),
                moneda = SoapTransport.Text(item, "Moneda"), saldo = SoapTransport.Decimal(item, "Saldo"), estado = SoapTransport.Text(item, "Estado")
            }).ToList();
        }
        catch (Exception ex) { LastError = ex.Message; return []; }
    }

    public async Task<Account?> GetAccountDetailsAsync(string accountCode)
    {
        try
        {
            var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
                "IMovimientosService", "GetDatosCuenta", SoapTransport.Argument(SoapTransport.MovementsNamespace, "cuenta", accountCode));
            var result = SoapTransport.Result(response, "GetDatosCuentaResult").Elements().FirstOrDefault();
            return result is null ? null : new Account
            {
                codigo = SoapTransport.Text(result, "Codigo"), nombreCliente = SoapTransport.Text(result, "NombreCliente"),
                emailCliente = SoapTransport.Text(result, "EmailCliente"), telefonoCliente = SoapTransport.Text(result, "TelefonoCliente"),
                moneda = SoapTransport.Text(result, "Moneda"), saldo = SoapTransport.Decimal(result, "Saldo"), estado = SoapTransport.Text(result, "Estado")
            };
        }
        catch (Exception ex) { LastError = ex.Message; return null; }
    }

    public async Task<List<Movement>> GetMovementsAsync(string? accountCode = null)
    {
        try
        {
            var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
                "IMovimientosService", "GetMovimientos", SoapTransport.Argument(SoapTransport.MovementsNamespace, "cuenta", accountCode ?? ""));
            return SoapTransport.Items(response, "GetMovimientosResult", "MovimientoData").Select(item => new Movement
            {
                numero = int.TryParse(SoapTransport.Text(item, "Numero"), out var number) ? number : 0,
                ordenCreacion = long.TryParse(SoapTransport.Text(item, "OrdenCreacion"), out var order) ? order : 0,
                fecha = DateTime.Parse(SoapTransport.Text(item, "Fecha"), CultureInfo.InvariantCulture),
                tipo = SoapTransport.Text(item, "Tipo"), importe = SoapTransport.Decimal(item, "Importe"),
                referencia = SoapTransport.Text(item, "Referencia"), codigoCuenta = SoapTransport.Text(item, "CodigoCuenta"),
                saldoActual = SoapTransport.Decimal(item, "SaldoActual"), nombreCliente = SoapTransport.Text(item, "NombreCliente")
            }).ToList();
        }
        catch (Exception ex) { LastError = ex.Message; return []; }
    }

    public async Task<bool> ProcessMovementAsync(MovementRequest request)
    {
        try
        {
            LastError = null;
            var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
                "IMovimientosService", "ProcesarMovimiento",
                SoapTransport.Argument(SoapTransport.MovementsNamespace, "tipo", request.tipo),
                SoapTransport.Argument(SoapTransport.MovementsNamespace, "cuentaOrigen", request.cuentaOrigen),
                SoapTransport.Argument(SoapTransport.MovementsNamespace, "cuentaDestino", request.cuentaDestino ?? ""),
                SoapTransport.Argument(SoapTransport.MovementsNamespace, "importe", request.importe.ToString(CultureInfo.InvariantCulture)));
            var result = SoapTransport.Result(response, "ProcesarMovimientoResult").Elements().FirstOrDefault();
            if (result is null) { LastError = "El servicio no devolvió el resultado de la operación."; return false; }
            var success = SoapTransport.Text(result, "Codigo") == "1";
            if (!success) LastError = SoapTransport.Text(result, "Mensaje");
            return success;
        }
        catch (Exception ex) { LastError = ex.Message; return false; }
    }

    public async Task<List<string>> GetMovementTypesAsync()
    {
        try
        {
            var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
                "IMovimientosService", "GetTiposMovimiento");
            return SoapTransport.Items(response, "GetTiposMovimientoResult", "string").Select(item => item.Value).ToList();
        }
        catch (Exception ex) { LastError = ex.Message; return []; }
    }
}
