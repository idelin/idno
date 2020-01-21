## idno(IDCardNum)
身份证号码校验及信息获取工具。

用于校验身份证合法性，用于通过身份证号码获取地区（省市县）、生日、年龄、干支、属相、星座、性别。

The tool is used to verify the validity of the ID card, and is used to obtain the region (province, city, county), birthday, age, ganzhi, genus, horoscope, and gender through the ID card number.

#### 1. 校验
`IdCardUtils.validateCard(idCard)`

校验码异常：如果是因为校验码错误导致的异常，会指明正确校验码。
```
win.delin.idcards.Exception.IdCardCheckCodeErrorException: cardNo is [220202202002022222], realCheckCode must [0]
	at win.delin.idcards.IdCardUtils.validateIdCard18(IdCardUtils.java:423)
	at win.delin.idcards.IdCardUtils.validateCard(IdCardUtils.java:390)
	at win.delin.idcards.IdCardUtils.getInformation(IdCardUtils.java:119)
	at win.delin.idcards.IdCardUtils.getInformation(IdCardUtils.java:109)
	at win.delin.idcards.IdCardUtilsTest.testGetInformation(IdCardUtilsTest.java:23)
```

#### 2. 获取信息
```java
//通过idcard获取信息
IdCard idCard1 = IdCardUtils.getInformation(idCard);
//通过idcard且为农历生日获取信息
IdCard idCard2 = IdCardUtils.getInformation(idCard, true);
```

结果栗子🌰：
```
IdCard{idCardNum='220202202002022220', area='吉林省吉林市昌邑区', province='吉林省', city='吉林市', county='昌邑区', birthday='2020-2-2', ganZhi='庚子年', age=0, zodiac='鼠', constellation='水瓶座', gender='F'}
IdCard{idCardNum='220202202002022220', area='吉林省吉林市昌邑区', province='吉林省', city='吉林市', county='昌邑区', birthday='2020-2-24', ganZhi='庚子年', age=0, zodiac='鼠', constellation='双鱼座', gender='F'}
```

IdCard对象信息说明：
```java
String idCardNum;
// 地区
String area;
// 省
String province;
// 市
String city;
// 县
String county;
// 生日(格式 => Y-M-D)
String birthday;
// 生日天干地支
String ganZhi;
// 年龄
int age;
// 属相
String zodiac;
// 星座
String constellation;
// 性别
String gender;
```
