package sa.edu.ksu.pnes.snort;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkScanner {

	private List<NetworkDevice> allDevices;
	private ExecutorService pool;

	private String networkAddress = null;

	public NetworkScanner(String iface) {

		this.allDevices = new ArrayList<>();
		String currentDevice = "";
		if (iface != null && iface.trim().length() > 0) {
			this.networkAddress = this.getNetworkInterfaceIp(iface);
			if (this.networkAddress != null && this.networkAddress.trim().length() > 0) {
				currentDevice = this.networkAddress;
				String prefex = this.networkAddress.substring(0, this.networkAddress.lastIndexOf('.'));
				if (!prefex.endsWith(".")) {
					prefex = prefex + ".";
				}
				for (int i = 0; i <= 255; i++) {

					NetworkDevice nd = new NetworkDevice();
					nd.setId("dev_" + i);
					nd.setIp(prefex + i);
					if(currentDevice!=null && currentDevice.trim().equalsIgnoreCase(nd.getIp().trim()))
					{
						nd.setCurrentDevice(true);
					}
					allDevices.add(nd);
				}
			}
		}

		this.pool = Executors.newFixedThreadPool(this.allDevices.size() > 0 ? this.allDevices.size() : 1 );
	}

	public List<NetworkDevice> getAllDevices() {
		return this.allDevices;
	}

	private String getNetworkInterfaceIp(String iface) {

		try {
			var face = NetworkInterface.getByName(iface);
			if (face == null) {
				return "";
			}

			var it = face.getInetAddresses().asIterator();
			final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
			while (it.hasNext()) {
				var addr = it.next();
				if (addr.getHostAddress().matches(IPV4_REGEX)) {
					return addr.getHostAddress();
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public void startProcess() throws Exception {

		for (int i = 0; i < this.allDevices.size(); i++) {
			pool.submit(new StatusChecker(this.allDevices.get(i)));
		}

	}

	public void shutdownProcess() {
		if (this.pool != null) {
			this.pool.shutdownNow();
		}
	}

	public String getNetworkAddress(){
		if(this.networkAddress == null)
		{
			return "0.0.0.0/24";
		}
		String prefex =
				this.networkAddress.substring(0, this.networkAddress.lastIndexOf('.'));
		if (!prefex.endsWith(".")) {
			prefex = prefex + ".";
		}
		return prefex + "0/24";
	}

}

class StatusChecker implements Callable<Void> {
	private NetworkDevice device;

	public StatusChecker(NetworkDevice dev) {
		this.device = dev;
	}

	@Override
	public Void call() throws Exception {
		// TODO Auto-generated method stub
		while (true) {
			InetAddress inAddress = InetAddress.getByName(this.device.getIp());
			if (inAddress.isReachable(1000)) {
				this.device.setStatus(NetworkDevice.UP);// .UP;

			} else {
				this.device.setStatus(NetworkDevice.DOWN);
			}
                        //1s -> 1000ms
                        //1m -> 60s -> 60000ms
                        //60 * 5 = 5MIN
			Thread.sleep(10000);
		}

	}

}
