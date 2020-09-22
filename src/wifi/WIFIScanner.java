package wifi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.Config;
import main.PiWifiScan;
import websocket.WSClientEvent;
import websocket.WSClientEvent.WEBCLIENTEVENT;
import websocket.WSCommunicationEvent.COMEVENT;
import websocket.WSClientListener;
import websocket.WSCommunicationEvent;

public class WIFIScanner implements WSClientListener {

	private static Log log = LogFactory.getLog(WIFIScanner.class);

	private transient ArrayList<WIFIListener> listeners;
	
	private Hashtable<String, WifiNetwork> networks = new Hashtable<String, WifiNetwork>();
	private String sConnectedESSID = "";
	
	public WIFIScanner () {
		listeners = new ArrayList<WIFIListener>();
	}
	
	public void refresh() {
		scan();
		getConnectedESSID();
      notifyEvent( new WSCommunicationEvent(COMEVENT.SCAN, getJSONInterface()));
	}
	
	public void scan () {
		try {
			BufferedReader reader;
	    	if (Config.DEBUG) {
	    		reader = new BufferedReader(new FileReader(new File("demo.txt")));
	    	} else {
	    		// Execute command
	        String command = "sudo iwlist " + PiWifiScan.o_configuration.getWLANInterface() + " scan";
	        Process p = Runtime.getRuntime().exec(command);
	        try {
	            p.waitFor();
	        } catch (InterruptedException ex) {
	            ex.printStackTrace();
	        }
	        
	        reader = new BufferedReader(new InputStreamReader(p.getInputStream()));	
	    	}
        
	    	parseContent(reader);
	    } catch (IOException e) {
	    }
	}
	
	public void getConnectedESSID () {
	    try {
	    	String commandresponse;
	    	if (Config.DEBUG) {
	    		commandresponse = "wlan0     ESSID:\"BRIVI\"";
	    	} else {
	    		// Execute command
	        String command = "sudo iwgetid " + PiWifiScan.o_configuration.getWLANInterface();
	        Process p = Runtime.getRuntime().exec(command);
	        try {
	            p.waitFor();
	        } catch (InterruptedException ex) {
	            ex.printStackTrace();
	        }
	        
	        commandresponse = new BufferedReader(new InputStreamReader(p.getInputStream())).toString();	
	    	}

	    	Pattern MY_PATTERN = Pattern.compile("^" + PiWifiScan.o_configuration.getWLANInterface() + ".*ESSID:\"(.*)\"|\\]");
			Matcher m = MY_PATTERN.matcher(commandresponse);
			if (m.find()) {
				sConnectedESSID = m.group(1);
			} else {
				sConnectedESSID = "";
			}
	    } catch (IOException e) {
	    }
	}
	
	public void connect (JSONObject _ident) {
		System.out.println(_ident);
	}
	
	private void parseContent (BufferedReader br) {
		String line;
        try {
			WifiNetwork last = null;
        	while ((line = br.readLine()) != null) {
				boolean isNewNetwork = isNewNetwork(line);
				if (isNewNetwork) {
					if (last != null) addNetwork(last);
					last = new WifiNetwork();
				}
				if (last != null) last.addCfgLine(line);
			}
        	if (last != null) addNetwork(last);
		} catch (IOException e) {
			log.error("Cannot parse wifi networks");
		}
	}
	
	private void addNetwork(WifiNetwork _network) {
		WPANetwork wpa = PiWifiScan.o_wpa.getFromSSID(_network.getSSID());
		if (wpa != null) {
			_network.setWPA(wpa);
		}
		networks.put(_network.getSSID(), _network);
	}
	
	private static boolean isNewNetwork(String _line) {
		Pattern pattern = Pattern.compile("Cell", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(_line);
		return matcher.find();
	}
	
	private JSONObject getJSONInterface() {
		JSONObject networkinterface = new JSONObject();
		try {
			JSONArray list = new JSONArray();
			for( WifiNetwork net : networks.values() ) {
				list.put(net.getJSON());
			}
			networkinterface.put("interface", PiWifiScan.o_configuration.getWLANInterface());
			networkinterface.put("ssid", sConnectedESSID);
			networkinterface.put("networks", list);
		} catch (JSONException e) {
			log.error("cannot create network interface");
		}
		return networkinterface;
	}

	@Override
	public void state_changed (WSClientEvent _event) {
		JSONObject item = _event.getProperties();
		// JSONObject from = _event.getFrom();
		
		switch(_event.eventType) {
			case CONNECTED:
			case SCAN:
				refresh();
				break;
			case CONNECT:
				connect(item);
				break;
			default:
				break;
		}
	}

	@Override
	public boolean isRegistered (WEBCLIENTEVENT _type) {
		return true; //register all webclient events
	}
	
	/**
	 * UPDATE EVENTS MANAGEMENT
	 * @param _listener
	 */
	public void addListener( WIFIListener _listener ) {
		if( _listener != null ) {
			if ( !listeners.contains( _listener ) ) {
				this.listeners.add( _listener );
			}
		}
	}

	public void removeListener( WIFIListener _listener ) {
		if( _listener != null ) {
			if ( listeners.contains( _listener ) ) {
				this.listeners.remove( _listener );
			}
		}
	}

	public void removeAllListeners() {
		listeners = new ArrayList<WIFIListener>();
	}

	public void notifyEvent( WSCommunicationEvent _event ) {
		if( listeners != null && listeners.size() > 0 ) {
			Object[] tl = listeners.toArray();
			for( int i = 0; i<tl.length ; i++ ) {
				WIFIListener listener = ((WIFIListener)tl[i]);
				if( listener.isRegistered( _event.eventType ) ) {
					listener.state_changed( _event );
				}
			}
		}
	}
}
