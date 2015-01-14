package mx.skyguardian.ftp.service.utils;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.util.PropertyPlaceholderHelper;

public class ApplicationUtil {
	private static Logger log = Logger.getLogger(ApplicationUtil.class);
	private ApplicationUtil () {
	}
	
	public static String getProperty(String url, Map<String, String> propertiesMap) {
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
	
	public static void writeXMLFile(String fullFileName, String fileData) throws Exception {
		OutputStream out = null;
		try{
			byte data[] = fileData.getBytes();
			Path path = Paths.get(fullFileName);
			out = new BufferedOutputStream(Files.newOutputStream(path,
					StandardOpenOption.CREATE_NEW));
			out.write(data, 0, data.length);
			log.info("XML_CREATED>>>Successfully wrote file [" + fullFileName + "]");
		}
		finally{
			if (out != null) {
				out.close();
			}
		}
	}
	
}
