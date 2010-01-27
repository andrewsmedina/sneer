package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.snapps.contacts.gui.ContactTextProvider;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.gui.WusicGui;

/**
 *
 * @author daniel
 */
class WusicGuiImpl implements WusicGui {

    private static final Wusic _controller = my(Wusic.class);

    private JFrame _frame;
    private MainPanel _mainPanel;

    private boolean _isInitialized = false;

    {
		my(MainMenu.class).addAction("Wusic", new Closure() { @Override public void run() {
			if (!_isInitialized){
				_isInitialized = true;
				_frame = initFrame();
				_controller.start();
			}
			_frame.setVisible(true);
		}});

		registerPlayingTrackTextProvider();
	}

	private JFrame initFrame() {
		JFrame result = my(ReactiveWidgetFactory.class).newFrame(title()).getMainWidget();

		_mainPanel = new MainPanel(PREFERRED_SIZE);
		result.getContentPane().add(_mainPanel);

    	result.setResizable(false);
		result.pack();

    	return result;
	}

	private Signal<String> title() {
		return my(Signals.class).adapt(_controller.playingTrackName(), new Functor<String, String>() { @Override public String evaluate(String track) {
			return "Wusic :: " + track;
		}});
	}

	private void registerPlayingTrackTextProvider() {
		my(ContactsGui.class).registerContactTextProvider(
			new ContactTextProvider() {
				@Override public Position position() {
					return ContactTextProvider.Position.RIGHT; 
				}

				@Override
				public Signal<String> textFor(Contact contact) {
					return my(Signals.class).adapt(my(PlayingTrackKeeper.class).playingTrack(contact), new Functor<String, String>() { @Override public String evaluate(String playingTrack) throws RuntimeException {
						return playingTrack.isEmpty()
							? ""
							: MUSICAL_NOTE_ICON + " " + playingTrack;
					}});
				}
			}
		);
	}

}
