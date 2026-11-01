namespace CLIESC_EuBank_SOAP_DOTNET_GR09.views
{
    partial class Home
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Home));
            this.header = new System.Windows.Forms.Panel();
            this.min_button = new System.Windows.Forms.Button();
            this.btn_exit = new System.Windows.Forms.Button();
            this.pictureBox4 = new System.Windows.Forms.PictureBox();
            this.MenuVertical = new System.Windows.Forms.Panel();
            this.btn_movements = new System.Windows.Forms.Button();
            this.btn_clients = new System.Windows.Forms.Button();
            this.btn_Ingresar = new System.Windows.Forms.Button();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.Contenedor = new System.Windows.Forms.Panel();
            this.header.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).BeginInit();
            this.MenuVertical.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // header
            // 
            this.header.BackColor = System.Drawing.Color.FromArgb(11, 31, 58);
            this.header.Controls.Add(this.min_button);
            this.header.Controls.Add(this.btn_exit);
            this.header.Controls.Add(this.pictureBox4);
            this.header.Dock = System.Windows.Forms.DockStyle.Top;
            this.header.Location = new System.Drawing.Point(0, 0);
            this.header.Name = "header";
            this.header.Size = new System.Drawing.Size(1024, 30);
            this.header.TabIndex = 3;
            this.header.MouseDown += new System.Windows.Forms.MouseEventHandler(this.header_MouseDown);
            // 
            // min_button
            // 
            this.min_button.BackColor = System.Drawing.Color.Transparent;
            this.min_button.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("min_button.BackgroundImage")));
            this.min_button.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Zoom;
            this.min_button.Cursor = System.Windows.Forms.Cursors.Hand;
            this.min_button.FlatAppearance.BorderSize = 0;
            this.min_button.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(23, 63, 102);
            this.min_button.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.min_button.ForeColor = System.Drawing.Color.Transparent;
            this.min_button.Location = new System.Drawing.Point(930, 3);
            this.min_button.Name = "min_button";
            this.min_button.Size = new System.Drawing.Size(25, 25);
            this.min_button.Visible = false;
            this.min_button.TabIndex = 10;
            this.min_button.UseVisualStyleBackColor = true;
            this.min_button.Click += new System.EventHandler(this.min_button_Click);
            // 
            // btn_exit
            // 
            this.btn_exit.BackColor = System.Drawing.Color.Transparent;
            this.btn_exit.Text = "X";
            this.btn_exit.Font = new System.Drawing.Font("Roboto", 14F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_exit.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_exit.FlatAppearance.BorderSize = 0;
            this.btn_exit.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(255, 51, 51);
            this.btn_exit.FlatAppearance.MouseDownBackColor = System.Drawing.Color.FromArgb(153, 0, 0);
            this.btn_exit.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_exit.ForeColor = System.Drawing.Color.Transparent;
            this.btn_exit.Anchor = System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right;
            this.btn_exit.Location = new System.Drawing.Point(979, 0);
            this.btn_exit.Name = "btn_exit";
            this.btn_exit.Size = new System.Drawing.Size(45, 30);
            this.btn_exit.TabIndex = 9;
            this.btn_exit.UseVisualStyleBackColor = true;
            this.btn_exit.Click += new System.EventHandler(this.btn_exit_Click);
            // 
            // pictureBox4
            // 
            this.pictureBox4.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox4.Image")));
            this.pictureBox4.Location = new System.Drawing.Point(3, 0);
            this.pictureBox4.Name = "pictureBox4";
            this.pictureBox4.Size = new System.Drawing.Size(41, 28);
            this.pictureBox4.Visible = false;
            this.pictureBox4.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox4.TabIndex = 8;
            this.pictureBox4.TabStop = false;
            // 
            // MenuVertical
            // 
            this.MenuVertical.BackColor = System.Drawing.Color.FromArgb(7, 34, 63);
            this.MenuVertical.Controls.Add(this.btn_movements);
            this.MenuVertical.Controls.Add(this.btn_clients);
            this.MenuVertical.Controls.Add(this.btn_Ingresar);
            this.MenuVertical.Controls.Add(this.pictureBox1);
            this.MenuVertical.Dock = System.Windows.Forms.DockStyle.Left;
            this.MenuVertical.Location = new System.Drawing.Point(0, 30);
            this.MenuVertical.Name = "MenuVertical";
            this.MenuVertical.Size = new System.Drawing.Size(220, 650);
            this.MenuVertical.TabIndex = 4;
            // 
            // btn_movements
            // 
            this.btn_movements.BackColor = System.Drawing.Color.FromArgb(7, 34, 63);
            this.btn_movements.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_movements.FlatAppearance.BorderColor = System.Drawing.Color.FromArgb(7, 34, 63);
            this.btn_movements.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(18, 52, 82);
            this.btn_movements.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_movements.Font = new System.Drawing.Font("Roboto", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_movements.ForeColor = System.Drawing.Color.White;
            this.btn_movements.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.btn_movements.Location = new System.Drawing.Point(0, 185);
            this.btn_movements.Name = "btn_movements";
            this.btn_movements.Size = new System.Drawing.Size(220, 50);
            this.btn_movements.TabIndex = 16;
            this.btn_movements.Text = "Movimientos";
            this.btn_movements.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_movements.Padding = new System.Windows.Forms.Padding(20, 0, 0, 0);
            this.btn_movements.TextImageRelation = System.Windows.Forms.TextImageRelation.TextBeforeImage;
            this.btn_movements.UseVisualStyleBackColor = false;
            this.btn_movements.Click += new System.EventHandler(this.btn_movements_Click);
            // 
            // btn_clients
            // 
            this.btn_clients.BackColor = System.Drawing.Color.FromArgb(7, 34, 63);
            this.btn_clients.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_clients.FlatAppearance.BorderColor = System.Drawing.Color.FromArgb(7, 34, 63);
            this.btn_clients.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(18, 52, 82);
            this.btn_clients.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_clients.Font = new System.Drawing.Font("Roboto", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_clients.ForeColor = System.Drawing.Color.White;
            this.btn_clients.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.btn_clients.Location = new System.Drawing.Point(0, 117);
            this.btn_clients.Name = "btn_clients";
            this.btn_clients.Size = new System.Drawing.Size(220, 50);
            this.btn_clients.TabIndex = 15;
            this.btn_clients.Text = "Cuentas de Clientes";
            this.btn_clients.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_clients.Padding = new System.Windows.Forms.Padding(20, 0, 0, 0);
            this.btn_clients.TextImageRelation = System.Windows.Forms.TextImageRelation.TextBeforeImage;
            this.btn_clients.UseVisualStyleBackColor = false;
            this.btn_clients.Click += new System.EventHandler(this.btn_clients_Click);
            // 
            // btn_Ingresar
            // 
            this.btn_Ingresar.BackColor = System.Drawing.Color.FromArgb(7, 34, 63);
            this.btn_Ingresar.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_Ingresar.FlatAppearance.BorderColor = System.Drawing.Color.FromArgb(7, 34, 63);
            this.btn_Ingresar.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(18, 52, 82);
            this.btn_Ingresar.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_Ingresar.Font = new System.Drawing.Font("Roboto", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_Ingresar.ForeColor = System.Drawing.Color.White;
            this.btn_Ingresar.Image = global::CLIESC_EuBank_SOAP_DOTNET_GR09.Properties.Resources.ingresar;
            this.btn_Ingresar.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.btn_Ingresar.Location = new System.Drawing.Point(0, 581);
            this.btn_Ingresar.Name = "btn_Ingresar";
            this.btn_Ingresar.Size = new System.Drawing.Size(220, 52);
            this.btn_Ingresar.TabIndex = 13;
            this.btn_Ingresar.Text = "Cerrar Sesión";
            this.btn_Ingresar.TextImageRelation = System.Windows.Forms.TextImageRelation.TextBeforeImage;
            this.btn_Ingresar.UseVisualStyleBackColor = false;
            this.btn_Ingresar.Click += new System.EventHandler(this.btn_Ingresar_Click);
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = global::CLIESC_EuBank_SOAP_DOTNET_GR09.Properties.Resources.ImageSidebar;
            this.pictureBox1.Location = new System.Drawing.Point(10, 22);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(200, 67);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // Contenedor
            // 
            this.Contenedor.Dock = System.Windows.Forms.DockStyle.Fill;
            this.Contenedor.Location = new System.Drawing.Point(220, 30);
            this.Contenedor.Name = "Contenedor";
            this.Contenedor.Size = new System.Drawing.Size(804, 650);
            this.Contenedor.TabIndex = 5;
            // 
            // Home
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.White;
            this.ClientSize = new System.Drawing.Size(1024, 680);
            this.Controls.Add(this.Contenedor);
            this.Controls.Add(this.MenuVertical);
            this.Controls.Add(this.header);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "Home";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Home";
            this.Load += new System.EventHandler(this.Home_Load);
            this.header.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).EndInit();
            this.MenuVertical.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel header;
        private System.Windows.Forms.Button min_button;
        private System.Windows.Forms.Button btn_exit;
        private System.Windows.Forms.PictureBox pictureBox4;
        private System.Windows.Forms.Panel MenuVertical;
        private System.Windows.Forms.Panel Contenedor;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Button btn_Ingresar;
        private System.Windows.Forms.Button btn_movements;
        private System.Windows.Forms.Button btn_clients;
    }
}
