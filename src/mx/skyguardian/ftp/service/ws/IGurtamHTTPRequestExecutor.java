package mx.skyguardian.ftp.service.ws;

import java.util.List;

import mx.skyguardian.ftp.service.bean.Unit;
import mx.skyguardian.ftp.service.bean.WialonSession;

public interface IGurtamHTTPRequestExecutor {
	WialonSession doLogin(String userName, String password) throws Exception;
	List<Unit> getUnits(String eid, String flags) throws Exception;
}
