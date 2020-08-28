using Newtonsoft.Json.Linq;
using PAC_7Frame.Http;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PAC_7Frame.Display
{
    public class ResourceCore
    {
        private const int dummyDelay = 1000;
        private HTTPClient httpClient;

        public ResourceCore()
        {
            httpClient = new HTTPClient();
            SystemEnv.InitResourceFolder();
        }

        public async Task<DeviceInfo> GetDeviceOptions()
        {
            await Task.Delay(dummyDelay);
            DeviceInfo data = null;
            JObject infoObj = await RequestDeviceInfo();
            if (infoObj != null)
            {
                data = new DeviceInfo(
                            infoObj.GetValue(HTTPPacket.Param_City).ToString(),
                            infoObj.GetValue(HTTPPacket.Param_Province).ToString(),
                            infoObj.GetValue(HTTPPacket.Param_Location_Lat).ToString(),
                            infoObj.GetValue(HTTPPacket.Param_Location_Lon).ToString(),
                            infoObj.GetValue(HTTPPacket.Param_Display_Time).ToString());
            }
            return data;
        }

        private async Task<JObject> RequestDeviceInfo()
        {
            JObject infoObj = null;
            try
            {
                JObject paramObj = new JObject();
                paramObj.Add(HTTPPacket.Param_Device_ID, SystemEnv.GetProductKey());
                JObject obj = await httpClient.Requester(HTTPPacket.Url_Get_Info, "POST", paramObj);
                if (obj != null)
                {
                    string resCode = obj.GetValue(HTTPPacket.Param_Res_Result).ToString();
                    Console.WriteLine("(Display) Device options = {0}", obj.ToString());
                    if (resCode.Equals(HTTPPacket.Res_Success))
                    {
                        JArray arrayObj = JArray.Parse(obj.GetValue(HTTPPacket.Param_Res_Rows).ToString());
                        infoObj = JObject.FromObject(arrayObj[0]);
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
            return infoObj;
        }


        /*
         * 이미지.영상 출력 정보 설정
         * - 고유 id로 등록된 리소스 정보 조회
         * - 사용자 설정 여부에 따라 디폴트/다운로드 폴더에 해당 파일 존재 여부 확인
         * - 로컬 폴더에 해당 파일이 존재하지 않을 경우 다운로드 진행
         */
        public async Task<List<ImageInfo>> GetImageSources()
        {
            await Task.Delay(dummyDelay);

            List<ImageInfo> setupIMGs = await RequestImageInfo();
            await DownloadIMGFiles(setupIMGs);

            foreach (ImageInfo info in setupIMGs)
            {
                info.SetConvertSource();
            }

            return setupIMGs;
        }

        private async Task<List<ImageInfo>> RequestImageInfo()
        {
            List<ImageInfo> imgs = new List<ImageInfo>();
            try
            {
                JObject paramObj = new JObject();
                paramObj.Add(HTTPPacket.Param_Device_ID, SystemEnv.GetProductKey());
                JObject obj = await httpClient.Requester(HTTPPacket.Url_Get_IMGs, "POST", paramObj);
                if (obj != null)
                {
                    string resCode = obj.GetValue(HTTPPacket.Param_Res_Result).ToString();
                    Console.WriteLine("(Display) Image resource = {0}", obj.ToString());
                    if (resCode.Equals(HTTPPacket.Res_Success))
                    {
                        JArray arrayObj = JArray.Parse(obj.GetValue(HTTPPacket.Param_Res_Rows).ToString());
                        foreach (JObject imgObj in arrayObj.Children<JObject>())
                        {
                            imgs.Add(new ImageInfo(
                                 imgObj.GetValue(HTTPPacket.Param_IMG_Order).ToString(),
                                 imgObj.GetValue(HTTPPacket.Param_User_File).ToString(),
                                 imgObj.GetValue(HTTPPacket.Param_IMG_Name).ToString()
                                ));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
            return imgs;
        }

        private async Task DownloadIMGFiles(List<ImageInfo> imgs)
        {
            // get local files
            List<string> downlaodFiles = new List<string>();
            List<string> defaultFiles = new List<string>();
            foreach (FileInfo info in new DirectoryInfo(SystemEnv.DownloadFolderPath).GetFiles())
            {
                downlaodFiles.Add(info.Name);
            }
            foreach (FileInfo info in new DirectoryInfo(SystemEnv.DefaultFolderPath).GetFiles())
            {
                defaultFiles.Add(info.Name);
            }

            foreach (ImageInfo info in imgs)
            {
                if (info.IsUserFile())
                {
                    if (!downlaodFiles.Contains(info.GetFileName()))
                    {
                        string fileUrl = new StringBuilder()
                          .AppendFormat(@"{0}/{1}/{2}/download", HTTPPacket.Url_IMG_Download, SystemEnv.GetProductKey(), info.GetFileName())
                          .ToString();
                        string filePath = SystemEnv.DownloadFolderPath + @"/" + info.GetFileName();
                        Console.WriteLine("(Display) Start download file = {0}", fileUrl);
                        await httpClient.DownloadFile(new Uri(fileUrl), filePath);
                    }
                }
                else
                {
                    if (!defaultFiles.Contains(info.GetFileName()))
                    {
                        string fileUrl = new StringBuilder()
                            .AppendFormat(@"{0}/{1}/{2}/download", HTTPPacket.Url_IMG_Download, "default", info.GetFileName())
                            .ToString();
                        string filePath = SystemEnv.DefaultFolderPath + @"/" + info.GetFileName();
                        Console.WriteLine("(Display) Start download file = {0}", fileUrl);
                        await httpClient.DownloadFile(new Uri(fileUrl), filePath);
                    }
                }
            }
        }


    }
}
