package win.delin.idcards;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import win.delin.idcards.Exception.IdCardCheckCodeErrorException;
import win.delin.idcards.Exception.IdCardException;
import win.delin.lunar.Lunar;
import win.delin.lunar.LunarSolarConverter;
import win.delin.lunar.Solar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author delin
 * @date 2020/1/14
 */
public class IdCardUtils {
    /**
     * 存放地区数据
     */
    public static JSONObject locationDatas;

    /**
     * 中国公民身份证号码最小长度。
     */
    private static final int CHINA_ID_MIN_LENGTH = 15;

    /**
     * 中国公民身份证号码最大长度。
     */
    private static final int CHINA_ID_MAX_LENGTH = 18;

    /**
     * 每位加权因子
     */
    private static final int POWER[] = {
            7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2
    };

    /**
     * 第18位校检码
     */
    private static final String VERIFY_CODE[] = {
            "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"
    };

    /**
     * 省、直辖市代码表
     */
    private static final String PROVINCE_CODE[] = {
            "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41",
            "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
            "81", "82", "91"
    };

    /**
     * 最低年限
     */
    private static final int MIN = 1930;

    /**
     * 天干
     */
    private final static String[] TIAN_GAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    /**
     * 地支
     */
    private final static String[] DI_ZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    public static final String[] ZODIAC_ARRAY = {"猴", "鸡", "狗", "猪", "鼠", "牛",
            "虎", "兔", "龙", "蛇", "马", "羊"};

    public static final String[] CONSTELLATION_ARRAY = { "摩羯座",
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
            "天蝎座", "射手座", "摩羯座" };

    public static final int[] CONSTELLATION_EDGE_DAY = { 20, 19, 21, 20, 21, 22, 23,
            23, 23, 24, 23, 22 };

    /**
     * 初始化地区信息并将省代码对应单独保存
     */
    private static Map<String, String> provinceCodes = new HashMap<String, String>();

    static {
        locationDatas = readLocationFile("data_location.json");
        for (Map.Entry<String, Object> locationData : locationDatas.entrySet()) {
            if ("0000".equals(locationData.getKey().substring(2, 6))) {
                provinceCodes.put(locationData.getKey().substring(0, 2), locationData.getValue().toString());
            }
        }
    }

    /**
     * 通过idCardNum获得
     * @param idCardNum
     * @return
     */
    public static IdCard getInformation(String idCardNum) throws IdCardException{
        return getInformation(idCardNum, false);
    }

    /**
     * 通过idCardNum获得
     * @param idCardNum
     * @param lunar
     * @return
     */
    public static IdCard getInformation(String idCardNum, boolean lunar) throws IdCardException{
        validateCard(idCardNum);
        IdCard idCard = new IdCard(idCardNum);
        idCard.setArea(getArea(idCardNum));
        idCard.setProvince(getProvice(idCardNum));
        idCard.setCity(getCity(idCardNum));
        idCard.setCounty(getCounty(idCardNum));
        idCard.setGender(getGenderByIdCard(idCardNum));
        idCard.setAge(getAgeByIdCard(idCardNum, lunar));
        idCard.setZodiac(getZodica(idCardNum, lunar));
        idCard.setConstellation(getConstellation(idCardNum, lunar));
        idCard.setBirthday(getBirthByIdCardStr(idCardNum, lunar));
        idCard.setGanZhi(getGanZhiByIdCard(idCardNum, lunar));
        return idCard;
    }

    /**
     * 所在省
     *
     * @param idCardNum
     * @return
     */
    public static String getProvice(String idCardNum) {
        return locationDatas.getString(idCardNum.substring(0, 2) + "0000");
    }

    /**
     * 所在市
     *
     * @param idCardNum
     * @return
     */
    public static String getCity(String idCardNum) {
        return locationDatas.getString(idCardNum.substring(0, 4) + "00");
    }

    /**
     * 所在县
     *
     * @param idCardNum
     * @return
     */
    public static String getCounty(String idCardNum) {
        return locationDatas.getString(idCardNum.substring(0, 6));
    }

