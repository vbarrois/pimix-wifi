package utils;

import java.net.NetworkInterface;
import java.net.SocketException;

public class GetNetworkAddress {

    public static String GetMAC() throws SocketException {
   	NetworkInterface netInf = NetworkInterface.getNetworkInterfaces().nextElement();
		final byte[] mac = netInf.getHardwareAddress();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
		}
		return sb.toString();
    }
}