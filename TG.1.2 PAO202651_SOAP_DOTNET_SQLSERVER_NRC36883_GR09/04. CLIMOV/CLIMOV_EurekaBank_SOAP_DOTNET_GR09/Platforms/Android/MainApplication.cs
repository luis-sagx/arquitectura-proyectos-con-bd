using Android.App;
using Android.Runtime;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

[Application]
public class MainApplication(IntPtr handle, JniHandleOwnership ownership) : MauiApplication(handle, ownership)
{
    protected override MauiApp CreateMauiApp() => MauiProgram.CreateMauiApp();
}
