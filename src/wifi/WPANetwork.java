package wifi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPANetwork {
	public String sSSID;
	public String sPSK;
	
	public WPANetwork () {
	}
	
	public String getSSID () {
		return sSSID;
	}
	
	public String getWEP () {
		return sPSK;
	}
	
	public void addCfgLine (String _line) {
		checkSSID(_line);
		checkWEP(_line);
	}
	
	private void checkSSID(String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?ssid=\"(.*)\".*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			sSSID = m.group(1);
		}
	}
	
	private void checkWEP(String _line) {
		Pattern MY_PATTERN = Pattern.compile("^.*?psk=\"(.*)\".*");
		Matcher m = MY_PATTERN.matcher(_line);
		if (m.find()) {
			sPSK = m.group(1);
		}
	}
}
