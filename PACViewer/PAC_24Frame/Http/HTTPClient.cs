using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace PAC_24Frame.Http
{
    public class HTTPClient
    {
        public async Task<JObject> Requester(string url, string method, JObject param)
        {
            JObject resultObj = null;
            try
            {
                var webRequest = (HttpWebRequest)WebRequest.Create(url);
                webRequest.ContentType = "application/json";
                webRequest.Timeout = 5000;
                webRequest.Method = method;

                if (param != null)
                {
                    using (var streamWriter = new StreamWriter(webRequest.GetRequestStream()))
                    {
                        streamWriter.Write(param);
                    }
                }

                HttpWebResponse httpResponse = (HttpWebResponse)await webRequest.GetResponseAsync();
                using (var streamReader = new StreamReader(httpResponse.GetResponseStream()))
                {
                    resultObj = JObject.Parse(streamReader.ReadToEnd());
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Http Err = {0}",e.Message);
            }

            return resultObj;
        }



        public async Task<bool> DownloadFile(Uri uri, string saveFilePath)
        {
            try
            {
                using (var client = new WebClient())
                {
                    await client.DownloadFileTaskAsync(uri, saveFilePath);
                    return true;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Http Err = {0}", e.Message);
                File.Delete(saveFilePath);
                return false;
            }
        }
    }
}
