package com.example.obdiibtsim;

public class PidInfo {
	private String name;
	private String value;
	private boolean type;
	private boolean checked = false;
	
	public PidInfo(String name, String value, boolean type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}
	
	public void setCheck(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isChecked() {
		return checked;
	}	
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	public boolean isSupport() {
		return type;
	}
}
