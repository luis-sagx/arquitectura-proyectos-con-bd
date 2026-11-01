using Microsoft.Maui.Controls;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

internal static class BankHeader
{
    private static readonly Color Navy = Color.FromArgb("#0B1F3A");

    public static Grid Create(string title, Action? onBack = null, Func<Task>? onRefresh = null)
    {
        var bar = new Grid
        {
            HeightRequest = 56,
            BackgroundColor = Navy,
            ColumnDefinitions =
            {
                new ColumnDefinition(new GridLength(54)),
                new ColumnDefinition(GridLength.Star),
                new ColumnDefinition(new GridLength(54))
            }
        };

        if (onBack is not null)
        {
            var back = new Button
            {
                Text = "‹",
                FontSize = 34,
                TextColor = Colors.White,
                BackgroundColor = Colors.Transparent,
                Padding = 0,
                VerticalOptions = LayoutOptions.Fill,
                HorizontalOptions = LayoutOptions.Fill
            };
            back.Clicked += (_, _) => onBack();
            bar.Add(back, 0, 0);
        }

        var titleContent = new HorizontalStackLayout
        {
            Spacing = 7,
            HorizontalOptions = LayoutOptions.Center,
            VerticalOptions = LayoutOptions.Center,
            Children =
            {
                new Image { Source = "sulli_general.png", WidthRequest = 30, HeightRequest = 30, Aspect = Aspect.AspectFit },
                new Label
                {
                    Text = title,
                    FontSize = 17,
                    FontAttributes = FontAttributes.Bold,
                    TextColor = Colors.White,
                    VerticalTextAlignment = TextAlignment.Center,
                    LineBreakMode = LineBreakMode.TailTruncation
                }
            }
        };
        bar.Add(titleContent, 1, 0);

        if (onRefresh is not null)
        {
            var refresh = new Button
            {
                Text = "↻",
                FontSize = 25,
                TextColor = Colors.White,
                BackgroundColor = Colors.Transparent,
                Padding = 0,
                VerticalOptions = LayoutOptions.Fill,
                HorizontalOptions = LayoutOptions.Fill
            };
            refresh.Clicked += async (_, _) => await onRefresh();
            bar.Add(refresh, 2, 0);
        }

        return bar;
    }
}
