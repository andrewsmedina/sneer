package metoo.packet;

import java.util.Map;

import metoo.MeTooPacket;

public class AppListResponse implements MeTooPacket{

	public final Map<String, String> _nameAndAppUID;
	
	public AppListResponse(Map<String,String> nameAndAppUID){
		_nameAndAppUID = nameAndAppUID;
	}
	public int type() {
		return APP_LIST_RESPONSE;
	}

}
