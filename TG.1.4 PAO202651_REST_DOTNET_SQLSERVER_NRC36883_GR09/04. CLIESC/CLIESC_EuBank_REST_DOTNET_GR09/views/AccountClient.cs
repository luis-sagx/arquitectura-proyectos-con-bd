using System;
using System.Windows.Forms;
using System.Net.Http;
using CLIESC_EuBank_REST_DOTNET_GR09.Services;
using System.Collections.Generic;
using CLIESC_EuBank_REST_DOTNET_GR09.Models;

namespace CLIESC_EuBank_REST_DOTNET_GR09.views
{
    public partial class AccountClient : Form
    {
        private readonly EuBankApiService _apiService;

        public AccountClient()
        {
            InitializeComponent();
            // Inicializar el servicio de API
            var httpClient = new HttpClient();
            _apiService = new EuBankApiService(httpClient);
            
            ldPanelRound2.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
            GW_Clients.Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
            GW_Clients.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            GW_Clients.RowsDefaultCellStyle.BackColor = System.Drawing.Color.White;
            GW_Clients.AlternatingRowsDefaultCellStyle.BackColor = System.Drawing.Color.FromArgb(244, 247, 251);
            // Cargar datos al iniciar el formulario
            this.Load += AccountClient_Load;
        }

        private async void AccountClient_Load(object sender, EventArgs e)
        {
            await CargarCuentas();
        }

        private async System.Threading.Tasks.Task CargarCuentas()
        {
            try
            {
                // Mostrar indicador de carga
                GW_Clients.DataSource = null;
                GW_Clients.Rows.Clear();

                var cuentas = await _apiService.GetAllAccountsAsync();

                if (cuentas != null && cuentas.Count > 0)
                {
                    // Configurar el DataGridView con los datos
                    var dataTable = new System.Data.DataTable();
                    dataTable.Columns.Add("Cuenta", typeof(string));
                    dataTable.Columns.Add("Cliente", typeof(string));
                    dataTable.Columns.Add("Moneda", typeof(string));
                    dataTable.Columns.Add("Estado", typeof(string));
                    dataTable.Columns.Add("Saldo", typeof(decimal));

                    foreach (var cuenta in cuentas)
                    {
                        dataTable.Rows.Add(
                            cuenta.codigo,
                            cuenta.nombreCliente,
                            cuenta.moneda,
                            cuenta.estado,
                            cuenta.saldo
                        );
                    }

                    GW_Clients.DataSource = dataTable;

                    // Ajustar el formato de las columnas
                    if (GW_Clients.Columns["Saldo"] != null)
                    {
                        GW_Clients.Columns["Saldo"].DefaultCellStyle.Format = "N2";
                        GW_Clients.Columns["Saldo"].DefaultCellStyle.FormatProvider = System.Globalization.CultureInfo.InvariantCulture;
                        GW_Clients.Columns["Saldo"].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleRight;
                    }
                }
                else
                {
                    MessageBox.Show("No se encontraron cuentas", "Información", 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al cargar las cuentas:\n{ex.Message}", 
                    "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void GW_Clients_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            // En el cliente Java, seleccionar una cuenta no abre otra pantalla.
        }

        private async void MostrarDetallesCuenta(string codigoCuenta)
        {
            try
            {
                var cuenta = await _apiService.GetAccountDetailsAsync(codigoCuenta);
                if (cuenta != null)
                {
                    string mensaje = $"Detalles de la Cuenta:\n\n" +
                                   $"Código: {cuenta.codigo}\n" +
                                   $"Cliente: {cuenta.nombreCliente}\n" +
                                   $"Email: {cuenta.emailCliente}\n" +
                                   $"Teléfono: {cuenta.telefonoCliente}\n" +
                                   $"Moneda: {cuenta.moneda}\n" +
                                   $"Saldo: {cuenta.saldo:N2}\n" +
                                   $"Estado: {cuenta.estado}";

                    MessageBox.Show(mensaje, "Información de la Cuenta", 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al obtener detalles:\n{ex.Message}", 
                    "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
    }
}
