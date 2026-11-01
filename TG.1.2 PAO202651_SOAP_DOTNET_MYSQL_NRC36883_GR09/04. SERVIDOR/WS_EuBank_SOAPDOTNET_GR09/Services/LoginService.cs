namespace WS_EuBank_SOAPDOTNET_GR09.Services;

public sealed class LoginService(BankRepository repository) : ILoginService
{
    public Task<bool> LoginAsync(string usuario, string clave, CancellationToken cancellationToken = default) =>
        repository.LoginAsync(usuario, clave, cancellationToken);
}
