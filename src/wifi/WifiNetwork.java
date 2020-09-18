package wifi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiNetwork {
	
	private static Log log = LogFactory.getLog(WifiNetwork.class);
	
	protected String sCell = "";
	protected String sAddress = "";
	protected String sChannel = "";
	protected String sFrequency = "";
	protected int iQuality = 0;
	protected int iMaxQuality = 0;
	protected int iSignalLevel = 0;
	protected String sESSID = "";
	protected boolean bEncryption = false;
	
	private String sSSID = "";
	private String sKey = "";
	
	public WifiNetwork () {}
	
	public String toString () {
		return sESSID;
	}
	
	public void addCfgLine (String _line) {
		checkCellAddress(_line);
		checkChannel(_line);
		checkFrequency(_line);
		checkQuality(_line);
		checkSignalLevel(_line);
		checkESSID(_line);
		checkEncryption(_line);
	}
	
	public String getSSID () {
		return sSSID;
	}
	
	private void checkCellAddress (String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?Cell ([0-9]*) - Address: (.*)|\\].*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			sCell = m.group(1);
		    sAddress = m.group(2);
		}
	}
	
	private void checkChannel (String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?Channel:([0-9]*)|\\].*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			sChannel = m.group(1);
		}
	}
	
	private void checkFrequency (String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?Frequency:([0-9.]*) GHz|\\].*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			sFrequency = m.group(1);
		}
	}
	
	private void checkQuality (String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?Quality=([0-9]*)/([0-9]*)|\\].*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			iQuality = Integer.parseInt(m.group(1));
			iMaxQuality = Integer.parseInt(m.group(2));
		}
	}
	
	private void checkSignalLevel(String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?Signal level=(.[0-9]*) dBm|\\].*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			iSignalLevel = Integer.parseInt(m.group(1));
		}
	}
	
	private void checkESSID (String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?ESSID:\"(.*)\"|\\].*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			sESSID = m.group(1);
		}
	}
	
	private void checkEncryption (String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?Encryption key:(on|off)|\\].*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			bEncryption = m.group(1).equals("on");
		}
	}
	
	public JSONObject getJSON() {
		JSONObject network = new JSONObject();
		try {
			network.put("cell", sCell);
			network.put("address", sAddress);
			network.put("channel", sChannel);
			network.put("frequency", sFrequency);
			network.put("quality", iQuality);
			network.put("maxquality", iMaxQuality);
			network.put("signal", iSignalLevel);
			network.put("encryption", bEncryption);
			network.put("ssid", sESSID);
			network.put("key", sKey);
		} catch (JSONException e) {
			log.error("Cannot export wifi network as JSON "+ e.getMessage());
		}
		return network;
	}
}
