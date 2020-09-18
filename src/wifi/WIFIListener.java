package wifi;

import websocket.WSCommunicationEvent;
import websocket.WSCommunicationEvent.COMEVENT;

public interface WIFIListener {
	public void state_changed( WSCommunicationEvent _event );
	public boolean isRegistered( COMEVENT _type );
}
