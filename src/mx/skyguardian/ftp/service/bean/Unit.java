package mx.skyguardian.ftp.service.bean;

import java.util.Map;


public class Unit {
	private String unitId;
	private String date;
	private String time;
	private String currentKm;
	
	public Unit(){
		
	}
	
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
	
	@SuppressWarnings("rawtypes")
	public void setUnitId(Map unitId) {		
		this.unitId = (unitId.get("nm") != null ? unitId.get("nm").toString() : "Unidad desconocida");
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
	
	@SuppressWarnings("rawtypes")
	public void setCurrentKm(Map currentKm) {
		String cKm = "Cuenta de odómetro desconocida";
		
		if (currentKm.get("prms") != null && currentKm.get("prms") instanceof Map) {
			Map prms = (Map) currentKm.get("prms"); 
			if (prms.get("odometer") != null && prms.get("odometer") instanceof Map) {
				Map odometer = ((Map)prms.get("odometer"));
				cKm = odometer.get("v") != null ?
						odometer.get("v").toString() :
							"Cuenta de odómetro desconocida";
			}
		}
		
		this.currentKm = cKm;
	}
	
	
}
