using System.Net.Http.Json;
using System.Text.Json;
using System.Net.Http;
using System.Threading.Tasks;
using CLIESC_EuBank_REST_DOTNET_GR09.Models;
using System.Collections.Generic;
using System;

namespace CLIESC_EuBank_REST_DOTNET_GR09.Services
{
    public class EuBankApiService
    {
        private readonly HttpClient _httpClient;
        private readonly string _baseUrl = "http://localhost:9090/api"; // Adjust the base URL as needed
        public string LastError { get; private set; }

        public EuBankApiService(HttpClient httpClient)
        {
            _httpClient = httpClient;
        }

        public async Task<bool> LoginAsync(LoginRequest request)
        {
            try
            {
                var response = await _httpClient.PostAsJsonAsync($"{_baseUrl}/login", request);
                if (response.IsSuccessStatusCode)
                {
                    var result = await response.Content.ReadFromJsonAsync<LoginResponse>();
                    return result != null && result.success;
                }
                return false;
            }
            catch
            {
                return false;
            }
        }

        public async Task<List<Account>> GetAllAccountsAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync($"{_baseUrl}/cuentas");
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadFromJsonAsync<List<Account>>();
                }
                return null;
            }
            catch
            {
                return null;
            }
        }

        public async Task<Account> GetAccountDetailsAsync(string accountCode)
        {
            try
            {
                var response = await _httpClient.GetAsync($"{_baseUrl}/cuentas/{accountCode}");
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadFromJsonAsync<Account>();
                }
                return null;
            }
            catch
            {
                return null;
            }
        }

        public async Task<List<Movement>> GetMovementsAsync(string accountCode = null)
        {
            try
            {
                var url = $"{_baseUrl}/movimientos";
                if (!string.IsNullOrEmpty(accountCode))
                {
                    url += $"?cuenta={accountCode}";
                }
                var response = await _httpClient.GetAsync(url);
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadFromJsonAsync<List<Movement>>();
                }
                return null;
            }
            catch
            {
                return null;
            }
        }

        public async Task<bool> ProcessMovementAsync(MovementRequest request)
        {
            try
            {
                LastError = null;
                Console.WriteLine($"[PROCESS] POST {_baseUrl}/movimientos");
                Console.WriteLine($"[PROCESS] Request: {JsonSerializer.Serialize(request)}");
                var response = await _httpClient.PostAsJsonAsync($"{_baseUrl}/movimientos", request);
                Console.WriteLine($"[PROCESS] Status: {response.StatusCode}");
                if (response.IsSuccessStatusCode)
                {
                    var content = await response.Content.ReadAsStringAsync();
                    Console.WriteLine($"[PROCESS] Response: {content}");
                }
                else
                {
                    var error = await response.Content.ReadAsStringAsync();
                    Console.WriteLine($"[PROCESS] Error Response: {error}");
                    try
                    {
                        using (var document = JsonDocument.Parse(error))
                        {
                            if (document.RootElement.TryGetProperty("message", out var message))
                                LastError = message.GetString();
                        }
                    }
                    catch (JsonException) { }
                    if (string.IsNullOrWhiteSpace(LastError)) LastError = error;
                }
                return response.IsSuccessStatusCode;
            }
            catch (Exception ex)
            {
                LastError = ex.Message;
                Console.WriteLine($"[PROCESS] Error: {ex.Message}");
                return false;
            }
        }

        public async Task<List<string>> GetMovementTypesAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync($"{_baseUrl}/tiposmovimiento");
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadFromJsonAsync<List<string>>();
                }
                return null;
            }
            catch
            {
                return null;
            }
        }
    }
}
