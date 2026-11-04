using Microsoft.Maui.Controls;
using System.Globalization;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

public sealed class MovementsPage : ContentPage
{
    private readonly AccountDto _account;
    private readonly CollectionView _list = new() { EmptyView = "No hay movimientos" };
    private readonly Label _message = new()
    {
        TextColor = Color.FromArgb("#B42318"),
        HorizontalTextAlignment = TextAlignment.Center,
        IsVisible = false
    };
    private readonly Button _new = new()
    {
        Text = "+",
        FontSize = 28,
        TextColor = Colors.White,
        BackgroundColor = Color.FromArgb("#0B1F3A"),
        WidthRequest = 56,
        HeightRequest = 56,
        CornerRadius = 28,
        Shadow = new Shadow { Brush = new SolidColorBrush(Colors.Black), Opacity = 0.22f, Radius = 8, Offset = new Point(0, 3) }
    };
    private OperationPage _operation = null!;

    public MovementsPage(AccountDto account)
    {
        _account = account;
        Title = $"Movimientos - {account.Codigo}";
        NavigationPage.SetHasNavigationBar(this, false);
        BackgroundColor = Color.FromArgb("#F4F7FB");

        _list.ItemTemplate = new DataTemplate(CreateMovementItem);
        _list.ItemsLayout = new LinearItemsLayout(ItemsLayoutOrientation.Vertical) { ItemSpacing = 0 };

        _operation = new OperationPage(_account, LoadMovements, () => _operation.IsVisible = false)
        {
            IsVisible = false,
            ZIndex = 2
        };
        _new.Clicked += (_, _) => _operation.Show();

        var root = new Grid
        {
            RowDefinitions = { new RowDefinition(new GridLength(56)), new RowDefinition(GridLength.Star) },
            Children =
            {
                BankHeader.Create($"Movimientos - {_account.Codigo}", onBack: async () => await Navigation.PopAsync()),
                _list,
                _message,
                _new,
                _operation
            }
        };
        Grid.SetRow(_list, 1);
        Grid.SetRow(_message, 1);
        Grid.SetRow(_new, 1);
        Grid.SetRow(_operation, 0);
        Grid.SetRowSpan(_operation, 2);
        _new.HorizontalOptions = LayoutOptions.End;
        _new.VerticalOptions = LayoutOptions.End;
        _new.Margin = new Thickness(18);
        Content = root;
    }

    protected override async void OnAppearing()
    {
        base.OnAppearing();
        await LoadMovements();
    }

    private View CreateMovementItem()
    {
        var title = new Label { FontAttributes = FontAttributes.Bold, FontSize = 15 };
        title.SetBinding(Label.TextProperty, new Binding(".", converter: new MovementTitleConverter()));
        title.SetBinding(Label.TextColorProperty, new Binding(".", converter: new MovementColorConverter()));

        var date = new Label { FontSize = 12, TextColor = Color.FromArgb("#667085") };
        date.SetBinding(Label.TextProperty, new Binding(nameof(MovementDto.Fecha), converter: new IsoDateConverter()));
        var reference = new Label { FontSize = 13, TextColor = Color.FromArgb("#475467") };
        reference.SetBinding(Label.TextProperty, new Binding(nameof(MovementDto.Referencia), stringFormat: "Ref: {0}"));
        reference.SetBinding(Label.IsVisibleProperty, new Binding(nameof(MovementDto.Referencia), converter: new NotBlankConverter()));
        var balance = new Label { FontSize = 13, TextColor = Color.FromArgb("#475467") };
        balance.SetBinding(Label.TextProperty, new Binding(nameof(MovementDto.SaldoActual), stringFormat: "Saldo: S/ {0:N2}"));

        var details = new VerticalStackLayout
        {
            Spacing = 3,
            Padding = new Thickness(16, 11),
            Children = { title, date, reference, balance, new BoxView { HeightRequest = 1, Color = Color.FromArgb("#E1E7EF") } }
        };
        return details;
    }

    private async Task LoadMovements()
    {
        try
        {
            _message.IsVisible = false;
            _list.ItemsSource = await BankApi.Movements(_account.Codigo);
        }
        catch (Exception ex)
        {
            _message.Text = $"Error: {ex.Message}";
            _message.IsVisible = true;
        }
    }
}

internal sealed class IsoDateConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        var raw = value?.ToString() ?? "";
        return DateTime.TryParseExact(raw, "yyyy-MM-dd", CultureInfo.InvariantCulture, DateTimeStyles.None, out var date) ? date.ToString("dd/MM/yyyy") : raw;
    }
    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) => value?.ToString() ?? "";
}

internal sealed class MovementTitleConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        if (value is not MovementDto movement) return "";
        var prefix = movement.Tipo.Contains("Entrada", StringComparison.OrdinalIgnoreCase) ? "+" : movement.Tipo.Contains("Salida", StringComparison.OrdinalIgnoreCase) ? "-" : "";
        return $"{movement.Tipo} {prefix}{movement.Importe.ToString("N2", CultureInfo.InvariantCulture)}";
    }
    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) => value?.ToString() ?? "";
}

internal sealed class MovementColorConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        if (value is not MovementDto movement) return Color.FromArgb("#344054");
        if (movement.Tipo.Contains("Entrada", StringComparison.OrdinalIgnoreCase)) return Color.FromArgb("#16804A");
        if (movement.Tipo.Contains("Salida", StringComparison.OrdinalIgnoreCase)) return Color.FromArgb("#B42318");
        return Color.FromArgb("#344054");
    }
    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) => value ?? Colors.Gray;
}

internal sealed class NotBlankConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture) => !string.IsNullOrWhiteSpace(value?.ToString());
    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) => value ?? false;
}
