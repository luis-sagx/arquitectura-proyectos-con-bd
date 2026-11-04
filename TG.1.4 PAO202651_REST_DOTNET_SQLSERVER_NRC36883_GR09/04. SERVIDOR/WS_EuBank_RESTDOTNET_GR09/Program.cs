using Microsoft.Data.SqlClient;

var builder = WebApplication.CreateBuilder(args);
var connectionString = Environment.GetEnvironmentVariable("DB_CONNECTION")
    ?? builder.Configuration.GetConnectionString("EurekaBank")
    ?? "Server=localhost;Database=EurekaBank;Integrated Security=True;Encrypt=True;TrustServerCertificate=True;MultipleActiveResultSets=True";

builder.Services.AddControllers();
builder.Services.AddSingleton(new BankRepository(connectionString));
builder.Services.AddCors(options => options.AddDefaultPolicy(policy =>
    policy.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));
builder.Services.AddOpenApi();

var app = builder.Build();
if (app.Environment.IsDevelopment()) app.MapOpenApi();
app.UseAuthorization();
app.UseCors();
app.MapControllers();
app.Run();

public partial class Program;
