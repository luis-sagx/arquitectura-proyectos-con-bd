using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Net.Http;
using CLIESC_EuBank_REST_DOTNET_GR09.Services;
using CLIESC_EuBank_REST_DOTNET_GR09.Models;

namespace CLIESC_EuBank_REST_DOTNET_GR09.views
{
    public partial class frmModalPersonalizado : Form
    {
        private Dictionary<string, Image> misImagenes = new Dictionary<string, Image>();
        private readonly EuBankApiService _apiService;
        private List<Account> _cuentasDisponibles;

        public frmModalPersonalizado()
        {
            InitializeComponent();
            // Inicializar el servicio de API
            var httpClient = new HttpClient();
            _apiService = new EuBankApiService(httpClient);
        }

        [DllImport("user32.DLL", EntryPoint = "ReleaseCapture")]
        private extern static void ReleaseCapture();
        [DllImport("user32.DLL", EntryPoint = "SendMessage")]
        private extern static void SendMessage(System.IntPtr hWnd, int wMsg, int wParam, int IParam);

        private void ldPanelRound1_MouseDown(object sender, MouseEventArgs e)
        {
            ReleaseCapture();
            SendMessage(this.Handle, 0x112, 0xf012, 0);
        }

        private async void frmModalPersonalizado_Load(object sender, EventArgs e)
        {
            pbVisor.SizeMode = PictureBoxSizeMode.Zoom;

            // Load images from project's Resources
            misImagenes.Add("RETIRO", Properties.Resources.Retiro);
            misImagenes.Add("DEPOSITO", Properties.Resources.Depósito);
            misImagenes.Add("TRANSFERENCIA", Properties.Resources.Transferencia);

            try
            {
                // Cargar tipos de movimientos desde el servicio
                var tiposMovimiento = await _apiService.GetMovementTypesAsync();
                
                if (tiposMovimiento != null && tiposMovimiento.Count > 0)
                {
                    foreach (string tipo in tiposMovimiento)
                    {
                        cbx_movimiento.Items.Add(tipo);
                    }

                    cbx_movimiento.OnSelectedIndexChanged += cbx_movimiento_OnSelectedIndexChanged;

                    if (cbx_movimiento.Items.Count > 0)
                    {
                        cbx_movimiento.SelectedIndex = 0;
                    }
                }

                // Cargar cuentas disponibles
                _cuentasDisponibles = await _apiService.GetAllAccountsAsync();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al cargar datos iniciales:\n{ex.Message}", 
                    "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void cbx_movimiento_OnSelectedIndexChanged(object sender, EventArgs e)
        {
            string seleccion = cbx_movimiento.SelectedItem?.ToString();

            if (!string.IsNullOrEmpty(seleccion))
            {
                // Buscar la imagen correspondiente en el diccionario
                if (misImagenes.ContainsKey(seleccion))
                {
                    pbVisor.Image = misImagenes[seleccion];
                }
                else
                {
                    pbVisor.Image = null;
                }

                // Habilitar/deshabilitar campo de cuenta destino según el tipo
                if (seleccion == "TRANSFERENCIA")
                {
                    txt_cuentadestino.Enabled = true;
                    label4.Enabled = true;
                }
                else
                {
                    txt_cuentadestino.Enabled = false;
                    txt_cuentadestino.Clear();
                    label4.Enabled = false;
                }
            }
        }

        private async void button1_Click(object sender, EventArgs e)
        {
            // Validar datos antes de crear el movimiento
            if (!ValidarDatos())
            {
                return;
            }

            try
            {
                // Crear el objeto de solicitud
                var movementRequest = new MovementRequest
                {
                    tipo = cbx_movimiento.SelectedItem.ToString(),
                    cuentaOrigen = tbx_cuentaorigen.Text.Trim(),
                    cuentaDestino = txt_cuentadestino.Enabled ? txt_cuentadestino.Text.Trim() : string.Empty,
                    importe = decimal.Parse(txt_monto.Text)
                };

                // Enviar la solicitud al servicio
                bool resultado = await _apiService.ProcessMovementAsync(movementRequest);

                if (resultado)
                {
                    MessageBox.Show("Movimiento procesado exitosamente", "Éxito", 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                    this.DialogResult = DialogResult.OK;
                    this.Close();
                }
                else
                {
                    MessageBox.Show(_apiService.LastError ?? "No se pudo procesar el movimiento. Verifique los datos e intente nuevamente.", 
                        "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al procesar el movimiento:\n{ex.Message}", 
                    "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private bool ValidarDatos()
        {
            // Validar tipo de movimiento
            if (cbx_movimiento.SelectedItem == null)
            {
                MessageBox.Show("Por favor seleccione un tipo de movimiento", "Validación", 
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                cbx_movimiento.Focus();
                return false;
            }

            // Validar cuenta origen
            if (string.IsNullOrWhiteSpace(tbx_cuentaorigen.Text))
            {
                MessageBox.Show("Por favor ingrese la cuenta origen", "Validación", 
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                tbx_cuentaorigen.Focus();
                return false;
            }

            // Validar cuenta destino si es transferencia
            if (cbx_movimiento.SelectedItem.ToString() == "TRANSFERENCIA")
            {
                if (string.IsNullOrWhiteSpace(txt_cuentadestino.Text))
                {
                    MessageBox.Show("Por favor ingrese la cuenta destino para la transferencia", 
                        "Validación", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    txt_cuentadestino.Focus();
                    return false;
                }

                if (tbx_cuentaorigen.Text.Trim() == txt_cuentadestino.Text.Trim())
                {
                    MessageBox.Show("La cuenta origen y destino no pueden ser iguales", 
                        "Validación", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    txt_cuentadestino.Focus();
                    return false;
                }
            }

            // Validar monto
            if (string.IsNullOrWhiteSpace(txt_monto.Text))
            {
                MessageBox.Show("Por favor ingrese el monto", "Validación", 
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                txt_monto.Focus();
                return false;
            }

            if (!decimal.TryParse(txt_monto.Text, out decimal monto))
            {
                MessageBox.Show("El monto ingresado no es válido", "Validación", 
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                txt_monto.Focus();
                return false;
            }

            if (monto <= 0)
            {
                MessageBox.Show("El monto debe ser mayor a cero", "Validación", 
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                txt_monto.Focus();
                return false;
            }

            return true;
        }
    }
}
