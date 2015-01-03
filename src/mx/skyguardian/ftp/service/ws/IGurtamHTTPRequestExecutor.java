package mx.skyguardian.ftp.service.ws;

import mx.skyguardian.ftp.service.bean.WialonSession;

public interface IGurtamHTTPRequestExecutor {
	WialonSession doLogin(String userName, String password);
	Object getUnits(String eid, String flags);
}
