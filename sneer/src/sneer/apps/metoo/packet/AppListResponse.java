package sneer.apps.metoo.packet;

import java.util.Map;

import sneer.apps.metoo.MeTooPacket;


public class AppListResponse implements MeTooPacket {

	private static final long serialVersionUID = 1L;

	public final Map<String, String> _installNameAndAppUID;
	
	public AppListResponse(Map<String,String> installNameAndAppUID){
		_installNameAndAppUID = installNameAndAppUID;
	}
	public int type() {
		return APP_LIST_RESPONSE; 
	}

}
