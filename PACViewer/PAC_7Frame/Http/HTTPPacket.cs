using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PAC_7Frame.Http
{
    public class HTTPPacket
    {
        /*
         *      REST API
         */
        //private const string BaseUrl = "http://192.168.1.12:3000";
        private const string BaseUrl = "http://54.180.153.12:3000";

        //  Web Server Url
        public const string Url_Exist_ID = BaseUrl + "/device/exist/code";
        public const string Url_Register_ID = BaseUrl + "/device/register/code";
        public const string Url_Get_Info = BaseUrl + "/device/get/options";
        public const string Url_Get_IMGs = BaseUrl + "/device/get/imgs";

        public const string Url_IMG_Download = BaseUrl + "/image";

        //  Params 
        public const string Param_Res_Result = "result";
        public const string Param_Res_Rows = "rows";
        public const string Param_Key_Exist = "exist";

        public const string Param_Device_ID = "did";
        public const string Param_City = "city";
        public const string Param_Province = "province";
        public const string Param_Location_Lat = "lat";
        public const string Param_Location_Lon = "lon";
        public const string Param_Display_Time = "playtime";

        public const string Param_IMG_Order = "_order";
        public const string Param_User_File = "usf";
        public const string Param_IMG_Name = "filename";

        public const string Res_Success = "1001";

        // Air Quality JSON Key
        public const string Key_Air_Status = "status";
        public const string Key_Air_Data = "data";
        public const string Key_Air_Info = "iaqi";
        public const string Key_Air_PM10 = "pm10";
        public const string Key_Air_PM25 = "pm25";
        public const string Key_Air_Value = "v";
        // OpenWeather JSON Key
        public const string Key_WT_Code = "cod";
        public const string Key_WT_List = "list";
        public const string Key_WT_Main = "main";
        public const string Key_WT_Temp = "temp";
        public const string Key_WT_Humidity = "humidity";
        public const string Key_WT_Weather = "weather";
        public const string Key_WT_Description = "description";
        public const string Key_WT_ID = "id";
    }
}
