using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Net.Http;
using CLIESC_EuBank_SOAP_DOTNET_GR09.Services;
using CLIESC_EuBank_SOAP_DOTNET_GR09.Models;

namespace CLIESC_EuBank_SOAP_DOTNET_GR09.views
{
    public partial class Movements : Form
    {
        private readonly EuBankApiService _apiService;

        public Movements()
        {
            InitializeComponent();
            // Inicializar el servicio de API
            var httpClient = new HttpClient();
            _apiService = new EuBankApiService(httpClient);
            
            ldPanelRound1.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
            GW_Movements.Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
            GW_Movements.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            GW_Movements.RowsDefaultCellStyle.BackColor = Color.White;
            GW_Movements.AlternatingRowsDefaultCellStyle.BackColor = Color.FromArgb(244, 247, 251);
            // Cargar datos al iniciar el formulario
            this.Load += Movements_Load;
        }

        private async void Movements_Load(object sender, EventArgs e)
        {
            await CargarMovimientos();
        }

        private async System.Threading.Tasks.Task CargarMovimientos()
        {
            try
            {
                // Limpiar el DataGridView
                GW_Movements.DataSource = null;
                GW_Movements.Rows.Clear();

                var movimientos = await _apiService.GetMovementsAsync();

                if (movimientos != null && movimientos.Count > 0)
                {
                    // Configurar el DataGridView con los datos
                    var dataTable = new System.Data.DataTable();
                    dataTable.Columns.Add("Cuenta", typeof(string));
                    dataTable.Columns.Add("Nro", typeof(int));
                    dataTable.Columns.Add("Fecha", typeof(DateTime));
                    dataTable.Columns.Add("Tipo", typeof(string));
                    dataTable.Columns.Add("Importe", typeof(decimal));
                    dataTable.Columns.Add("Saldo", typeof(decimal));

                    foreach (var mov in movimientos)
                    {
                        dataTable.Rows.Add(
                            mov.codigoCuenta,
                            mov.numero,
                            mov.fecha,
                            mov.tipo,
                            mov.importe,
                            mov.saldoActual
                        );
                    }

                    GW_Movements.DataSource = dataTable;

                    // Ajustar el formato de las columnas
                    if (GW_Movements.Columns["Fecha"] != null)
                    {
                        GW_Movements.Columns["Fecha"].DefaultCellStyle.Format = "yyyy-MM-dd";
                    }
                    
                    if (GW_Movements.Columns["Importe"] != null)
                    {
                        GW_Movements.Columns["Importe"].DefaultCellStyle.Format = "N2";
                        GW_Movements.Columns["Importe"].DefaultCellStyle.FormatProvider = System.Globalization.CultureInfo.InvariantCulture;
                        GW_Movements.Columns["Importe"].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleRight;
                    }

                    if (GW_Movements.Columns["Saldo"] != null)
                    {
                        GW_Movements.Columns["Saldo"].DefaultCellStyle.Format = "N2";
                        GW_Movements.Columns["Saldo"].DefaultCellStyle.FormatProvider = System.Globalization.CultureInfo.InvariantCulture;
                        GW_Movements.Columns["Saldo"].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleRight;
                    }
                }
                else
                {
                    MessageBox.Show("No se encontraron movimientos", "Información", 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al cargar los movimientos:\n{ex.Message}", 
                    "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private async void btn_Ingresar_Click(object sender, EventArgs e)
        {
            using (frmModalPersonalizado miModal = new frmModalPersonalizado())
            {
                // Mostrar el modal para crear un nuevo movimiento
                DialogResult resultado = miModal.ShowDialog();

                if (resultado == DialogResult.OK)
                {
                    // Si el usuario aceptó, recargar los movimientos
                    await CargarMovimientos();
                    MessageBox.Show("Movimiento creado exitosamente.", "Éxito", 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
        }

        private void GW_Movements_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            // Aquí puedes agregar funcionalidad para mostrar detalles del movimiento seleccionado
        }
    }
}
