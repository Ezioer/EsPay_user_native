package com.easou.androidsdk.sso;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.util.Tools;

/**
 * 票据对象
 * 
 * @author damon
 * @since 2012.07.03
 * @version 1.0
 *
 */
public class EucUCookie implements java.io.Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -3460097573745876956L;
	
	/**值*/
	private String u;
	/**COOKIE域*/
	private String domain = "." + Tools.getHostName();
	/**COOKIE路径*/
	private String path = "/";
	/**失效时间*/
	private int age = 63072000;
	
	
	public String getU() {
		return u;
	}

	public void setU(String u) {
		this.u = u;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
