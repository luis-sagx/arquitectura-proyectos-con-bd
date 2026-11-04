namespace WS_EuBank_SOAPDOTNET_GR09.Services;

public interface ILoginService
{
    Task<bool> LoginAsync(string usuario, string clave, CancellationToken cancellationToken = default);
}
