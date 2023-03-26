package utilities;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public final class NicDump {
	private NicDump() {

	}

	public static void main(String[] pArgs) {
		try {
			Enumeration<NetworkInterface> tNics = NetworkInterface
					.getNetworkInterfaces();
			int tCount = 0;

			if (tNics.hasMoreElements()) {
				System.out.println("=== Network Interfaces ===");
				System.out.println();
			}
			while (tNics.hasMoreElements()) {
				NetworkInterface tNetIf = tNics.nextElement();
				tCount++;
				System.out.println("Identifying name:    \"" + tNetIf.getName()
						+ "\"");
				System.out.println("Verbose name:        \""
						+ tNetIf.getDisplayName() + "\"");
				Enumeration<InetAddress> tAssignedIps = tNetIf
						.getInetAddresses();
				while (tAssignedIps.hasMoreElements()) {
					InetAddress tAddress = tAssignedIps.nextElement();
					System.out.println("Assigned IP address: "
							+ tAddress.getHostAddress());
				}
				System.out.println();
			}

			if (tCount > 0) {
				System.out.println(tCount + " network interface"
						+ (tCount > 1 ? "s" : "") + " found.");
			} else {
				System.out.println("None found.");
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
