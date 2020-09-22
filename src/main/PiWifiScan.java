package main;

import java.net.SocketException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import utils.GetNetworkAddress;
import websocket.WSClient;
import wifi.WIFIScanner;
import wifi.WPASupplicant;

public class PiWifiScan {

	private static Log log = LogFactory.getLog(PiWifiScan.class);
	
	public static Config o_configuration;
	private static WSClient o_wsclient; 
	private static WIFIScanner o_scanner = new WIFIScanner(); 
	public static WPASupplicant o_wpa = new WPASupplicant();
	
	public static void main(String[] args)
	{
		log.info("Pi WIFI scanner started v" + Config.VERSION);
		
		String id = "";
		try {
			id = GetNetworkAddress.GetMAC();
			log.info("Identification ID: " + id);
		} catch (SocketException e1) {
			log.error("CRITICAL: No MAC address found !");
			log.error("EXIT APPLICATION");
			System.exit(0);
		}
		
		log.info("Loading configuration file" );
		// load configuration
		o_configuration = Config.getInstance();
		o_configuration.load();
		if( !o_configuration.isLoaded()) {
			log.error("CRITICAL: No configuration file found !");
			log.error("EXIT APPLICATION");
			System.exit(0);
		}
		
		o_wpa.analyze();
			
		o_wsclient = new WSClient( o_configuration.getRouterAddress(), o_configuration.getRouterPort(), id);
		if( o_wsclient.isInitialized() ) {
			o_wsclient.addListener(o_scanner);
			o_wsclient.start();
			o_scanner.addListener(o_wsclient);
		} else {
			log.error( "Cannot initialize Web Client" );
		}
	}
}
