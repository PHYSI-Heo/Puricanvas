using Newtonsoft.Json.Linq;
using PAC_24Frame.Http;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PAC_24Frame
{
    class ProductKeyManager
    {
        private string productCode;

        public ProductKeyManager()
        {
        }

        private string GetRandomCode(int length)
        {
            Random rand = new Random();
            string input = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            var chars = Enumerable.Range(0, length).Select(x => input[rand.Next(0, input.Length)]);
            return new string(chars.ToArray());
        }

        private bool IsCreateProductKey()
        {
            bool isExists =  File.Exists(SystemEnv.DEVICE_ID_FILE);
            if (isExists)
                productCode = File.ReadAllText(SystemEnv.DEVICE_ID_FILE);
            return isExists;
        }

        public async Task<string> GetProductKey()
        {
            if (!IsCreateProductKey())
            {
                await RequestRegisterKey();
            }
            return productCode;
        }


       /* private async Task<bool> RequestExistsKey(string code)
        {
            try
            {
                JObject paramObj = new JObject();
                paramObj.Add(HTTPPacket.Param_Device_ID, code);
                JObject obj = await httpClient.Requester(HTTPPacket.Url_Exists_ID, "POST", paramObj);

                if (obj != null)
                {
                    string resCode = obj.GetValue(HTTPPacket.Param_Res_Result).ToString();
                    bool isExists = Boolean.Parse(obj.GetValue(HTTPPacket.Param_Key_Exist).ToString());
                    Console.WriteLine("(Product) Exist result = {0}", obj.ToString());
                    return resCode.Equals(HTTPPacket.Res_Success) && !isExists;
                }
                else
                {
                    return false;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return false;
            }
        }*/


        private async Task<bool> RequestRegisterKey()
        {
            try
            {
                string code = GetRandomCode(6);
                JObject paramObj = new JObject();
                paramObj.Add(HTTPPacket.Param_Device_ID, code);
                JObject obj = await new HTTPClient().Requester(HTTPPacket.Url_Register_ID, "POST", paramObj);

                if (obj != null)
                {
                    string resCode = obj.GetValue(HTTPPacket.Param_Res_Result).ToString();
                    Console.WriteLine("(Product) Register result = {0}", obj.ToString());
                    if (resCode.Equals(HTTPPacket.Res_Success))
                    {                       
                        File.WriteAllText(SystemEnv.DEVICE_ID_FILE, code);
                        productCode = code;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return false;
            }
        }

    }
}
