using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

public sealed class AccountsPage : ContentPage
{
    private readonly CollectionView _list = new();
    private readonly Label _message = new()
    {
        TextColor = Color.FromArgb("#B42318"),
        HorizontalTextAlignment = TextAlignment.Center,
        VerticalTextAlignment = TextAlignment.Center,
        IsVisible = false
    };
    private List<AccountDto> _accounts;

    public AccountsPage(List<AccountDto> accounts)
    {
        _accounts = accounts;
        Title = "Cuentas";
        NavigationPage.SetHasNavigationBar(this, false);
        BackgroundColor = Color.FromArgb("#F4F7FB");

        _list.EmptyView = new Label
        {
            Text = "No hay cuentas disponibles",
            TextColor = Color.FromArgb("#475467"),
            HorizontalTextAlignment = TextAlignment.Center,
            VerticalTextAlignment = TextAlignment.Center
        };
        _list.ItemTemplate = new DataTemplate(CreateAccountCard);
        _list.SelectionMode = SelectionMode.Single;
        _list.SelectionChanged += AccountSelected;
        _list.ItemsSource = _accounts;

        var content = new Grid
        {
            RowDefinitions = { new RowDefinition(new GridLength(56)), new RowDefinition(GridLength.Star) },
            Children = { BankHeader.Create("Cuentas", onRefresh: LoadAccounts), _list, _message }
        };
        Grid.SetRow(_list, 1);
        Grid.SetRow(_message, 1);
        Content = content;
    }

    private View CreateAccountCard()
    {
        var code = new Label { FontAttributes = FontAttributes.Bold, FontSize = 16, TextColor = Color.FromArgb("#0B1F3A") };
        code.SetBinding(Label.TextProperty, nameof(AccountDto.Codigo));

        var clientCurrency = new Label { FontSize = 14, TextColor = Color.FromArgb("#344054") };
        clientCurrency.SetBinding(Label.TextProperty, new Binding(nameof(AccountDto.NombreCliente), stringFormat: "{0} · "));
        var currency = new Label { FontSize = 14, TextColor = Color.FromArgb("#344054") };
        currency.SetBinding(Label.TextProperty, nameof(AccountDto.Moneda));

        var balance = new Label { FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#0B1F3A") };
        balance.SetBinding(Label.TextProperty, new Binding(nameof(AccountDto.Saldo), stringFormat: "Saldo: S/ {0:N2}"));
        var state = new Label { FontSize = 12, TextColor = Color.FromArgb("#667085") };
        state.SetBinding(Label.TextProperty, new Binding(nameof(AccountDto.Estado), stringFormat: "{0} · "));
        var email = new Label { FontSize = 12, TextColor = Color.FromArgb("#667085"), LineBreakMode = LineBreakMode.TailTruncation };
        email.SetBinding(Label.TextProperty, nameof(AccountDto.EmailCliente));
        var arrow = new Label { Text = "›", FontSize = 28, TextColor = Color.FromArgb("#667085"), VerticalTextAlignment = TextAlignment.Center };

        var details = new VerticalStackLayout
        {
            Spacing = 3,
            Children =
            {
                code,
                new HorizontalStackLayout { Spacing = 0, Children = { clientCurrency, currency } },
                balance,
                new HorizontalStackLayout { Spacing = 0, Children = { state, email } }
            }
        };
        var row = new Grid
        {
            ColumnDefinitions = { new ColumnDefinition(GridLength.Star), new ColumnDefinition(new GridLength(28)) },
            Padding = 14,
            ColumnSpacing = 8
        };
        row.Add(details, 0, 0);
        row.Add(arrow, 1, 0);

        return new Border
        {
            Margin = new Thickness(12, 7),
            BackgroundColor = Colors.White,
            Stroke = Color.FromArgb("#E1E7EF"),
            StrokeShape = new Microsoft.Maui.Controls.Shapes.RoundRectangle { CornerRadius = 12 },
            Content = row
        };
    }

    private async Task LoadAccounts()
    {
        try
        {
            _message.IsVisible = false;
            _accounts = await BankApi.Accounts();
            _list.ItemsSource = null;
            _list.ItemsSource = _accounts;
        }
        catch (Exception ex)
        {
            _message.Text = $"Error: {ex.Message}";
            _message.IsVisible = true;
        }
    }

    private async void AccountSelected(object? sender, SelectionChangedEventArgs e)
    {
        if (e.CurrentSelection.FirstOrDefault() is not AccountDto account) return;
        _list.SelectedItem = null;
        await Navigation.PushAsync(new MovementsPage(account));
    }
}
