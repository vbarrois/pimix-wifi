package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Configuration
{
	private File config_file = null;
	private URL config_url = null;
	private Hashtable<String, Object> props = new Hashtable<String, Object>(64);
	private boolean bStatus = false;
	private static Log log = LogFactory.getLog(Configuration.class);
	/**
	 * Constructs a new Configuration object that stores
	 * it's properties in the file with the given name.
	 */
	public Configuration( String _file_name ) {
		this.config_file = new File(_file_name);
		
		log.info("Working Directory = " + System.getProperty("user.dir"));

		bStatus = load();
	}

	/**
	 * Constructs a new Configuration object that stores
	 * it's properties in the given file.
	 */
	public Configuration( File _config_file ) {
		this.config_file = _config_file;
		bStatus = load();
	}

	/**
	 * Constructs a new Configuration object that stores
	 * it's properties in the given file.
	 */
	public Configuration( URL _config_file ) {
		this.config_url = _config_file;
		bStatus = load();
	}

	/**
	 * Constructs a new Configuration object that doesn't
	 * have a file associated with it.
	 */
	public Configuration() {
		this.config_file = null;
	}

	/**
	 * @return The config file.
	 */
	public File getConfigFile() {
		return config_file;
	}

	/**
	 * @return The status of loading
	 */
	public boolean isLoaded() {
		return bStatus;
	}

	/**
	 * Adds a the property with the given name and value.
	 *
	 * @param _name  The name of the property.
	 * @param _value The value of the property.
	 */
	public void add( String _name, String _value ) {
		props.put(_name, _value);
	}

	/**
	 * Adds the boolean property.
	 *
	 * @param _name  The name of the property.
	 * @param _value The value of the property.
	 */
	public void add( String _name, boolean _value ) {
		props.put(_name, _value ? "true" : "false");
	}

	/**
	 * Adds the integer property.
	 *
	 * @param _name  The name of the property.
	 * @param _value The value of the property.
	 */
	public void add( String _name, int _value ) {
		props.put(_name, Integer.toString(_value) );
	}

	/**
	 * Adds the double property.
	 *
	 * @param _name  The name of the property.
	 * @param _value The value of the property.
	 */
	public void add( String _name, double _value ) {
		props.put(_name, Double.toString(_value));
	}

	/**
	 * Returns the value of the property with the
	 * given name.  Null is returned if the named
	 * property is not found.
	 *
	 * @param The name of the desired property.
	 * @return The value of the property.
	 */
	public String get( String _name ) {
		return (String) props.get(_name);
	}

	/**
	 * Returns the value of the property with the
	 * given name.  'default_value' is returned if the
	 * named property is not found.
	 *
	 * @param The           name of the desired property.
	 * @param _default_value The default value of the property which is returned
	 *                      if the property does not have a specified value.
	 * @return The value of the property.
	 */
	public String get( String _name, String _default_value ) {
		Object value = props.get(_name);
		return value != null ? (String) value : _default_value;
	}

	/**
	 * Returns the value of the property with the given name.
	 * 'false' is returned if the property does not have a
	 * specified value.
	 *
	 * @param _name   The name of the desired property.
	 * @param return The value of the property.
	 */
	public boolean getBoolean( String _name ) {
		Object value = props.get(_name);
		return value != null ? value.equals("true") : false;
	}

	/**
	 * Returns the value of the property with the given name.
	 *
	 * @param _name          The name of the desired property.
	 * @param _default_value The default value of the property which is returned
	 *                      if the property does not have a specified value.
	 * @param return        The value of the property.
	 */
	public boolean getBoolean( String _name, boolean _default_value ) {
		Object value = props.get(_name);
		return value != null ? value.equals("true") : _default_value;
	}

	/**
	 * Returns the value of the property with the given name.
	 * '0' is returned if the property does not have a
	 * specified value.
	 *
	 * @param _name   The name of the desired property.
	 * @param return The value of the property.
	 */
	public int getInt( String _name ) {
		try {
			return Integer.parseInt((String) props.get(_name));
		} catch (Exception e) { }
		return -1;
	}

	/**
	 * Returns the value of the property with the given name.
	 *
	 * 	@param _name          The name of the desired property.
	 * @param _default_value The default value of the property which is returned
	 *                      if the property does not have a specified value.
	 * @param return        The value of the property.
	 */
	public int getInt( String _name, int _default_value) {
		try {
			return Integer.parseInt((String) props.get(_name));
		} catch (Exception e) { }
		return _default_value;
	}

	/**
	 * Returns the value of the property with the given name.
	 * '0' is returned if the property does not have a
	 * specified value.
	 *
	 * @param _name   The name of the desired property.
	 * @param return The value of the property.
	 */
	public double getDouble( String _name ) {
		try {
			return Double.parseDouble((String) props.get(_name));
		} catch (Exception e) { }
		return -1d;
	}

	/**
	 * Returns the value of the property with the given name.
	 *
	 * @param _name          The name of the desired property.
	 * @param _default_value The default value of the property which is returned
	 *                      if the property does not have a specified value.
	 * @param return        The value of the property.
	 */
	public double getDouble( String _name, double _default_value ) {
		try {
			return Double.parseDouble((String) props.get(_name));
		} catch (Exception e) { }
		return _default_value;
	}

	/**
	 * Removes the property with the given name.
	 *
	 * @param _name The name of the property to remove.
	 */
	public void remove( String _name ) {
		props.remove(_name);
	}

	/**
	 * Removes all the properties.
	 */
	public void removeAll() {
		props.clear();
	}

	/**
	 * Loads the property list from the configuration file.
	 *
	 * @return True if the file was loaded successfully, false if
	 *         the file does not exists or an error occurred reading
	 *         the file.
	 */
	public boolean load() {
		if ((config_file == null) && (config_url == null)) return false;
		// Loads from URL.
		if (config_url != null) {
			try {
				return load(new BufferedReader(new InputStreamReader(config_url.openStream())));
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		// Loads from file.
		else {
			try {
				if (!config_file.exists()) config_file.createNewFile();

				return load(new BufferedReader(new FileReader(config_file)));
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean load( BufferedReader _buffy ) throws IOException {
		Hashtable<String, Object> props = this.props;
		String line = null;
		while ((line = _buffy.readLine()) != null) {
			int eq_idx = line.indexOf('=');
			if (eq_idx > 0) {
				String name = line.substring(0, eq_idx).trim();
				String value = line.substring(eq_idx + 1).trim();
				props.put(name, value);
			}
		}
		_buffy.close();
		return true;
	}

	/**
	 * Saves the property list to the config file.
	 *
	 * @return True if the save was successful, false othewise.
	 */
	public boolean save() {
		if ( config_url != null ) return false;
		try {
			PrintWriter out = new PrintWriter(new FileWriter(config_file));
			return save(out);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean save( PrintWriter _out ) throws IOException {
		Hashtable<String, Object> props = this.props;
		Enumeration<String> names = props.keys();
		SortedStrings sorter = new SortedStrings();
		while ( names.hasMoreElements() ) {
			sorter.add((String) names.nextElement());
		}
		for (int i = 0; i < sorter.stringCount(); i++) {
			String name = sorter.stringAt(i);
			String value = (String) props.get(name);
			_out.print(name);
			_out.print("=");
			_out.println(value);
			System.out.println(name + '=' + value);

		}
		
		_out.close();
		return true;
	}

	public void storeCRC() {
		add("crc", generateCRC());
	}

	public boolean isValidCRC() {
		String crc = generateCRC();
		String stored_crc = (String) props.get("crc");
		if (stored_crc == null) return false;
		return stored_crc.equals(crc);
	}

	private String generateCRC() {
		Hashtable<String, Object> props = this.props;
		CRC32OutputStream crc = new CRC32OutputStream();
		PrintWriter pr = new PrintWriter(crc);
		Enumeration<String> names = props.keys();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (!name.equals("crc")) {
				pr.println((String) props.get(name));
			}
		}
		pr.flush();
		pr.close();
		return "" + crc.getValue();
	}
}