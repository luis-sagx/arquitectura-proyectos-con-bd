using Microsoft.Extensions.Logging;

namespace CLIMOV_EurekaBank_REST_DOTNET_GR09;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();
        builder.UseMauiApp<App>();
#if DEBUG
        builder.Logging.AddDebug();
#endif
        return builder.Build();
    }
}
