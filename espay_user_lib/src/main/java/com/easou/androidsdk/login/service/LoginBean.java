package com.easou.androidsdk.login.service;


/**
 * 鉴权返回结果
 * 
 * @author jay
 * @since 2013.01.27
 * @version 1.0
 *
 */
public class LoginBean implements java.io.Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 4782288831244671067L;

	private EucToken token;
	/*U信息*/
	private EucUCookie u;
	/*是否登录的注册方式*/
	private boolean isRegist;
	/*返回ESID*/
	private String esid;
	/*返回的用户实体*/
	private LUser user;

	public EucUCookie getU() {
		return u;
	}

	public void setU(EucUCookie u) {
		this.u = u;
	}

	public boolean isRegist() {
		return isRegist;
	}

	public void setRegist(boolean isRegist) {
		this.isRegist = isRegist;
	}

	public String getEsid() {
		return esid;
	}

	public void setEsid(String esid) {
		this.esid = esid;
	}

	/**
	 * 用来提供给Gson进行反射加载
	 * @author ted
	 * @since 2013-05-07
	 */
	public LoginBean(){
	}


	/**
	 * 构造函数
	 *
	 * @param jUser
	 * @param token
	 * @param esid
	 * @param isRegist
	 *     注册登录标识
	 */
	public LoginBean(EucToken token, LUser user, String esid, boolean isRegist){
		this.token = token;
		this.user = user;
		this.esid = esid;
		this.isRegist = isRegist;
	}


	/**
	 * 构造函数
	 *
	 * @param jUser
	 * @param token
	 * @param esid
	 * @param isRegist
	 *     注册登录标识
	 */
	public LoginBean(EucToken token, LUser user, EucUCookie u, String esid, boolean isRegist){
		this.token = token;
		this.user = user;
		this.u = u;
		this.esid = esid;
		this.isRegist = isRegist;
	}

	/**
	 * 通行证
	 * 
	 * @param token
	 */
	public EucToken getToken() {
		return token;
	}
	
	public void setToken(EucToken token) {
		this.token = token;
	}

	/**
	 * 用户信息
	 */
	public LUser getUser() {
		return user;
	}

	public void setUser(LUser user) {
		this.user = user;
	}
}
