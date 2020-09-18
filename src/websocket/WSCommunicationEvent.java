package websocket;

import org.json.JSONObject;

public class WSCommunicationEvent {
	
	public COMEVENT eventType;
	public static enum COMEVENT{ SCAN };
	public JSONObject data;
	
	public WSCommunicationEvent( COMEVENT _eventType, JSONObject _data ) {
		this.eventType = _eventType;
		this.data = _data;
	}
	
	public WSCommunicationEvent( COMEVENT _eventType ) {
		this.eventType = _eventType;
		this.data = null;
	}
}
