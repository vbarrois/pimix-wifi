package websocket;

import websocket.WSClientEvent.WEBCLIENTEVENT;

public interface WSClientListener {
	public void state_changed( WSClientEvent _event );
	public boolean isRegistered( WEBCLIENTEVENT _type );
}
