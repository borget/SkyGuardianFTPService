export SG_FTP_APP_CONTEXT=/home/alberto/git/SkyGuardianFTPService/config/applicationContext.xml
export SG_FTP_CHANNEL_CONTEXT=/home/alberto/git/SkyGuardianFTPService/config/FtpOutboundChannelAdapter-context.xml
export SG_BASE_FOLDER=/home/alberto/git/SkyGuardianFTPService/test
export SG_TEMP_FOLDER=/home/alberto/git/SkyGuardianFTPService/test/temp
#nohup java -jar ./SkyGuardianFTPService.jar &
java -jar ./SkyGuardianFTPService.jar