    /**
     * 所在全地区
     *
     * @param idCardNum
     * @return
     */
    public static String getArea(String idCardNum) {
        return getProvice(idCardNum) + getCity(idCardNum) + getCounty(idCardNum);
    }

    /**
     * 根据身份编号获取性别
     *
     * @param idCardNum 身份编号
     * @return 性别(M - 男 ， F - 女 ， N - 未知)
     */
    public static String getGenderByIdCard(String idCardNum) {
        String sGender = "N";
        if (idCardNum.length() == CHINA_ID_MIN_LENGTH) {
            idCardNum = conver15CardTo18(idCardNum);
        }
        String sCardNum = idCardNum.substring(16, 17);
        if (Integer.parseInt(sCardNum) % 2 != 0) {
            sGender = "M";
        } else {
            sGender = "F";
        }
        return sGender;
    }

    /**
     * 根据身份编号获取年龄
     *
     * @param idCard 身份编号
     * @return 年龄
     */
    public static int getAgeByIdCard(String idCard, boolean isLunar) {
        int iAge = 0;
        if (idCard.length() == CHINA_ID_MIN_LENGTH) {
            idCard = conver15CardTo18(idCard);
        }
        Calendar birthCal = getBirthByIdCard(idCard, isLunar);
        Calendar cal = Calendar.getInstance();
        int iCurrYear = cal.get(Calendar.YEAR);
        iAge = iCurrYear - birthCal.get(Calendar.YEAR);
        return iAge;
    }

    public static String getGanZhiByIdCard(String idCard, boolean isLunar) {
        if (idCard.length() == CHINA_ID_MIN_LENGTH) {
            idCard = conver15CardTo18(idCard);
        }
        Calendar birthCal = getLunarBirthByIdCard(idCard, isLunar);
        return LunarSolarConverter.lunarYearToGanZhi(birthCal.get(Calendar.YEAR));
    }

    /**
     * 根据身份编号获取生日
     *
     * @param idCard 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static String getBirthByIdCardStr(String idCard, boolean isLunar) {
        Calendar cal = getBirthByIdCard(idCard, isLunar);
        return String.format("%s-%s-%s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar getBirthByIdCard(String idCard, boolean isLunar) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idCard = conver15CardTo18(idCard);
        }
        int year = Integer.valueOf(idCard.substring(6, 10));
        int month = Integer.valueOf(idCard.substring(10, 12));
        int day = Integer.valueOf(idCard.substring(12, 14));

        if (isLunar) {
            Lunar lunar = new Lunar();
            lunar.lunarYear = year;
            lunar.lunarMonth = month;
            lunar.lunarDay = day;
            Solar solar = LunarSolarConverter.LunarToSolar(lunar);
            year = solar.solarYear;
            month = solar.solarMonth;
            day = solar.solarDay;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }


    public static Calendar getLunarBirthByIdCard(String idCard, boolean isLunar) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idCard = conver15CardTo18(idCard);
        }
        int year = Integer.valueOf(idCard.substring(6, 10));
        int month = Integer.valueOf(idCard.substring(10, 12));
        int day = Integer.valueOf(idCard.substring(12, 14));

        if (!isLunar) {
            Solar solar = new Solar();
            solar.solarYear = year;
            solar.solarMonth = month;
            solar.solarDay = day;
            Lunar lunar = LunarSolarConverter.SolarToLunar(solar);
            year = lunar.lunarYear;
            month = lunar.lunarMonth;
            day = lunar.lunarDay;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }

    /**
     * 根据日期获取生肖
     *
     * @return
     */
    public static String getZodica(Calendar time) {
        return ZODIAC_ARRAY[time.get(Calendar.YEAR) % 12];
    }

    /**
     * 根据身份证号码获取属相
     * @param idCard
     * @param lunar
     * @return
     */
    public static String getZodica(String idCard, boolean lunar) {
        return getZodica(getBirthByIdCard(idCard, lunar));
    }

    public static String getZodica(String idCard) {
        return getZodica(getZodica(idCard, false));
    }

