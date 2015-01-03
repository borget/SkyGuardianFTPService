package mx.skyguardian.ftp.service.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

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
	
	public WialonSession doLogin(String userName, String password) {
		WialonSession wialonSession = null;
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("user", userName);
		properties.put("password", password);
		String loginUrl = ApplicationUtil.getURL(appProperties.getProperty("mx.skyguardian.ftpservice.login.url"), properties);
		ObjectMapper mapper = JsonFactory.create();
		wialonSession = mapper.readValue(HTTP.getJSON(loginUrl, null), WialonSession.class);
		((WialonSession) wialonSession).getUser().setPassword("");
		
		return wialonSession;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getUnits(String eid, String flags) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("sid", eid);
		properties.put("flags", flags);

		String unitsUrl = ApplicationUtil.getURL(
				appProperties.getProperty("mx.skyguardian.ftpservice.units.url"),
				properties);
		
		ObjectMapper mapper = JsonFactory.create();
		Map data = mapper.readValue(HTTP.getJSON(unitsUrl, null), Map.class);
		Object items = data.get("items");
		if (items instanceof List) {
			for (Object item : (List)items) {
				if (item instanceof Map) {
					Map unit = (Map)item;
					Object unitId = unit.get("id");
					log.debug(unitId);
				}
			}
		}
		return null;
	}
	
	public Properties getAppProperties() {
		return this.appProperties;
	}

	public void setAppProperties(Properties appProperties) {
		this.appProperties = appProperties;
	}
}
