package mx.skyguardian.ftp.service.ws;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import mx.skyguardian.ftp.service.bean.Unit;
import mx.skyguardian.ftp.service.bean.WialonSession;
import mx.skyguardian.ftp.service.utils.ApplicationUtil;

import org.apache.log4j.Logger;
import org.boon.HTTP;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

public class GurtamHTTPRequestExecutor implements IGurtamHTTPRequestExecutor {
	private static Logger log = Logger.getLogger(GurtamHTTPRequestExecutor.class);
	@Resource(name = "appProperties")
	private Properties appProperties;
	
	public String getHTTPRequest(String urlString) throws Exception {

		InputStream inputStream = null;
		String jsonString = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");

			inputStream = new BufferedInputStream(connection.getInputStream());

			ByteArrayOutputStream dataCache = new ByteArrayOutputStream();

			byte[] buff = new byte[1024];
			int len;
			while ((len = inputStream.read(buff)) >= 0) {
				dataCache.write(buff, 0, len);
			}
			dataCache.close();
			jsonString = new String(dataCache.toByteArray()).trim();
					
		} finally {
			if (null != inputStream) {
				inputStream.close();
			}
		}
		return jsonString;
	}
	
	public WialonSession doLogin(String userName, String password) throws Exception {
		WialonSession wialonSession = null;
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("user", userName);
		properties.put("password", password);
		String loginUrl = ApplicationUtil.getProperty(appProperties.getProperty("mx.skyguardian.ftpservice.login.url"), properties);
		ObjectMapper mapper = JsonFactory.create();
		wialonSession = mapper.readValue(HTTP.getJSON(loginUrl, null), WialonSession.class);
		
		if (wialonSession.getEid() != null && wialonSession.getUser() != null){
			((WialonSession) wialonSession).getUser().setPassword(password);
		} else {
			throw new Exception("Authentication error. Please check the user and password.");
		}
	
		return wialonSession;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Unit> getUnits(String eid, String flags) throws Exception {
		log.debug("GETTING UNITS FROM GURTAM PLATFORM ...");
		List<Unit> units = new ArrayList<Unit>();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("sid", eid);
		properties.put("flags", flags);

		String unitsUrl = ApplicationUtil.getProperty(
				appProperties.getProperty("mx.skyguardian.ftpservice.units.url"),
				properties);
		
		ObjectMapper mapper = JsonFactory.create();
		Map data = mapper.readValue(HTTP.getJSON(unitsUrl, null), Map.class);
		Object items = data.get("items");
		if (items instanceof List) {
			for (Object item : (List)items) {
				if (item instanceof Map) {
					Map mappedUnit = (Map)item;
					units.add(getUnit(mappedUnit));
				}
			}
		} else if (items instanceof Map) {
			Map mappedUnit = (Map) items;
			units.add(getUnit(mappedUnit));
		} else {
			throw new Exception("Exception retrieving units. No units were found.");
		}
		return units;
	}
	
	@SuppressWarnings("rawtypes")
	private Unit getUnit(Map mappedUnit){
		Unit unit = new Unit();
		unit.setUnitId(mappedUnit);
		unit.setCurrentKm(mappedUnit);
		
		GregorianCalendar dateTime = getDatetimeOfUnitPosition(mappedUnit);
		
		if (dateTime != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(appProperties.getProperty("mx.skyguardian.ftpservice.dateformat"));
			unit.setDate(dateFormat.format(dateTime.getTime()));
			SimpleDateFormat hourFormat = new SimpleDateFormat(appProperties.getProperty("mx.skyguardian.ftpservice.hourformat"));
			unit.setTime(hourFormat.format(dateTime.getTime()));
		} else {
			unit.setDate("dd/MM/YYYY");
			unit.setTime("00:00:00 AM/PM");
		}		
		return unit;
	}
	
	@SuppressWarnings("rawtypes")
	private GregorianCalendar getDatetimeOfUnitPosition(Map mappedUnit){
		GregorianCalendar dateTime = null;
		if (mappedUnit.get("pos") != null && mappedUnit.get("pos") instanceof Map) {
			Map pos = (Map) mappedUnit.get("pos"); 
			if (pos.get("t") != null) {
				Long t = Long.valueOf(pos.get("t").toString()); 
				dateTime = ApplicationUtil.getDatetimeByTimeZone(appProperties.getProperty("mx.skyguardian.ftpservice.timezone"),t);
			}
		}
		return dateTime;
	}
	
	public Properties getAppProperties() {
		return this.appProperties;
	}

	public void setAppProperties(Properties appProperties) {
		this.appProperties = appProperties;
	}
}
