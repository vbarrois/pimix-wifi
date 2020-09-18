package main;

import utils.Configuration;

public class Config
{
	public static String CONFIG_FILE_NAME = "wifi.ini";
	public final static String VERSION = "0.0.1";
	public static boolean DEBUG = true;
	
	private Configuration _config = null;

	// configuration keys
	private static final String KEY_ROUTERADDRESS	= "router_address";
	private static final String KEY_ROUTERPORT		= "router_port";
	private static final String KEY_WLANINTERFACE	= "wlan_interface";
	private static final String KEY_DEBUGMODE		= "debug";
	
	private static Config _instance 	= null;
	private String _router_address		= "pimix-router";
	private int	_router_port			= 82;
	private String _wlan_interface		= "wlan0";
	private boolean _debug_mode 		= false;

	private Config(){}

	/**
	 * Returns Config instance.
	*/
	public synchronized static Config getInstance() {
		if (_instance == null) {
			_instance = new Config();
		}
		return _instance;
	}

	/**
	 * Loads configuration for the specified file.
	*/
	public void load(String configfile) {
		CONFIG_FILE_NAME = configfile;
		load();
	}

	/**
	* Loads configuration.
	*/
	public void load() {
		_config = new Configuration(CONFIG_FILE_NAME);
		// Creates config entries if needed.
		if (_config.get(KEY_ROUTERADDRESS) != null) setRouterAddress(_config.get(KEY_ROUTERADDRESS));
		
		if (_config.get(KEY_ROUTERPORT) != null) setRouterPort(_config.getInt(KEY_ROUTERPORT));

		if (_config.get(KEY_DEBUGMODE) == null) _config.add(KEY_DEBUGMODE, _debug_mode);
		setDebugMode(_config.getBoolean(KEY_DEBUGMODE));
	}

	/**
	 * @return loading status of the config file
	*/
	public boolean isLoaded() {
		return (_config!=null && _config.isLoaded());
	}

	/**
	 * Saves configuration.
	*/
	public void save() {
		if (_config != null) {
			_config.save();
		}
	}
	
	/**
	 * load / save properties
	 * @param _folder
	 */
	public void setDebugMode( boolean _debug ) {
		_debug_mode = _debug;
	}

	public boolean isDebugMode() {
		return _debug_mode;
	}
	
	public void setRouterAddress(String _addr) {
		if(_addr != null && _addr.length() > 0) _router_address = _addr;
	}

	public String getRouterAddress() {
		return _router_address;
	}
	
	public void setWLANInterface(String _interface) {
		if(_interface != null && _interface.length() > 0) _wlan_interface = _interface;
	}

	public String getWLANInterface() {
		return _wlan_interface;
	}
	
	public void setRouterPort(int _port) {
		if(("" + _port).length() > 0) _router_port = _port;
	}

	public int getRouterPort() {
		return _router_port;
	}
}