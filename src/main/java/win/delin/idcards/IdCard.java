package win.delin.idcards;

/**
 * @author delin
 * @date 2020/1/14
 */
public class IdCard {
    String idCardNum;
    // 地区
    String area;
    // 省
    String province;
    // 市
    String city;
    // 县
    String county;
    // 生日
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

    public IdCard(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGanZhi() {
        return ganZhi;
    }

    public void setGanZhi(String ganZhi) {
        this.ganZhi = ganZhi;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IdCard{");
        sb.append("idCardNum='").append(idCardNum).append('\'');
        sb.append(", area='").append(area).append('\'');
        sb.append(", province='").append(province).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", county='").append(county).append('\'');
        sb.append(", birthday='").append(birthday).append('\'');
        sb.append(", ganZhi='").append(ganZhi).append('\'');
        sb.append(", age=").append(age);
        sb.append(", zodiac='").append(zodiac).append('\'');
        sb.append(", constellation='").append(constellation).append('\'');
        sb.append(", gender='").append(gender).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
