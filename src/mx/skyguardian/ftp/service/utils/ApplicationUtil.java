package mx.skyguardian.ftp.service.utils;

import java.util.Map;
import java.util.Properties;

import org.springframework.util.PropertyPlaceholderHelper;

public class ApplicationUtil {
	
	public static String getURL(String url, Map<String, String> propertiesMap) {
		PropertyPlaceholderHelper h = new PropertyPlaceholderHelper("$[", "]");
		Properties p = new Properties();

		for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
			p.setProperty(entry.getKey(), entry.getValue());
		}

		return h.replacePlaceholders(url, p);
	}
}
