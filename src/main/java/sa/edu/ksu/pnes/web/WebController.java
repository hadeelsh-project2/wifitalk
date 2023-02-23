
package sa.edu.ksu.pnes.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sa.edu.ksu.pnes.CurrentUserType;
import sa.edu.ksu.pnes.snort.*;

@Controller
@RequestMapping("/")
public class WebController {

	@Value("${platform}")
	private String platform;

	@Value("${winfire.block}")
	private String winBlockCMD;

	@Value("${winfire.release}")
	private String winReleaseCMD;

	@Value("${iptables.block}")
	private String blockCMD;
	
	@Value("${iptables.release}")
	private String releaseCMD;
	
	@Autowired
	private CurrentUserType currentUserType;
	
	@Value("${snort.log}")
	private String appLogFile;

	@Value("${snort.rules}")
	private String snortRulesFile;
	
	@Value("${snort.interface}")
	private String netface;
	
	@Autowired
	private NetworkScanner networkScanner;

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	@ModelAttribute("usrType")
	public String currentUserType() {
		return this.currentUserType.getUserType();
	}
	
	@GetMapping("/userType/{type}")
	public String updateUserType(@PathVariable("type") String type) {
		this.currentUserType.setUserType(type);
		if(CurrentUserType.NON_TECH.equals(type))
		{
			return "redirect:/devices";
		}
		else if(CurrentUserType.TECH.equals(type))
		{
			return "redirect:/rules";
		}
		return "login";
	}
	
	@GetMapping
	public String index(Model model) {

		var ssid = LogFilesReader.readSSID(this.platform);
		var devs = this.networkScanner.getAllDevices();
	
		int up = 0;
		if (devs == null) {
			devs = new ArrayList<NetworkDevice>();

		}
		var devices = devs.stream().filter(x -> NetworkDevice.UP.equals(x.getStatus())).toList();
		up = devices.size();
		model.addAttribute("upCount", up);
		model.addAttribute("downCount", (devs.size() - up));
		model.addAttribute("total", devs.size());
		model.addAttribute("netface", netface);
		model.addAttribute("ssid", ssid);
		model.addAttribute("devices", devices);
		model.addAttribute("netaddr",this.networkScanner.getNetworkAddress());
		return "index";
	}
	
        @GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/about")
	public String aboutPage() {
		return "about";
	}
        
	@GetMapping("/logout")
	public String logout() {
		this.currentUserType.setUserType("NOT_SET");
		return "redirect:/login";
	}
        
        
	@GetMapping("/rules")
	public String getRules(Model model) {
		if(!CurrentUserType.TECH.equals(currentUserType.getUserType())) {
			return "login";
		}
		var rules = LogFilesReader.readRules(this.snortRulesFile);
		model.addAttribute("rules", rules);
		return "list";
	}

       
	@GetMapping("/records")
	public String getRecords(Model model) {
		if(!CurrentUserType.TECH.equals(currentUserType.getUserType())) {
			return "login";
		}
		var records = LogFilesReader.getLog(this.appLogFile);
            
		model.addAttribute("records", records);
		return "view";
	}

	@GetMapping("/devices")
	public String getDevices(Model model) {
		if(!CurrentUserType.NON_TECH.equals(this.currentUserType.getUserType()))
		{
			return "redirect:/login";
		}
		var devs = this.networkScanner.getAllDevices();
		int up = 0;
		if (devs != null) {
			up = (int) devs.stream().filter(x -> NetworkDevice.UP.equals(x.getStatus())).count();
			devs.sort(new Comparator<NetworkDevice>() {

				@Override
				public int compare(NetworkDevice o1, NetworkDevice o2) {

					return o2.getStatus().compareTo(o1.getStatus());
				}

			});

		} else {
			devs = new ArrayList<NetworkDevice>();

		}
		
		this.classifyDevices(devs);
		model.addAttribute("devices", devs);
		model.addAttribute("upCount", up);
		model.addAttribute("downCount", (devs.size() - up));
		model.addAttribute("total", devs.size());
		return "devices";
	}
	
	private void classifyDevices(List<NetworkDevice> devs)
	{
		List<SnortLogMessage> records = LogFilesReader.getLog(this.appLogFile);
		if(records != null && records.size() > 0)
		{
			logger.info("Records of messages are not null");
			for(var record : records)
			{

				if(record.getMessage() == null || !record.getMessage().contains("|")) {
					logger.error("Message Record: Is Null");
					continue;
				}
				logger.info(String.format("Reading message %s",record.getMessage()));
				var danger = record.getMessage().substring(record.getMessage().lastIndexOf('|')).trim();
				logger.info(String.format("danger is evaluated to %s",danger));
				var found = devs.stream().filter(x -> x.getIp().trim().equals(record.getSourceIp().trim())).findFirst();
				if(found.isPresent())
				{

					logger.info(String.format("setting status of device %s",found.get().getIp()));
					if(found.get().isCurrentDevice())
					{
						continue;
					}
					found.get().setDanger("|danger".equals(danger));

				}
				else
				{
					logger.info(String.format("device ip %s is not found",record.getSourceIp()));
				}
			}
		}
		else
		{
			logger.error("Records of messages are NULL");
		}
	}
	
	@GetMapping("/block/{ip}")
	public String blockDevice(@PathVariable("ip") String devIP) {

		String fullCmd = this.platform.equalsIgnoreCase(Platforms.WINDOWS)
				? this.winBlockCMD : this.blockCMD;
		fullCmd = fullCmd.replace("@ip",devIP);

		String cmd[] = fullCmd.split(" ");

		boolean isBlocked = CommandRunner.executeIptables(this.platform,cmd);
		NetworkDevice nd = this.networkScanner.getAllDevices().stream().filter(x -> x.getIp().equals(devIP)).findFirst().get();
		nd.setBlocked(isBlocked);
		return "redirect:/devices";
	}
	

	
	@GetMapping("/release/{ip}")
	public String releaseDevice(@PathVariable("ip") String devIP) {

		String fullCmd = this.platform.equalsIgnoreCase(Platforms.WINDOWS)
				? this.winReleaseCMD : this.releaseCMD;
		fullCmd = fullCmd.replace("@ip",devIP);
		String cmd[] = fullCmd.split(" ");

		boolean isReleased = CommandRunner.executeIptables(this.platform,cmd);
		NetworkDevice nd = this.networkScanner.getAllDevices().stream().filter(x -> x.getIp().equals(devIP)).findFirst().get();
		nd.setBlocked(!isReleased);
		
		return "redirect:/devices";
	}

}
