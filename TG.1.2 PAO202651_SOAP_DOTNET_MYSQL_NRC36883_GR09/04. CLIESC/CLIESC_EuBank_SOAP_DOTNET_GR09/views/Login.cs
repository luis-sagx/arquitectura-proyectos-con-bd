using System;
using System.Net.Http;
using System.Runtime.InteropServices;
using System.Drawing;
using System.Windows.Forms;
using CLIESC_EuBank_SOAP_DOTNET_GR09.Services;
using CLIESC_EuBank_SOAP_DOTNET_GR09.Models;

namespace CLIESC_EuBank_SOAP_DOTNET_GR09.views
{
    public partial class Login : Form
    {
        private readonly EuBankApiService _apiService;
        private Label _error = new();

        public Login()
        {
            _apiService = new EuBankApiService();
            BuildSoapLogin();
        }

        private void BuildSoapLogin()
        {
            SuspendLayout();
            Text = "Iniciar sesión - EuBank";
            FormBorderStyle = FormBorderStyle.None;
            StartPosition = FormStartPosition.CenterScreen;
            ClientSize = new Size(1024, 680);
            BackColor = Color.FromArgb(11, 31, 58);
            BackgroundImage = Properties.Resources.LoginBackground;
            BackgroundImageLayout = ImageLayout.Stretch;
            Font = new Font("Segoe UI", 10F, FontStyle.Regular);

            header = new Panel
            {
                BackColor = Color.FromArgb(11, 31, 58),
                Location = Point.Empty,
                Size = new Size(ClientSize.Width, 30),
                Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right
            };
            header.MouseDown += header_MouseDown;

            lblHeader = new Label
            {
                AutoSize = true,
                Text = "INICIAR SESIÓN",
                ForeColor = Color.White,
                Font = new Font("Roboto Medium", 12F, FontStyle.Regular),
                Location = new Point(18, 6)
            };
            btn_exit = new Button
            {
                Text = "X",
                ForeColor = Color.White,
                BackColor = Color.FromArgb(11, 31, 58),
                FlatStyle = FlatStyle.Flat,
                Size = new Size(45, 30),
                Location = new Point(ClientSize.Width - 45, 0),
                Anchor = AnchorStyles.Top | AnchorStyles.Right,
                Cursor = Cursors.Hand
            };
            btn_exit.FlatAppearance.BorderSize = 0;
            btn_exit.FlatAppearance.MouseOverBackColor = Color.FromArgb(255, 51, 51);
            btn_exit.FlatAppearance.MouseDownBackColor = Color.FromArgb(153, 0, 0);
            btn_exit.Click += btn_exit_Click;
            header.Controls.Add(lblHeader);
            header.Controls.Add(btn_exit);

            lbl_tech = new Label
            {
                Text = "SOAP .NET",
                ForeColor = Color.White,
                Font = new Font("Roboto", 21F, FontStyle.Bold),
                TextAlign = ContentAlignment.MiddleCenter,
                Location = new Point(302, 150),
                Size = new Size(420, 45)
            };

            lblUser = NewLoginLabel("Usuario", 329, 230);
            tbx_user = NewLoginTextBox(329, 262);
            lblPass = NewLoginLabel("Contraseña", 329, 328);
            txb_password = NewLoginTextBox(329, 360);
            txb_password.UseSystemPasswordChar = true;

            _error = new Label
            {
                ForeColor = Color.FromArgb(255, 208, 208),
                Font = new Font("Segoe UI", 9F),
                TextAlign = ContentAlignment.MiddleCenter,
                Location = new Point(302, 412),
                Size = new Size(420, 40)
            };

            btn_Ingresar = new Button
            {
                Text = "Iniciar Sesión",
                BackColor = Color.FromArgb(23, 63, 102),
                ForeColor = Color.White,
                Font = new Font("Roboto", 14F, FontStyle.Bold),
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand,
                Location = new Point(332, 475),
                Size = new Size(360, 50)
            };
            btn_Ingresar.FlatAppearance.BorderSize = 0;
            btn_Ingresar.FlatAppearance.MouseOverBackColor = Color.FromArgb(18, 52, 82);
            btn_Ingresar.Click += btn_Ingresar_Click;

            Controls.Add(lbl_tech);
            Controls.Add(lblUser);
            Controls.Add(tbx_user);
            Controls.Add(lblPass);
            Controls.Add(txb_password);
            Controls.Add(_error);
            Controls.Add(btn_Ingresar);
            Controls.Add(header);
            AcceptButton = btn_Ingresar;
            ResumeLayout(false);
        }

        private static Label NewLoginLabel(string text, int x, int y) => new()
        {
            AutoSize = true,
            Text = text,
            ForeColor = Color.White,
            Font = new Font("Roboto", 16F),
            Location = new Point(x, y)
        };

        private static TextBox NewLoginTextBox(int x, int y) => new()
        {
            BorderStyle = BorderStyle.FixedSingle,
            BackColor = Color.White,
            ForeColor = Color.FromArgb(102, 102, 102),
            Font = new Font("Roboto Light", 14F),
            Location = new Point(x, y),
            Size = new Size(360, 38),
            TabStop = true
        };

        [DllImport("user32.DLL", EntryPoint = "ReleaseCapture")]
        private static extern void ReleaseCapture();

        [DllImport("user32.DLL", EntryPoint = "SendMessage")]
        private static extern void SendMessage(IntPtr hWnd, int wMsg, int wParam, int lParam);

        private void header_MouseDown(object sender, MouseEventArgs e)
        {
            ReleaseCapture();
            SendMessage(Handle, 0x112, 0xf012, 0);
        }

        private void btn_exit_Click(object sender, EventArgs e) => Application.Exit();

        private void min_button_Click(object sender, EventArgs e) => WindowState = FormWindowState.Minimized;

        private void lblUser_Click(object sender, EventArgs e) { }

        private async void btn_Ingresar_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(tbx_user.Text))
            {
                _error.Text = "El usuario no puede estar vacío.";
                tbx_user.Focus();
                return;
            }
            if (string.IsNullOrWhiteSpace(txb_password.Text))
            {
                _error.Text = "La contraseña no puede estar vacía.";
                txb_password.Focus();
                return;
            }

            btn_Ingresar.Enabled = false;
            btn_Ingresar.Text = "Validando...";
            _error.Text = "";
            try
            {
                var request = new LoginRequest { usuario = tbx_user.Text.Trim(), clave = txb_password.Text };
                if (await _apiService.LoginAsync(request))
                {
                    Hide();
                    var home = new Home();
                    home.FormClosed += (_, _) => Close();
                    home.Show();
                }
                else
                {
                    _error.Text = "Usuario o clave incorrectos.";
                    txb_password.Clear();
                    txb_password.Focus();
                }
            }
            catch (Exception ex)
            {
                _error.Text = $"Error al conectar con el servidor: {ex.Message}";
            }
            finally
            {
                if (!IsDisposed)
                {
                    btn_Ingresar.Enabled = true;
                    btn_Ingresar.Text = "Iniciar Sesión";
                }
            }
        }
    }
}
