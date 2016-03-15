package com.cdjx.kolo;

public class GuanJia {
	
	private Integer id;
	private String name;
	private String serverPath;
//	private String serverPort;		
//	private String controlCenterIP;	
//	private String controlCenterPort;
	private String videoIP;
	private String videoPort;
	private String videoChanel;		

	public GuanJia(String name, String serverPath,  String videoIP, 
			String videoPort, String videoChanel) {
		super();
		this.name = name;
		this.serverPath = serverPath;
//		this.serverPort = serverPort;
//		this.controlCenterIP = controlCenterIP;
//		this.controlCenterPort = controlCenterPort;
		this.videoIP = videoIP;
		this.videoPort = videoPort;
		this.videoChanel = videoChanel;
	}
	
	public GuanJia(int id, String name, String serverPath,  String videoIP, 
			String videoPort, String videoChanel) {
		super();
		this.id = id;
		this.name = name;
		this.serverPath = serverPath;
//		this.serverPort = serverPort;
//		this.controlCenterIP = controlCenterIP;
//		this.controlCenterPort = controlCenterPort;
		this.videoIP = videoIP;
		this.videoPort = videoPort;
		this.videoChanel = videoChanel;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	/*	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getControlCenterIP() {
		return controlCenterIP;
	}

	public void setControlCenterIP(String controlCenterIP) {
		this.controlCenterIP = controlCenterIP;
	}

	public String getControlCenterPort() {
		return controlCenterPort;
	}

	public void setControlCenterPort(String controlCenterPort) {
		this.controlCenterPort = controlCenterPort;
	}*/

	public String getVideoIP() {
		return videoIP;
	}

	public void setVideoIP(String videoIP) {
		this.videoIP = videoIP;
	}

	public String getVideoPort() {
		return videoPort;
	}

	public void setVideoPort(String videoPort) {
		this.videoPort = videoPort;
	}

	public String getVideoChanel() {
		return videoChanel;
	}

	public void setVideoChanel(String videoChanel) {
		this.videoChanel = videoChanel;
	}

	@Override
	public String toString() {
		return "GuanJia [id=" + id + ", name=" + name 
				+ ", serverPath=" + serverPath + ",   videoIP="
				+ videoIP + ", videoPort=" + videoPort + ", videoChanel="
				+ videoChanel + "]";
	}	
}
