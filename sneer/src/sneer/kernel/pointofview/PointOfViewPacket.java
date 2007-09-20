package sneer.kernel.pointofview;

import java.io.Serializable;

import wheel.graphics.JpgImage;

public class PointOfViewPacket implements Serializable {
	public final String _name;
	public final String _thoughtOfTheDay;
	public final JpgImage _picture;
	public final String _profile;

	public PointOfViewPacket(String name, String thoughtOfDay, JpgImage picture, String profile){
		_name = name;
		_thoughtOfTheDay = thoughtOfDay;
		_picture = picture;
		_profile = profile;
	}

	private static final long serialVersionUID = 1L;
}
