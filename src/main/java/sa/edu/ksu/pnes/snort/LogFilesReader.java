package sa.edu.ksu.pnes.snort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LogFilesReader {

    
      ///etc/snort/custom_rules/pnes.rules
	public static List<SnortRule> readRules(String file) {
		List<SnortRule> rules = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line = br.readLine();
			while (line != null) {
				if(line.trim().length() <= 0)
				{
					line = br.readLine();
					continue;
				}
				rules.add(new SnortRule(line));
				line = br.readLine();
			}
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rules;
	}

	public static String  readSSID(String platform) {
		String[] cmd = new String[] {"netsh","wlan","show","interfaces"};
		if(Platforms.LINUX.equalsIgnoreCase(platform)){
			cmd = new String[] {"iwgetid"};
		}
		ProcessBuilder builder = new ProcessBuilder(cmd);
		String SSID = "Default Wireless SSID";
		try {
			Process p = builder.start();
			BufferedReader br = new BufferedReader(p.inputReader());
			String line = br.readLine();
			while (line != null) {
				if(line.trim().length() > 0)
				{
					if(line.toLowerCase().contains("ssid") && !line.toLowerCase().contains("bssid")) {
						var data = line.split(":");
						if (data.length == 2) {
							SSID = data[1].replace("\"", "");
							break;
						}
					}
				}
				line = br.readLine();
			}
			br.close();
			if(p!=null && p.isAlive())
			{
				p.destroy();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return SSID;
	}
        //line = 1
        //line = 2
        //line = 3
	public static List<SnortLogMessage> getLog(String file){
		List<SnortLogMessage> logs = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line = br.readLine();
			int number = 1;
			while (line != null) {
				if(line.trim().length() <= 0)
				{
					line = br.readLine();
					continue;
				}
				
				logs.add(number - 1,new SnortLogMessage(line,number++));
				line = br.readLine();
			}
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return logs;
	}

}
