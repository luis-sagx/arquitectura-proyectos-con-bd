using WS_EuBank_SOAPDOTNET_GR09;
using WS_EuBank_SOAPDOTNET_GR09.Services;

var builder = WebApplication.CreateBuilder(args);
var connectionString = Environment.GetEnvironmentVariable("DB_CONNECTION")
    ?? builder.Configuration.GetConnectionString("EurekaBank")
    ?? "Server=localhost;Port=3306;Database=eurekabank;User ID=root;SslMode=None;Allow User Variables=True";

builder.Services.AddSingleton(new BankRepository(connectionString));
builder.Services.AddScoped<LoginService>();
builder.Services.AddScoped<MovimientosService>();
builder.Services.AddCors(options => options.AddDefaultPolicy(policy =>
    policy.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));

var app = builder.Build();
app.UseCors();
app.MapSoapServices();
app.MapGet("/", () => Results.Text("EuBank SOAP .NET está disponible. WSDL: /Services/LoginService.svc?wsdl", "text/plain"));
app.Run();

public partial class Program;
