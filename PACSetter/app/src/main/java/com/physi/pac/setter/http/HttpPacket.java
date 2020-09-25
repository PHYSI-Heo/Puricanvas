package com.physi.pac.setter.http;

public class HttpPacket {

//    public static final String BASEURL = "http://192.168.1.12:3000";
    public static final String BASEURL = "http://13.124.176.173:3000";

    /*
         API
     */
    public static final String REGISTER_ID_URL = BASEURL + "/device/register/code";
    public static final String EXIST_ID_URL = BASEURL + "/device/exist/code";
    public static final String GET_INFO_URL = BASEURL + "/device/get/options";
    public static final String RESET_INFO_URL = BASEURL + "/device/reset";
    public static final String UPDATE_INFO_URL = BASEURL + "/device/update/options";
    public static final String GET_IMGs_URL = BASEURL + "/device/get/imgs";
    public static final String UPDATE_IMGs_URL = BASEURL + "/device/update/imgs";
    public static final String GET_BASIC_IMGs = BASEURL + "/device/get/basic/imgs";

    public static final String UPLOAD_IMGs_URL = BASEURL + "/image/upload";

    /*
        # Params
     */
    public static final String PARAMS_RESULT = "result";
    public static final String PARAMS_ROWS = "rows";
    public static final String PARAMS_ID_EXIST = "exist";

    public static final String PARAMS_DEVICE_ID = "did";
    public static final String PARAMS_CITY = "city";
    public static final String PARAMS_PROVINCE = "province";
    public static final String PARAMS_LOCATION_LAT = "lat";
    public static final String PARAMS_LOCATION_LON = "lon";
    public static final String PARAMS_DISPLAY_TIME = "playtime";

    public static final String PARAMS_IMG_ORDER = "_order";
    public static final String PARAMS_USER_FILE = "usf";
    public static final String PARAMS_IMG_FILE_NAME = "filename";


}
