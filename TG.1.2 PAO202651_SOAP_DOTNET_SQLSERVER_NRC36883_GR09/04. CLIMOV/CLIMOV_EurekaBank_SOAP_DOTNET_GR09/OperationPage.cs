using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

public sealed class OperationPage : ContentView
{
    private readonly AccountDto _origin;
    private readonly Func<Task> _onSuccess;
    private readonly Action _onClose;
    private readonly Picker _type = new() { Title = "Seleccione tipo" };
    private readonly Entry _destination = new() { Placeholder = "Cuenta destino", IsVisible = false };
    private readonly Entry _amount = new() { Placeholder = "0.00", Keyboard = Keyboard.Numeric };
    private readonly Label _destinationLabel = new() { Text = "Cuenta destino", FontSize = 13, TextColor = Color.FromArgb("#344054"), IsVisible = false };
    private readonly Label _error = new() { TextColor = Color.FromArgb("#B42318"), FontSize = 13 };
    private readonly Button _save = new()
    {
        Text = "Registrar",
        BackgroundColor = Color.FromArgb("#0B1F3A"),
        TextColor = Colors.White,
        HeightRequest = 48,
        CornerRadius = 8
    };
    private readonly Button _cancel = new()
    {
        Text = "Cancelar",
        BackgroundColor = Colors.Transparent,
        TextColor = Color.FromArgb("#475467"),
        HeightRequest = 48
    };
    private bool _typesLoaded;

    public OperationPage(AccountDto origin, Func<Task> onSuccess, Action onClose)
    {
        _origin = origin;
        _onSuccess = onSuccess;
        _onClose = onClose;

        var title = new Label
        {
            Text = "Registrar movimiento",
            FontSize = 20,
            FontAttributes = FontAttributes.Bold,
            TextColor = Color.FromArgb("#101828")
        };
        var typeLabel = new Label { Text = "Tipo", FontSize = 13, TextColor = Color.FromArgb("#344054") };
        var amountLabel = new Label { Text = "Importe", FontSize = 13, TextColor = Color.FromArgb("#344054") };

        StyleInput(_type);
        StyleInput(_destination);
        StyleInput(_amount);

        _type.SelectedIndexChanged += TypeChanged;
        _save.Clicked += SaveClicked;
        _cancel.Clicked += (_, _) => Close();

        var actions = new Grid
        {
            ColumnDefinitions = { new ColumnDefinition(GridLength.Star), new ColumnDefinition(GridLength.Star) },
            ColumnSpacing = 10,
            Children = { _cancel, _save }
        };
        Grid.SetColumn(_save, 1);

        var form = new VerticalStackLayout
        {
            Spacing = 9,
            Children = { title, typeLabel, _type, _destinationLabel, _destination, amountLabel, _amount, _error, actions }
        };
        var dialog = new Border
        {
            BackgroundColor = Colors.White,
            Stroke = Color.FromArgb("#E1E7EF"),
            StrokeShape = new Microsoft.Maui.Controls.Shapes.RoundRectangle { CornerRadius = 14 },
            Padding = 20,
            HorizontalOptions = LayoutOptions.Fill,
            VerticalOptions = LayoutOptions.Center,
            Content = form
        };

        Content = new Grid
        {
            Padding = new Thickness(18),
            BackgroundColor = Color.FromArgb("#80000000"),
            Children = { dialog }
        };
    }

    public async void Show()
    {
        _error.Text = "";
        _amount.Text = "";
        IsVisible = true;
        if (_typesLoaded) return;
        try
        {
            var types = await BankApi.MovementTypes();
            _type.Items.Clear();
            foreach (var type in types) _type.Items.Add(type);
            if (_type.Items.Count > 0) _type.SelectedIndex = 0;
            _typesLoaded = true;
        }
        catch (Exception ex) { _error.Text = $"No se pudieron cargar los tipos: {ex.Message}"; }
    }

    private static void StyleInput(View view)
    {
        view.BackgroundColor = Colors.White;
        view.HeightRequest = 48;
    }

    private void TypeChanged(object? sender, EventArgs e)
    {
        var isTransfer = string.Equals(_type.SelectedItem?.ToString(), "TRANSFERENCIA", StringComparison.OrdinalIgnoreCase);
        _destination.IsVisible = isTransfer;
        _destinationLabel.IsVisible = isTransfer;
        if (!isTransfer) _destination.Text = "";
    }

    private async void SaveClicked(object? sender, EventArgs e)
    {
        if (!decimal.TryParse(_amount.Text, out var amount) || amount <= 0)
        {
            _error.Text = "El importe debe ser mayor a cero.";
            return;
        }

        var type = _type.SelectedItem?.ToString() ?? "";
        var destination = _destination.Text?.Trim();
        if (type == "TRANSFERENCIA" && (string.IsNullOrWhiteSpace(destination) || destination == _origin.Codigo))
        {
            _error.Text = "Ingresa una cuenta destino válida y distinta a la de origen.";
            return;
        }

        _save.IsEnabled = false;
        _save.Text = "Procesando…";
        _error.Text = "";
        try
        {
            var error = await BankApi.CreateMovement(_origin.Codigo, destination, type, amount);
            if (error is not null)
            {
                _error.Text = error;
                return;
            }
            await _onSuccess();
            Close();
        }
        catch (Exception ex) { _error.Text = ex.Message; }
        finally
        {
            _save.IsEnabled = true;
            _save.Text = "Registrar";
        }
    }

    private void Close()
    {
        IsVisible = false;
        _onClose();
    }
}
