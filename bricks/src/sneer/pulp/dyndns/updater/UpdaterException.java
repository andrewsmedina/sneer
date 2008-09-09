package sneer.pulp.dyndns.updater;

import wheel.lang.exceptions.FriendlyException;

public abstract class UpdaterException extends FriendlyException {

	private static final long serialVersionUID = 1L;

	public UpdaterException(String message, String help) {
		super(message, help);
	}

	public UpdaterException(Throwable cause, String help) {
		super(cause, help);
	}
}
