using WS_EuBank_SOAPDOTNET_GR09.Contracts;

namespace WS_EuBank_SOAPDOTNET_GR09.Services;

public sealed class MovimientosService(BankRepository repository) : IMovimientosService
{
    public Task<List<CuentaData>> GetDatosCuentasAsync(CancellationToken cancellationToken = default) =>
        repository.GetAccountsAsync(cancellationToken);

    public Task<CuentaData?> GetDatosCuentaAsync(string cuenta, CancellationToken cancellationToken = default) =>
        repository.GetAccountAsync(cuenta, cancellationToken);

    public Task<List<MovimientoData>> GetMovimientosAsync(string? cuenta, CancellationToken cancellationToken = default) =>
        repository.GetMovementsAsync(cuenta, cancellationToken);

    public Task<List<string>> GetTiposMovimientoAsync(CancellationToken cancellationToken = default) =>
        Task.FromResult(new List<string> { "DEPOSITO", "RETIRO", "TRANSFERENCIA" });

    public async Task<ResultadoOperacion> ProcesarMovimientoAsync(string tipo, string cuentaOrigen, string? cuentaDestino, decimal importe, CancellationToken cancellationToken = default)
    {
        var error = await repository.RegisterMovementAsync(tipo, cuentaOrigen, cuentaDestino, importe, cancellationToken);
        return error is null
            ? new ResultadoOperacion { Codigo = 1, Mensaje = "Movimiento registrado exitosamente." }
            : new ResultadoOperacion { Codigo = -1, Mensaje = error };
    }
}
