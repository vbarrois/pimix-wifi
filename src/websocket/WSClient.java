package websocket;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import websocket.WSCommunicationEvent.COMEVENT;
import wifi.WIFIListener;
import websocket.WSClientEvent.WEBCLIENTEVENT;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class WSClient extends Thread implements WIFIListener {
    
	private static Log log = LogFactory.getLog(WSClient.class);
	
	private boolean 	bInitialized = false;
	
	public static int INFO 		= 0;
	public static int WARNING 	= 1;
	public static int DANGER 	= 2;
	
	private String 		sHost;
	private int			iPort;
	private Socket 		oSocketClient;
	
	private transient ArrayList<WSClientListener> listeners;
	
    public WSClient( String _host, int _port, String _id) {
    	sHost = _host;
    	iPort = _port;
    	//
    	log.info("Try to connect to " + _host + " on port " + _port);
    	//
    	listeners = new ArrayList<WSClientListener>();
    	//
		try {
			IO.Options opts = new IO.Options();
			opts.query = "binding= { \"component\": \"wifiscanner\", \"ID\": \"" + _id + "\" }";
			oSocketClient = IO.socket( "http://"+sHost+":"+iPort, opts );
			
			oSocketClient.io().on( Manager.EVENT_TRANSPORT, new Emitter.Listener() {
		    	  @Override
		    	  public void call(Object... args) {
		    		  Transport transport = (Transport)args[0];
		    		  
		    		  transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
		    			  @Override
		    			  public void call(Object... args) {
		    				  // @SuppressWarnings("unchecked")
		    				  // Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
		    				  // modify request headers
		    				  try {
		    					  // headers.put("mixer", Arrays.asList( convertToUTF8(PiWifiScan.o_preferences.getJSON().toString())) );
		    				  } catch(Exception ex) {
		    					  
		    				  }
		    			  }
		    		  });
		    	  }
		    } );
			oSocketClient
			.on( Socket.EVENT_CONNECT, new Emitter.Listener() {
		    	  @Override
		    	  public void call(Object... args) { onconnect(); }
		    } )
			.on( "message", new Emitter.Listener() {
		    	  @Override
		    	  public void call(Object... args) {
		    		  log.info( "Socket event -> " + args[0].toString() );
		    	  }
		    } )
			.on( "command", new Emitter.Listener() {
		    	  @Override
		    	  public void call(Object... args) {
		    		  execute( args[0].toString(), args[1].toString() );
		    	  }
		    } )
			.on( Socket.EVENT_DISCONNECT, new Emitter.Listener() {
		    	  @Override
		    	  public void call(Object... args) { ondisconnect(); }
			} );
			bInitialized = true;
		} catch ( URISyntaxException e ) {
			bInitialized = false;
		}
    }
    
    public boolean isInitialized() {
    	return bInitialized;
    }
    
    public void disconnect() {
    	oSocketClient.disconnect();
    }
    
    private void ondisconnect() {
    	log.info( "disconnected" );
    }
    
    private void onconnect() {
    	log.info( "connected" );
    	notifyEvent(new WSClientEvent(this, WEBCLIENTEVENT.CONNECTED, null) );
    }
    
    private void emit( String _command, JSONObject _comobject ) {
    	if (oSocketClient.connected())
    		oSocketClient.emit(_command, convertToUTF8(_comobject.toString()));
    }
    
    private void execute( String _command, String _from ) {
    	if (oSocketClient.connected()) {
    		try {
    			JSONObject cmd = new JSONObject(_command);
    			JSONObject from = new JSONObject(_from);
    			String action = cmd.getString("action");
    			log.info( "execute command " + action + " from " + _from );
    	    	if( action.toUpperCase().equals( "SCAN" ) ) notifyEvent(new WSClientEvent(this, WEBCLIENTEVENT.SCAN, from ) );
    	    	else if( action.toUpperCase().equals( "CONNECT" ) ) notifyEvent(new WSClientEvent(this, WEBCLIENTEVENT.CONNECT, cmd.getJSONObject("network"), from ) );
    		} catch (JSONException e) {
    			log.error("cannot parse command " + e);
    		}		
    	}
   	}
    
    public void run() {
    	// Connect
	    oSocketClient.connect();
    }
    
    /**
	 * UPDATE EVENTS MANAGEMENT
	 * @param _listener
	 */
	public void addListener( WSClientListener _listener ) {
		if( _listener != null ) {
			if ( !listeners.contains( _listener ) ) {
				this.listeners.add( _listener );
			}
		}
	}

	public void removeListener( WSClientListener _listener ) {
		if( _listener != null ) {
			if ( listeners.contains( _listener ) ) {
				this.listeners.remove( _listener );
			}
		}
	}

	public void removeAllListeners() {
		listeners = new ArrayList<WSClientListener>();
	}

	public void notifyEvent( WSClientEvent _event ) {
		if( listeners != null && listeners.size() > 0 ) {
			Object[] tl = listeners.toArray();
			for( int i = 0; i<tl.length ; i++ ) {
				WSClientListener listener = ((WSClientListener)tl[i]);
				if( listener.isRegistered( _event.eventType ) ) {
					listener.state_changed( _event );
				}
			}
		}
	}

	/** WIFI SCANNER EVENTS */
	@Override
	public void state_changed(WSCommunicationEvent _event) {
		switch(_event.eventType) {
			case SCAN:
				emit("scan", _event.data);
				break;
		}
	}

	@Override
	public boolean isRegistered(COMEVENT _type) {
		return true; // register all messages
	}
	
	public  String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes(), "UTF-8" );
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
}