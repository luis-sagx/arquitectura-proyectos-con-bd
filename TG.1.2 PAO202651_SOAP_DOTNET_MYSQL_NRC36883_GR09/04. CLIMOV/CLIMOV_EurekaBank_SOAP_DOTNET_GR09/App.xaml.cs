using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

public partial class App : Application
{
    public App() => InitializeComponent();

    protected override Window CreateWindow(IActivationState? activationState) =>
        new(new NavigationPage(new LoginPage()) { BarBackgroundColor = Color.FromArgb("#0B1F3A"), BarTextColor = Colors.White });
}