    /**
     * 根据日期获取星座
     *
     * @param time
     * @return
     */
    public static String getConstellation(Calendar time) {
        int month = time.get(Calendar.MONTH);
        int day = time.get(Calendar.DAY_OF_MONTH);
        return day < CONSTELLATION_EDGE_DAY[month - 1] ? CONSTELLATION_ARRAY[month - 1]
                : CONSTELLATION_ARRAY[month];
    }

    /**
     * 根据身份证号码获取星座
     * @param idCard
     * @param lunar
     * @return
     */
    public static String getConstellation(String idCard, boolean lunar) {
        return getConstellation(getBirthByIdCard(idCard, lunar));
    }

    public static String getConstellation(String idCard) {
        return getConstellation(idCard, false);
    }


    /**
     * 根据身份编号获取生日年
     *
     * @param idCard 身份编号
     * @return 生日(yyyy)
     */
    public static Short getYearByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idCard = conver15CardTo18(idCard);
        }
        return Short.valueOf(idCard.substring(6, 10));
    }

    /**
     * 根据身份编号获取生日月
     *
     * @param idCard 身份编号
     * @return 生日(MM)
     */
    public static Short getMonthByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idCard = conver15CardTo18(idCard);
        }
        return Short.valueOf(idCard.substring(10, 12));
    }

    /**
     * 根据身份编号获取生日天
     *
     * @param idCard 身份编号
     * @return 生日(dd)
     */
    public static Short getDateByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idCard = conver15CardTo18(idCard);
        }
        return Short.valueOf(idCard.substring(12, 14));
    }

    /**
     * 验证身份证是否合法
     */
    public static boolean validateCard(String idCardNum) throws IdCardException{
        String card = idCardNum.trim();
        if (validateIdCard18(card)) {
            return true;
        }
        if (validateIdCard15(card)) {
            return true;
        }
        return false;
    }

    /**
     * 验证18位身份编码是否合法
     *
     * @param idCardNum 身份编码
     * @return 是否合法
     */
    public static boolean validateIdCard18(String idCardNum) throws IdCardCheckCodeErrorException {
        boolean bTrue = false;
        if (idCardNum.length() == CHINA_ID_MAX_LENGTH) {
            // 前17位
            String code17 = idCardNum.substring(0, 17);
            // 第18位
            String code18 = idCardNum.substring(17, CHINA_ID_MAX_LENGTH);
            if (isNum(code17)) {
                char[] cArr = code17.toCharArray();
                if (cArr != null) {
                    int[] iCard = converCharToInt(cArr);
                    int iSum17 = getPowerSum(iCard);
                    // 获取校验位
                    String val = getCheckCode18(iSum17);
                    if (val.length() > 0) {
                        if (val.equalsIgnoreCase(code18)) {
                            bTrue = true;
                        } else {
                            throw new IdCardCheckCodeErrorException(idCardNum, val);
                        }
                    }
                }
            }
        }
        return bTrue;
    }

    /**
     * 将15位身份证号码转换为18位
     *
     * @param idCardNum 15位身份编码
     * @return 18位身份编码
     */
    public static String conver15CardTo18(String idCardNum) {
        String idCard18 = "";
        if (idCardNum.length() != CHINA_ID_MIN_LENGTH) {
            return null;
        }
        if (isNum(idCardNum)) {
            // 获取出生年月日
            String birthday = idCardNum.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            if (birthDate != null) {
                cal.setTime(birthDate);
            }
            // 获取出生年(完全表现形式,如：2010)
            String sYear = String.valueOf(cal.get(Calendar.YEAR));
            idCard18 = idCardNum.substring(0, 6) + sYear + idCardNum.substring(8);
            // 转换字符数组
            char[] cArr = idCard18.toCharArray();
            if (cArr != null) {
                int[] iCard = converCharToInt(cArr);
                int iSum17 = getPowerSum(iCard);
                // 获取校验位
                String sVal = getCheckCode18(iSum17);
                if (sVal.length() > 0) {
                    idCard18 += sVal;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
        return idCard18;
    }


    /**
     * 验证15位身份编码是否合法
     *
     * @param idCardNum 身份编码
     * @return 是否合法
     */
    public static boolean validateIdCard15(String idCardNum) {
        if (idCardNum.length() != CHINA_ID_MIN_LENGTH) {
            return false;
        }
        if (isNum(idCardNum)) {
            String proCode = idCardNum.substring(0, 2);
            if (provinceCodes.get(proCode) == null) {
                return false;
            }
            String birthCode = idCardNum.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yy").parse(birthCode.substring(0, 2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            if (birthDate != null) {
                cal.setTime(birthDate);
            }
            if (!valiDate(cal.get(Calendar.YEAR), Integer.valueOf(birthCode.substring(2, 4)),
                    Integer.valueOf(birthCode.substring(4, 6)))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 验证小于当前日期 是否有效
     *
     * @param iYear  待验证日期(年)
     * @param iMonth 待验证日期(月 1-12)
     * @param iDate  待验证日期(日)
     * @return 是否有效
     */
    public static boolean valiDate(int iYear, int iMonth, int iDate) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int datePerMonth;
        if (iYear < MIN || iYear >= year) {
            return false;
        }
        if (iMonth < 1 || iMonth > 12) {
            return false;
        }
        switch (iMonth) {
            case 4:
            case 6:
            case 9:
            case 11:
                datePerMonth = 30;
                break;
            case 2:
                boolean dm = ((iYear % 4 == 0 && iYear % 100 != 0) || (iYear % 400 == 0))
                        && (iYear > MIN && iYear < year);
                datePerMonth = dm ? 29 : 28;
                break;
            default:
                datePerMonth = 31;
        }
        return (iDate >= 1) && (iDate <= datePerMonth);
    }

    /**
     * 数字验证
     *
     * @param val
     * @return 提取的数字。
     */
    public static boolean isNum(String val) {
        return val == null || "".equals(val) ? false : val.matches("^[0-9]*$");
    }

    /**
     * 将字符数组转换成数字数组
     *
     * @param ca 字符数组
     * @return 数字数组
     */
    public static int[] converCharToInt(char[] ca) {
        int len = ca.length;
        int[] iArr = new int[len];
        try {
            for (int i = 0; i < len; i++) {
                iArr[i] = Integer.parseInt(String.valueOf(ca[i]));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iArr;
    }


    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param iArr
     * @return 身份证编码。
     */
    public static int getPowerSum(int[] iArr) {
        int iSum = 0;
        if (POWER.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                for (int j = 0; j < POWER.length; j++) {
                    if (i == j) {
                        iSum = iSum + iArr[i] * POWER[j];
                    }
                }
            }
        }
        return iSum;
    }

    /**
     * 将power和值与11取模获得余数进行校验码判断
     *
     * @param iSum
     * @return 校验位
     */
    public static String getCheckCode18(int iSum) {
        String sCode = "";
        switch (iSum % 11) {
            case 10:
                sCode = "2";
                break;
            case 9:
                sCode = "3";
                break;
            case 8:
                sCode = "4";
                break;
            case 7:
                sCode = "5";
                break;
            case 6:
                sCode = "6";
                break;
            case 5:
                sCode = "7";
                break;
            case 4:
                sCode = "8";
                break;
            case 3:
                sCode = "9";
                break;
            case 2:
                sCode = "x";
                break;
            case 1:
                sCode = "0";
                break;
            case 0:
                sCode = "1";
                break;
        }
        return sCode;
    }

    /**
     * 文件读取
     *
     * @param path
     * @return
     */
    private static String readFile(String path) {
        BufferedReader reader;
        String laststr = "";
        try (
                FileInputStream fileInputStream = new FileInputStream(ClassLoader.getSystemResource(path).getFile());
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8")
        ) {
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return laststr;
    }

    /**
     * 获得json序列化地区信息
     *
     * @param path
     * @return
     */
    private static JSONObject readLocationFile(String path) {
        JSONObject dataLocations = JSON.parseObject(readFile(path));
        return dataLocations;
    }
}
