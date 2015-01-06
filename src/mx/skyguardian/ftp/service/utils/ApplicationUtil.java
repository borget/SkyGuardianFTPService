package mx.skyguardian.ftp.service.utils;

import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.springframework.util.PropertyPlaceholderHelper;

public class ApplicationUtil {
	
	private ApplicationUtil () {
	}
	
	public static String getURL(String url, Map<String, String> propertiesMap) {
		PropertyPlaceholderHelper h = new PropertyPlaceholderHelper("$[", "]");
		Properties p = new Properties();

		for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
			p.setProperty(entry.getKey(), entry.getValue());
		}

		return h.replacePlaceholders(url, p);
	}
	
	public static GregorianCalendar getDatetimeByTimeZone(String timeZone, Long posix) {
		TimeZone defaultTimeZone = TimeZone.getDefault();
		GregorianCalendar fechaHoraEvento = null;
		
		if (!defaultTimeZone.getID().equalsIgnoreCase(timeZone)){
			TimeZone mxTimeZone = TimeZone.getTimeZone(timeZone);
			fechaHoraEvento = (GregorianCalendar)GregorianCalendar.getInstance(mxTimeZone);
			int offSet = mxTimeZone.getOffset(fechaHoraEvento.getTimeInMillis());
			fechaHoraEvento.setTimeInMillis(posix * 1000);
			fechaHoraEvento.add(GregorianCalendar.MILLISECOND, offSet);
		} else {
			fechaHoraEvento = (GregorianCalendar)GregorianCalendar.getInstance();
			fechaHoraEvento.setTimeInMillis(posix * 1000);
		}
		
		return fechaHoraEvento;
	}
	
}
