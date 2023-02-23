package sa.edu.ksu.pnes.snort;


public class NetworkDevice {

	public static final String UP = "Connected";
	public static final String DOWN = "Disconnected";
	
	private String id;
	private String ip;
	private String status;
	private boolean danger;
	private boolean blocked;
	private long lastStatus;

	private boolean currentDevice;
	
	public NetworkDevice() {
		this.status = DOWN;
		this.id = "_";
		this.ip ="0.0.0.0";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isDanger() {
		return danger;
	}
	public void setDanger(boolean danger) {
		this.danger = danger;
	}
	public boolean isBlocked(){
		return blocked;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	public long getLastStatus() {
		return lastStatus;
	}
	public void setLastStatus(long lastStatus) {
		this.lastStatus = lastStatus;
	}


	public boolean isCurrentDevice(){
		return this.currentDevice;
	}
	public void setCurrentDevice(boolean currentDevice){
		this.currentDevice = currentDevice;
	}
}
