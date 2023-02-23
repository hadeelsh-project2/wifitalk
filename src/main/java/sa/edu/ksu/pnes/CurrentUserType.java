package sa.edu.ksu.pnes;


public class CurrentUserType {
	
	public static final String TECH = "TECH";
	public static final String NON_TECH = "NON_TECH";
	
	private String userType;
	public CurrentUserType() {
		this.userType = "NOT_SET";
	}
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
	
}
