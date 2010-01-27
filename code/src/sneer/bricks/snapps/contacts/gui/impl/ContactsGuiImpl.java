package sneer.bricks.snapps.contacts.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.images.Images;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.heartbeat.stethoscope.Stethoscope;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.collections.listsorter.ListSorter;
import sneer.bricks.pulp.reactive.gates.strings.StringGates;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.main.synth.Synth;
import sneer.bricks.skin.main.synth.scroll.SynthScrolls;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.bricks.skin.popuptrigger.PopupTrigger;
import sneer.bricks.skin.widgets.reactive.LabelProvider;
import sneer.bricks.skin.widgets.reactive.ListWidget;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactImageProvider;
import sneer.bricks.snapps.contacts.gui.ContactTextProvider;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.contacts.gui.comparator.ContactComparator;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;

class ContactsGuiImpl implements ContactsGui {

	private final Synth _synth = my(Synth.class);
	{ _synth.notInGuiThreadLoad(this.getClass()); }

	private final Image ONLINE = getImage("ContactsGuiImpl.onlineIconName");
	private final Image OFFLINE = getImage("ContactsGuiImpl.offlineIconName");

	private ContactImageProvider _contactImageProvider;
	private List<ContactTextProvider> _contactTextProviders = new ArrayList<ContactTextProvider>();

	private final ListWidget<Contact> _contactList;

	private Container _container;

	ContactsGuiImpl() {
		_contactImageProvider = new ContactImageProvider() { @Override public Signal<Image> imageFor(Contact contact) {
			Signal<Boolean> isOnline = my(Stethoscope.class).isAlive(contact);
			return my(Signals.class).adapt(isOnline, new Functor<Boolean, Image>(){ @Override public Image evaluate(Boolean value) {
				return value ? ONLINE : OFFLINE;
			}});
		}};

		_contactTextProviders.add(
			new ContactTextProvider() {
				@Override public Position position() { return ContactTextProvider.Position.LEFT; }
				@Override public Signal<String> textFor(Contact contact) { return contact.nickname(); }
			}
		);

		final ListSignal<Contact> _sortedList = my(ListSorter.class).sort(my(Contacts.class).contacts() , my(ContactComparator.class), new SignalChooser<Contact>() { @Override public Signal<?>[] signalsToReceiveFrom(Contact contact) {
			return new Signal<?>[] { my(Stethoscope.class).isAlive(contact), contact.nickname() };
		}});

		final ByRef<ListWidget<Contact>> ref = ByRef.newInstance();
		my(GuiThread.class).invokeAndWait(new Closure() { @Override public void run() {
			ref.value = my(ReactiveWidgetFactory.class).newList(_sortedList, new ContactLabelProvider(), new ContactsGuiCellRenderer(new ContactLabelProvider()));
		}});
		_contactList = ref.value;

		my(InstrumentRegistry.class).registerInstrument(this);
	} 

	private Image getImage(String key) {
		return my(Images.class).getImage(ContactsGuiImpl.class.getResource((String) _synth.getDefaultProperty(key)));
	}

	@Override
	public void init(InstrumentPanel window) {
		_container = window.contentPane();
		my(ContactActionManager.class).setBaseComponent(_container);

		_contactList.getComponent().setName("ContactList");
		_synth.attach(_contactList.getComponent());
		
		JScrollPane scrollPane = my(SynthScrolls.class).create();
		scrollPane.getViewport().add(_contactList.getComponent());
		
		_container.setLayout(new BorderLayout());
		_container.add(scrollPane, BorderLayout.CENTER);
		
		addContactActions(window.actions());
		addDefaultContactAction();
		
		new ListContactsPopUpSupport();
	}

	private void addDefaultContactAction() {
		contactList().addMouseListener(new MouseAdapter(){ @Override public void mouseReleased(MouseEvent e) {
			if (e.getClickCount() > 1)
				my(ContactActionManager.class).defaultAction().run();
		}});
	}

	@Override
	public int defaultHeight() {
		return 144;
	}
	
	@Override
	public String title() {
		return "My Contacts";
	}
	
	@Override
	public Signal<Contact> selectedContact(){
		return _contactList.selectedElement();
	}
	
	private void addContactActions(MenuGroup<JPopupMenu> menuGroup) {
		menuGroup.addAction(new Action() {
			@Override public String caption() { return "New Contact..."; }
			@Override public void run() {
				contactList().setSelectedValue(newContact(), true);
			}});
	}
	
	private Contact newContact() {
		return my(Contacts.class).produceContact("<New Contact>");
	}

	private JList contactList() {
		return (JList)_contactList.getComponent();
	}	

	final class ContactLabelProvider implements LabelProvider<Contact> {

		@Override public Signal<Image> imageFor(final Contact contact) {
			return _contactImageProvider.imageFor(contact);
		}

		@Override public Signal<String> textFor(final Contact contact) {
			Signal<String>[] texts = my(CollectionUtils.class).map(_contactTextProviders, new Functor<ContactTextProvider, Signal<String>>() { @Override public Signal<String> evaluate(ContactTextProvider textProvider) throws RuntimeException {
				return textProvider.textFor(contact);
			}}).toArray(new Signal[0]);

			return my(StringGates.class).concat(" ", texts);
		}

	}
	
	private final class ListContactsPopUpSupport {
		private ListContactsPopUpSupport() {
			final JList list = _contactList.getMainWidget();
			my(PopupTrigger.class).listen(list, new Consumer<MouseEvent>(){ @Override public void consume(MouseEvent e) {
				tryToShowContactMenu(e);
			}});
		}
		
		private void tryToShowContactMenu(MouseEvent e) {
			JList list = _contactList.getMainWidget();
			int index = list.locationToIndex(e.getPoint());
			list.getSelectionModel().setSelectionInterval(index, index);
			if (!e.isPopupTrigger()) return;
			
			MenuGroup<JPopupMenu> popupMain = my(MenuFactory.class).createPopupMenu();
			for (ContactAction action : my(ContactActionManager.class).actions())
				if (action.isVisible())
					popupMain.addAction(action);

			if (popupMain.getWidget().getSubElements().length>0)
				popupMain.getWidget().show(e.getComponent(),e.getX(),e.getY());
		}
	}

	@Override
	public void registerContactTextProvider(ContactTextProvider textProvider) {
		_contactTextProviders.add(textProvider);
		Collections.sort(_contactTextProviders, new Comparator<ContactTextProvider>() { @Override public int compare(ContactTextProvider provider1, ContactTextProvider provider2) {
			final int result = provider1.position().compareTo(provider2.position());
			return (result == 0)
				? _contactTextProviders.indexOf(provider1) - _contactTextProviders.indexOf(provider2)
				: result; 
		}});
	}

}