package mx.skyguardian.ftp.service.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import mx.skyguardian.ftp.service.bean.Unit;
import mx.skyguardian.ftp.service.bean.WialonSession;
import mx.skyguardian.ftp.service.supercsv.SuperCSVHelper;
import mx.skyguardian.ftp.service.utils.ApplicationUtil;
import mx.skyguardian.ftp.service.utils.Constants;
import mx.skyguardian.ftp.service.ws.IGurtamHTTPRequestExecutor;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

public class SkyguardianFTPManager implements ISkyguardianFTPManager {
	private static Logger log = Logger.getLogger(SkyguardianFTPManager.class);
	@Resource(name = "appProperties")
	private Properties appProperties;
	private IGurtamHTTPRequestExecutor httpRequestExecutor;
	private SuperCSVHelper superCSVHelper;
	private static File SG_BASE_FOLDER = null;
	private static File SG_TEMP_FOLDER = null;
	private static String SG_FTP_CHANNEL_CONTEXT = null;
	
	public static void main (String ... args) {
		Map<String, String> envVariablesMap = System.getenv();
		if(args.length > 0 && args[0].equals("DEBUG_MODE")){
			System.out.println("DEBUG_MODE enabled...");
			SkyguardianFTPManager.SG_FTP_CHANNEL_CONTEXT = "/home/alberto/git/SkyGuardianFTPService/config/FtpOutboundChannelAdapter-context.xml";
        	SkyguardianFTPManager.SG_BASE_FOLDER = new File("/home/alberto/git/SkyGuardianFTPService/test");
        	SkyguardianFTPManager.SG_TEMP_FOLDER = new File("/home/alberto/git/SkyGuardianFTPService/test/temp");
        	new FileSystemXmlApplicationContext("file:/home/alberto/git/SkyGuardianFTPService/config/applicationContext.xml");
		}
		else if (envVariablesMap.get("SG_FTP_APP_CONTEXT") != null &&
        	envVariablesMap.get("SG_FTP_CHANNEL_CONTEXT") != null &&
        	envVariablesMap.get("SG_BASE_FOLDER") != null &&
        	envVariablesMap.get("SG_TEMP_FOLDER") != null){
        	System.out.println("Env variables are properly set.");
        	SkyguardianFTPManager.SG_FTP_CHANNEL_CONTEXT = envVariablesMap.get("SG_FTP_CHANNEL_CONTEXT");
        	SkyguardianFTPManager.SG_BASE_FOLDER = new File(envVariablesMap.get("SG_BASE_FOLDER"));
        	SkyguardianFTPManager.SG_TEMP_FOLDER = new File(envVariablesMap.get("SG_TEMP_FOLDER"));
        	new FileSystemXmlApplicationContext("file:"+envVariablesMap.get("SG_FTP_APP_CONTEXT"));
        } else {
        	System.err.println("ERROR:The required env variables are not properly set or incorrect ARGS parameter.");
        	Runtime.getRuntime().exit(0);
        }
	}
	
	@Override
	public void sendFilesToFTPServer() {
		try {
			WialonSession session = this.httpRequestExecutor.doLogin(
					this.appProperties.getProperty("mx.skyguardian.ftpservice.gurtam.user"),
					this.appProperties.getProperty("mx.skyguardian.ftpservice.gurtam.password"));
			List<Unit> units = this.httpRequestExecutor.getUnits(session.getEid(), Constants.FLAGS_0x00100401);
			String csvFileNameWithExt = getFileName(session.getTm())+".csv";
			String fullFileName = SkyguardianFTPManager.SG_BASE_FOLDER+File.separator+csvFileNameWithExt;
			this.superCSVHelper.writeCSVToFile(units, fullFileName);
			this.sendFileToFTPServer(csvFileNameWithExt);
			String xmlFileNameWithExt = getFileName(session.getTm())+".xml";
			this.createSendXMLFileToFTPServer(xmlFileNameWithExt);
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private void createSendXMLFileToFTPServer (String xmlFileNameWithExt) throws Exception {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("user", this.appProperties.getProperty("mx.skyguardian.ftpservice.gurtam.user"));
		properties.put("password", this.appProperties.getProperty("mx.skyguardian.ftpservice.gurtam.password.encrypted"));
		String unitsUrl = ApplicationUtil.getProperty(
				this.appProperties.getProperty("mx.skyguardian.ftpservice.controltower.units"),properties);
		String unitsXml = this.httpRequestExecutor.getHTTPRequest(unitsUrl);	
		String xmlFileName = SkyguardianFTPManager.SG_BASE_FOLDER+File.separator+xmlFileNameWithExt;
		ApplicationUtil.writeXMLFile(xmlFileName, unitsXml);
		this.sendFileToFTPServer(xmlFileNameWithExt);
	}
	
	private String getFileName(Long gurtamLoginTime) throws Exception{
		String fileName = null;
		if (gurtamLoginTime != null) {
			GregorianCalendar loginTime = ApplicationUtil.getDatetimeByTimeZone(
					this.appProperties.getProperty("mx.skyguardian.ftpservice.timezone"), 
					gurtamLoginTime);
			
			SimpleDateFormat fileFormat = new SimpleDateFormat(this.appProperties.getProperty("mx.skyguardian.ftpservice.filenameformat"));
			fileName = fileFormat.format(loginTime.getTime());
		} else {
			throw new Exception("Error getting login time to generate the csv file name.");
		}
		return fileName;
	}
	
	public void sendFileToFTPServer(String fileName) throws Exception {
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
				"file:"+SkyguardianFTPManager.SG_FTP_CHANNEL_CONTEXT);
		try {
			MessageChannel ftpChannel = ctx.getBean("ftpChannel", MessageChannel.class);
			SkyguardianFTPManager.SG_BASE_FOLDER.mkdirs();
			final File fileToSend = new File(SkyguardianFTPManager.SG_TEMP_FOLDER+File.separator, fileName);
			final InputStream inputStream = new FileInputStream(SkyguardianFTPManager.SG_BASE_FOLDER+File.separator+fileName);
			FileUtils.copyInputStreamToFile(inputStream, fileToSend);
			final Message<File> message = MessageBuilder.withPayload(fileToSend).build();
			ftpChannel.send(message);

			log.info("FTP_SERVICE>>>Successfully transfered file [" + fileName + "] to the remote FTP location.");
			
		} catch(Exception e) {
			throw new Exception("Error sending CSV to FTP Server. " + e.getMessage());
		} finally {
			ctx.close();
			FileUtils.deleteQuietly(SkyguardianFTPManager.SG_TEMP_FOLDER);
		}
	}
	
	public Properties getAppProperties() {
		return this.appProperties;
	}

	public void setAppProperties(Properties appProperties) {
		this.appProperties = appProperties;
	}
	
	public IGurtamHTTPRequestExecutor getHttpRequestExecutor() {
		return httpRequestExecutor;
	}

	public void setHttpRequestExecutor(
			IGurtamHTTPRequestExecutor httpRequestExecutor) {
		this.httpRequestExecutor = httpRequestExecutor;
	}
	
	public SuperCSVHelper getSuperCSVHelper() {
		return superCSVHelper;
	}

	public void setSuperCSVHelper(SuperCSVHelper superCSVHelper) {
		this.superCSVHelper = superCSVHelper;
	}
}
