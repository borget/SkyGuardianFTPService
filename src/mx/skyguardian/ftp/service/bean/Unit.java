package mx.skyguardian.ftp.service.bean;


public class Unit {
	private String unitId;
	private String date;
	private String time;
	private String currentKm;
	
	public Unit(String unitId, String date, String time, String currentKm) {
		super();
		this.unitId = unitId;
		this.date = date;
		this.time = time;
		this.currentKm = currentKm;
	}
	
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCurrentKm() {
		return currentKm;
	}
	public void setCurrentKm(String currentKm) {
		this.currentKm = currentKm;
	}
	
	
}
