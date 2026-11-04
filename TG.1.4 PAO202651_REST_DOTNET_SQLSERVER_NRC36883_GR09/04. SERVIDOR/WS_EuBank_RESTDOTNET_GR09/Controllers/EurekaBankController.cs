using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api")]
public sealed class EurekaBankController(BankRepository repository) : ControllerBase
{
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest request) => Ok(new { success = await repository.Login(request.Usuario, request.Clave) });
    [HttpGet("cuentas")]
    public async Task<IActionResult> Accounts() => Ok(await repository.Accounts());
    [HttpGet("cuentas/{codigo}")]
    public async Task<IActionResult> Account(string codigo) { var row=(await repository.Accounts(codigo)).FirstOrDefault();return row is null?NotFound():Ok(row); }
    [HttpGet("movimientos")]
    public async Task<IActionResult> Movements([FromQuery]string? cuenta=null) => Ok(await repository.Movements(cuenta));
    [HttpGet("tiposmovimiento")]
    public IActionResult Types() => Ok(new[]{"DEPOSITO","RETIRO","TRANSFERENCIA"});
    [HttpPost("movimientos")]
    public async Task<IActionResult> Register([FromBody]MovementRequest request) { var error=await repository.Register(request);return error is null?Ok(new{success=true,message="Movimiento registrado"}):BadRequest(new{success=false,message=error}); }
}
public record LoginRequest(string Usuario,string Clave);
