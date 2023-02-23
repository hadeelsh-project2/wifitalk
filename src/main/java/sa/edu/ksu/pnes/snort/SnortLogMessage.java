package sa.edu.ksu.pnes.snort;

import java.io.IOException;
import java.io.StringReader;

public class SnortLogMessage {

	private String id;
	private String time;
	private String protocol;
	private String sourceIp;
	private String targetIp;
	private String sourcePort;
	private String targetPort;
	private String message;
	
	public SnortLogMessage(String line, int number) {
		try {
			this.id = "log_" + number;
		this.parseLine(line);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	private void parseLine(String line) throws IOException {
		if(line == null)
			return;
		char[] sr = line.toCharArray();
		StringBuffer sb = new StringBuffer();
		int index = 0; //time  
		char c = sr[index++];
		while(c != ' ') {
			sb.append(c);
			c = sr[index++];
		}
		this.time = sb.toString();
		c = sr[index++];
		while(c!= '\'') c = sr[index++];
		sb = new StringBuffer();
		c = sr[index++];
		while(c!= '\'') { 
			sb.append(c);
			c = sr[index++];}
		this.message = sb.toString();
	
		c = sr[index++];
		while(c != '{') c = sr[index++];
		
		sb = new StringBuffer();
		c = sr[index++];
		while(c != '}') 
			{
			sb.append(c);
			c = sr[index++];}
		this.protocol = sb.toString();
		
		index++;//skip space after protocol.
		
		sb = new StringBuffer();
		for(;index < sr.length; index++) {
			sb.append(sr[index]);
		}
		var ips = sb.toString().split("->");
		if(ips.length == 2) {
                     //src
			var srcip = ips[0].split(":");
			if(srcip.length == 2 ) {
				this.sourceIp = srcip[0];
				this.sourcePort = srcip[1];
			}
			else
				
			{
				this.sourceIp = srcip[0];
			}
                        //dest
			var destip = ips[1].split(":");
			if(srcip.length == 2 ) {
				this.targetIp = destip[0];
				this.targetPort = destip[1];
			}
			else
				
			{
				this.targetIp = destip[0];
			}
		}
                else 
		{
			this.sourceIp = ips[0];
		}
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSourceIp() {
		return sourceIp;
	}
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	public String getTargetIp() {
		return targetIp;
	}
	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}
	public String getSourcePort() {
		return sourcePort;
	}
	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}
	public String getTargetPort() {
		return targetPort;
	}
	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
