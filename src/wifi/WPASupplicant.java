package wifi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.Config;
import main.PiWifiScan;

public class WPASupplicant {
	private static Log log = LogFactory.getLog(WPASupplicant.class);

	private Hashtable<String, WPANetwork> wpaNetworks = new Hashtable<String, WPANetwork>();
	
	public WPASupplicant () {}
	
	public void analyze () {
		readFile();
	}
	
	public void readFile () {
		try {
			BufferedReader reader;
			if (Config.DEBUG) {
				reader = new BufferedReader(new FileReader(new File("wpa_supplicant.conf")));
			} else {
				reader = new BufferedReader(new FileReader(new File(PiWifiScan.o_configuration.getWPASupplicant())));
			}
			
			parseContent(reader);
			log.info("Found " + wpaNetworks.size() + " saved network(s)");
	   } catch (IOException e) {
	   	log.error(e.toString());
	   }
	}
	
	private void parseContent (BufferedReader br) {
		String line;
        try {
      	  WPANetwork last = null;
      	  while ((line = br.readLine()) != null) {
      		  boolean isNewNetwork = isNewNetwork(line);
      		  if (isNewNetwork) {
      			  if (last != null) wpaNetworks.put(last.getSSID(), last);
      			  last = new WPANetwork();
      		  }
      		  if (last != null) last.addCfgLine(line);
      	  }
      	  if (last != null) wpaNetworks.put(last.getSSID(), last);
        } catch (IOException e) {
      	  log.error("Cannot parse wpa networks");
        }
	}
	
	public WPANetwork getFromSSID(String _ssid) {
		return wpaNetworks.get(_ssid);
	}
	
	private static boolean isNewNetwork(String _line) {
		Pattern pattern = Pattern.compile("network=\\{", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(_line);
		return matcher.find();
	}
	
	public static boolean addNetwork(String _ssid, String _psk) {
		String command = "wpa_passphrase \"" + _ssid + "\" \"" + _psk + "\" | sudo tee " + PiWifiScan.o_configuration.getWPASupplicant();
      try {
         Process p = Runtime.getRuntime().exec(command);
         p.waitFor();
         BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));	

      } catch (InterruptedException | IOException ex) {
      	log.error("Cannot add network to wpa_supplicant");
      	return false;
      }
      log.info("Network added to wpa_supplicant");
      return true;
	}
}
