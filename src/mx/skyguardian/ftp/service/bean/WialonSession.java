package mx.skyguardian.ftp.service.bean;

public class WialonSession {
	private User user = null;
	private String eid = null;
	private Long tm = null;

	public WialonSession () {
		
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
		
	public class User {
		private String nm = null;
		private String password = null;
		private String id = null;
		
		public String getNm() {
			return nm;
		}
		
		public void setNm(String nm) {
			this.nm = nm;
		}
		
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
	}
	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public Long getTm() {
		return tm;
	}

	public void setTm(Long tm) {
		this.tm = tm;
	}
}
