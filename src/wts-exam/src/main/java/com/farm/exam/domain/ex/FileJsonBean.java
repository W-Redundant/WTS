package com.farm.exam.domain.ex;

import com.farm.doc.domain.Docfile;

public class FileJsonBean implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2165169049243720411L;
	private Docfile info;
	private String base64;

	public Docfile getInfo() {
		return info;
	}

	public void setInfo(Docfile info) {
		this.info = info;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}
}
