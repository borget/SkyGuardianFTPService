package mx.skyguardian.ftp.service.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

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
	
	
	public static void main (String ... args) {

		Map<String, String> env = System.getenv();
		boolean isEnvSet = false;
		String envVar = null;
        for (String envName : env.keySet()) {
        	if(envName.equalsIgnoreCase("FTP_APP_PROPERTIES")){
        		envVar = env.get(envName);
        		isEnvSet = true;
        		break;
        	}
        }
        if (true){
        	System.out.println("Env Variable has been found [FTP_SERVICE_CONTEXT_PATH="+envVar+"]");
        	new FileSystemXmlApplicationContext("file:/home/alberto/git/SkyGuardianFTPService/config/applicationContext.xml");
        	//new FileSystemXmlApplicationContext("file:"+envVar);
        } else {
        	System.err.println("ERROR:The env variable has not been set [FTP_SERVICE_CONTEXT_PATH]");
        	Runtime.getRuntime().exit(0);
        }
	}
	
	private final File baseFolder = new File("/home/alberto/git/SkyGuardianFTPService/test/virtual");
	@Override
	public void sendFilesToFTPServer() {
		log.debug("sending files to FTP server...");
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
				"file:/home/alberto/git/SkyGuardianFTPService/config/FtpOutboundChannelAdapter-context.xml");
		try {
			MessageChannel ftpChannel = ctx.getBean("ftpChannel", MessageChannel.class);
			baseFolder.mkdirs();
			final File fileToSendA = new File(baseFolder, "a.txt");
			final InputStream inputStreamA = new FileInputStream("/home/alberto/git/SkyGuardianFTPService/test/a.txt");
			FileUtils.copyInputStreamToFile(inputStreamA, fileToSendA);
			final Message<File> messageA = MessageBuilder.withPayload(fileToSendA).build();
			ftpChannel.send(messageA);

			log.info("Successfully transfered file" + "FILEXXX" + " to the remote FTP location.");
		} catch(Exception e) {
			log.error(e.getMessage());
		} finally {
			ctx.close();
			FileUtils.deleteQuietly(baseFolder);
		}
	}
	
	public Properties getAppProperties() {
		return this.appProperties;
	}

	public void setAppProperties(Properties appProperties) {
		this.appProperties = appProperties;
	}
}
