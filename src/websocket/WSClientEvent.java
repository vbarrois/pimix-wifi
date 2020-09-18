package websocket;

import org.json.JSONObject;

public class WSClientEvent {
	public WSClient source;
	public WEBCLIENTEVENT eventType;
	public static enum WEBCLIENTEVENT{ 
		CONNECTED,
		SCAN,
		CONNECT
	};
	public JSONObject json;
	public JSONObject from;
	
	public WSClientEvent (WSClient _source, WEBCLIENTEVENT _eventType, JSONObject _from) {
		this.source = _source;
		this.eventType = _eventType;
		this.json = null;
		this.from = _from;
	}
	
	public WSClientEvent( WSClient _source, WEBCLIENTEVENT _eventType, JSONObject _json, JSONObject _from ) {
		this.source = _source;
		this.eventType = _eventType;
		this.json = _json;
		this.from = _from;
	}
	
	public JSONObject getProperties() {
		return json;
	}
	
	public JSONObject getFrom() {
		return from;
	}
}
