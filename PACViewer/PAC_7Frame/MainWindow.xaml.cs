using PAC_7Frame.Display;
using PAC_7Frame.Mqtt;
using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Threading;

namespace PAC_7Frame
{
    /// <summary>
    /// MainWindow.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class MainWindow : Window
    {
        private ResourceCore resourceCore;
        private MQTTClient mqttClient;


        public MainWindow()
        {
            InitializeComponent();
            InitializeObject();
        }

        private void InitializeObject()
        {
            resourceCore = new ResourceCore();
            mqttClient = new MQTTClient();
        }

        private void Window_ContentRendered(object sender, EventArgs e)
        {
            if (!CheckProductCode())
                return;

            SetConfiguration(true, true);
            StartMQTT();
        }


        private void MqttClient_SubscribeListener(string message)
        {
            Dispatcher.Invoke(new Action(delegate {
                SetConfiguration(message.Equals(SystemEnv.UPDATE_SETTING_INFO),
                    message.Equals(SystemEnv.UPDATE_IMAGE_RESOURCE));
            }), DispatcherPriority.Normal);
        }


        private async void StartMQTT()
        {
            bool connected = await mqttClient.Connect();
            if (connected)
            {
                mqttClient.StartSubscribe();
                mqttClient.SubscribeListener += MqttClient_SubscribeListener;
            }
        }

        private async void SetConfiguration(bool isSetting, bool isImageResource)
        {
            Dp_Image.Stop();
            Pg_Loading.Visibility = Visibility.Visible;

            if (isImageResource)
            {
                Pg_Loading.SetMessage("이미지 정보를 가져옵니다.");
                List<ImageInfo> sources = await resourceCore.GetImageSources();
                Dp_Image.SetImageResources(sources);
            }

            if (isSetting)
            {
                Pg_Loading.SetMessage("설정 정보를 가져옵니다.");
                DeviceInfo settingData = await resourceCore.GetDeviceOptions();
                if (settingData != null)
                {                  
                    Dp_Image.SetOutputTime(int.Parse(settingData.GetDisplayTime()));
                }
            }

            Pg_Loading.Visibility = Visibility.Collapsed;
            Dp_Image.Start(isImageResource);
        }

        private bool CheckProductCode()
        {
            if (SystemEnv.GetProductKey() == "0")
            {
                RegisterKeyDialog registerView = new RegisterKeyDialog();
                registerView.Owner = this;
                Nullable<bool> result = registerView.ShowDialog();
                return result.Value;
            }
            return true;
        }
    }
}
