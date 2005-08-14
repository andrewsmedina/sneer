package sneer;

import java.util.Date;

import org.prevayler.Transaction;

public class ContactRemoval implements Transaction {

	private final String _nickname;
	
	public ContactRemoval(String nickname) {
		_nickname = nickname;
	}

	public void executeOn(Object home, Date ignored) {
		((Home)home).life().forgetNickname(_nickname);
	}

	private static final long serialVersionUID = 1L;
}
