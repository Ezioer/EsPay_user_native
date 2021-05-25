package com.easou.androidsdk.login.service;

public class LUser implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3853138146789650522L;
    private Long id; // id
    private String name; // 用户名
    private String nickName; // 用户昵称
    private String mobile; // 手机号
    private Integer status; // 状态，0被锁定, 1正常
    private Integer sex; // 性别
    private Integer occuId; // 职业id
    private Integer birthday; // 出生年月(数值)
    private String city; // 城市
    private int isAutoRegist;
    private String passwd;    // 用户密码
    private String identityName;
    private String identityNum;
    private int identityStatus;//0认证中，1成功，2失败【未认证】，3未认证，
    private int thirdPartySsoId;
    private String openId;

    public int getThirdPartySsoId() {
        return thirdPartySsoId;
    }

    public void setThirdPartySsoId(int thirdPartySsoId) {
        this.thirdPartySsoId = thirdPartySsoId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getIdentityStatus() {
        return identityStatus;
    }

    public void setIdentityStatus(int identityStatus) {
        this.identityStatus = identityStatus;
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public String getIdentityNum() {
        return identityNum;
    }

    public void setIdentityNum(String identityNum) {
        this.identityNum = identityNum;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getOccuId() {
        return occuId;
    }

    public void setOccuId(Integer occuId) {
        this.occuId = occuId;
    }

    public Integer getBirthday() {
        return birthday;
    }

    public void setBirthday(Integer birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIsAutoRegist() {
        return isAutoRegist;
    }

    public void setIsAutoRegist(int isAutoRegist) {
        this.isAutoRegist = isAutoRegist;
    }
}
