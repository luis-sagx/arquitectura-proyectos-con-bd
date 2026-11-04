using WS_EuBank_SOAPDOTNET_GR09.Contracts;

namespace WS_EuBank_SOAPDOTNET_GR09.Services;

public interface IMovimientosService
{
    Task<List<CuentaData>> GetDatosCuentasAsync(CancellationToken cancellationToken = default);
    Task<CuentaData?> GetDatosCuentaAsync(string cuenta, CancellationToken cancellationToken = default);
    Task<List<MovimientoData>> GetMovimientosAsync(string? cuenta, CancellationToken cancellationToken = default);
    Task<List<string>> GetTiposMovimientoAsync(CancellationToken cancellationToken = default);
    Task<ResultadoOperacion> ProcesarMovimientoAsync(string tipo, string cuentaOrigen, string? cuentaDestino, decimal importe, CancellationToken cancellationToken = default);
}
