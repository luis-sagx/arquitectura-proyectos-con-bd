using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

public sealed class LoginPage : ContentPage
{
    private readonly Entry _user = new() { Placeholder = "Usuario", Keyboard = Keyboard.Text };
    private readonly Entry _password = new() { Placeholder = "Contraseña", IsPassword = true };
    private readonly Label _message = new() { TextColor = Color.FromArgb("#B42318"), HorizontalTextAlignment = TextAlignment.Center };
    private readonly Button _login = new() { Text = "Iniciar Sesión", BackgroundColor = Color.FromArgb("#0B1F3A"), TextColor = Colors.White, HeightRequest = 56, CornerRadius = 12 };

    public LoginPage()
    {
        NavigationPage.SetHasNavigationBar(this, false);
        Title = "EurekaBank";
        BackgroundColor = Color.FromArgb("#F4F7FA");
        _login.Clicked += LoginClicked;
        Content = new ScrollView { Content = new VerticalStackLayout
        {
            Padding = new Thickness(24, 20), Spacing = 14,
            Children =
            {
                new Border
                {
                    HeightRequest = 210,
                    Stroke = Color.FromArgb("#E1E7EF"),
                    StrokeShape = new Microsoft.Maui.Controls.Shapes.RoundRectangle { CornerRadius = 16 },
                    Content = new Image { Source = "login_background.webp", Aspect = Aspect.AspectFill }
                },
                new VerticalStackLayout { Padding = new Thickness(0, 8), Spacing = 4, Children =
                {
                    new Label { Text = "SOAP .NET", FontSize = 24, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#0B1F3A"), HorizontalTextAlignment = TextAlignment.Center },
                    new Label { Text = "Iniciar sesión en EuBank", FontSize = 17, TextColor = Color.FromArgb("#173F66"), HorizontalTextAlignment = TextAlignment.Center }
                }},
                CreateOutlinedEntry(_user), CreateOutlinedEntry(_password), _message, _login
            }
        }};
    }

    private static Border CreateOutlinedEntry(Entry entry)
    {
        entry.BackgroundColor = Colors.White;
        entry.TextColor = Color.FromArgb("#101828");
        entry.PlaceholderColor = Color.FromArgb("#667085");
        entry.HeightRequest = 52;
        return new Border
        {
            Stroke = Color.FromArgb("#CBD5E1"),
            StrokeShape = new Microsoft.Maui.Controls.Shapes.RoundRectangle { CornerRadius = 8 },
            Padding = new Thickness(12, 0),
            BackgroundColor = Colors.White,
            Content = entry
        };
    }

    private async void LoginClicked(object? sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(_user.Text) || string.IsNullOrWhiteSpace(_password.Text))
        {
            _message.Text = "Complete todos los campos";
            return;
        }
        _login.IsEnabled = false;
        _login.Text = "Validando…";
        _message.Text = "";
        try
        {
            if (!await BankApi.Login(_user.Text.Trim(), _password.Text))
            {
                _message.Text = "Usuario o contraseña incorrectos.";
                return;
            }
            var accounts = await BankApi.Accounts();
            var page = new AccountsPage(accounts);
            await Navigation.PushAsync(page);
            Navigation.RemovePage(this);
        }
        catch (Exception ex) { _message.Text = $"No se pudo conectar con el servidor: {ex.Message}"; }
        finally { _login.IsEnabled = true; _login.Text = "Iniciar Sesión"; }
    }
}
