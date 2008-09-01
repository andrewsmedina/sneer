package snapps.contacts.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;

import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import snapps.contacts.ContactsSnapp;
import sneer.kernel.container.Inject;
import sneer.pulp.connection.ConnectionManager;
import sneer.pulp.contacts.Contact;
import sneer.pulp.contacts.ContactManager;
import sneer.skin.snappmanager.SnappManager;
import sneer.skin.widgets.reactive.LabelProvider;
import sneer.skin.widgets.reactive.ListWidget;
import sneer.skin.widgets.reactive.RFactory;
import wheel.graphics.Images;
import wheel.lang.Functor;
import wheel.reactive.Signal;
import wheel.reactive.impl.Adapter;

public class ContactsSnappImpl implements ContactsSnapp {

	private static final Image ONLINE = getImage("online.png");
	private static final Image OFFLINE = getImage("offline.png");

	@Inject
	static private ContactManager _contacts;

	@Inject
	static private RFactory _rfactory;

	@Inject
	static private ConnectionManager _connectionManager;

	private ListWidget<Contact> _contactList;

	@Inject
	static private SnappManager _snapps;
	
	public ContactsSnappImpl(){
		_snapps.registerSnapp(this);
	} 

	private static Image getImage(String fileName) {
		return Images.getImage(ContactsSnappImpl.class.getResource(fileName));
	}
	
	@Override
	public void init(Container container) {	
		_contactList = _rfactory.newList(_contacts.contacts(),
				new LabelProvider<Contact>() {

					@Override
					public Signal<Image> imageFor(Contact contact) {
						
						Signal<Boolean> isOnline = _connectionManager.connectionFor(contact).isOnline();
						
						Functor<Boolean, Image> functor = new Functor<Boolean, Image>(){
							@Override
							public Image evaluate(Boolean value) {
								return value?ONLINE:OFFLINE;
							}};
						
						Adapter<Boolean, Image> imgSource = new Adapter<Boolean, Image>(isOnline, functor);
						return imgSource.output();	
					}

					@Override
					public Signal<String> labelFor(Contact contact) {
						return contact.nickname();
					}
				});
		
		container.setLayout(new BorderLayout());
		container.add(_contactList.getComponent(), BorderLayout.CENTER);
		_contactList.getComponent().setBorder(new TitledBorder(new EmptyBorder(5,5,5,5), "My Contacts"));
	}

	@Override
	public String getName() {
		return "My Contacts";
	}
}