using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PAC_7Frame.Display
{
    public class ImageInfo
    {
        public const int TYPE_PICTURE = 1;
        public const int TYPE_VIDEO = 2;

        private string no, usfState, fileName;
        private Uri uri;
        private int type;

        public ImageInfo(string no, string usfState, string fileName)
        {
            this.no = no;
            this.usfState = usfState;
            this.fileName = fileName;
        }

        public void SetConvertSource()
        {
            string fileType = fileName.Substring(fileName.LastIndexOf("."));
            this.type = SystemEnv.IMG_TYPEs.Contains(fileType) ? TYPE_PICTURE : TYPE_VIDEO;
            string rootFolder = usfState.Equals("1") ? SystemEnv.DownloadFolderPath : SystemEnv.DefaultFolderPath;
            this.uri = new Uri(rootFolder + @"/" + fileName, UriKind.Absolute);
        }

        public bool IsUserFile()
        {
            return usfState.Equals("1");
        }

        public string GetFileName()
        {
            return fileName;
        }

        public Uri GetUri()
        {
            return uri;
        }

        public int GetFileType()
        {
            return type;
        }
    }
}
