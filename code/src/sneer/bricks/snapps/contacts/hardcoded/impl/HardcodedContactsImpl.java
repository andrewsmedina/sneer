package sneer.bricks.snapps.contacts.hardcoded.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.snapps.contacts.hardcoded.HardcodedContacts;
import sneer.foundation.lang.arrays.ImmutableByteArray;
import sneer.foundation.lang.exceptions.NotImplementedYet;
import sneer.foundation.lang.exceptions.Refusal;

public class HardcodedContactsImpl implements HardcodedContacts {

	
	private final Contacts _contactManager = my(Contacts.class);

	
	HardcodedContactsImpl() {
		if(!_contactManager.contacts().currentElements().isEmpty()) {
			for (Contact contact : _contactManager.contacts().currentElements()) {
				System.out.println(contact.nickname());
			}
			return;
		}
				
		for (ContactInfo contact : contacts())
			add(contact);
	}
	
	
	private void add(ContactInfo contact) {
		if (my(OwnSeal.class).get().currentValue().equals(contact._seal))
			return;
		
		addAddresses(contact);
		addSeal(contact);
	}

	
	private void addSeal(ContactInfo contact) {
		if (contact._seal == null) return;
		try {
			my(ContactSeals.class).put(contact._nick, contact._seal);
		} catch (Refusal e) {
			throw new NotImplementedYet(e);
		}
	}

	
	private void addAddresses(ContactInfo contactInfo) {
		String nick = contactInfo._nick;
		Contact contact = _contactManager.produceContact(nick);
		my(InternetAddressKeeper.class).add(contact, contactInfo._host, contactInfo._port);
	}

	
	private ContactInfo[] contacts() {
		return new ContactInfo[] {
			new ContactInfo("Bamboo","rbo.selfip.net",5923),
//			new ContactInfo("Bihaiko", "bihaiko.dyndns.org", 6789),
//			new ContactInfo("Daniel Santos", "dfcsantos.homeip.net", 7777),
			new ContactInfo("Dummy", "localhost", 7777, newSeal("1b7b8e78558d0389fc39ed3fc3f6d588a1c40af8cdac9aaf1f7b918f508589b5d2ec9b5bec0179926140c2cabe8ec202e8529421fc60380cac123f97a81e7608")),
			new ContactInfo("Edmundo", "edmundo.selfip.net", 8888, newSeal("cd27ee9965cc808ffb2f5379d8c246dd26e050541927ef886541ef0c7e7af527ae98c87075418806748f72f7ef60496d49d6ab317f9c08f75f542253b3487014")),
			new ContactInfo("Igor Arouca", "igorarouca.selfip.net", 6789, newSeal("5865e61278e15a24546be3042cf8caee95b7399cd194e80fad9a15b516e785bba9c0f80f604bb5a0bfda26ea2a7459361a55280b8e321256f866b876c33ff286")),
			new ContactInfo("Kalecser", "kalecser.dyndns.org", 7770, newSeal("535e7bf346a0b398b43621c03c4810f685d80014a7197e8adb5ecc9ba35af01e76589b2a0dc0661ca5c55a4d45aa11a6aedba97f1e68665a75b80008c65b998b")),
			new ContactInfo("Klaus", "klausw.selfip.net", 5923, newSeal("9fa8ae50bde46dc175527015afc3d9005cfccb2dfaaac7c51d8c854419bb5381efb34a15876cef1e25d170babd451d25e3d5e20a96404094a0e62c94524755b8")),
			new ContactInfo("Patrick Roemer", "judgefang.dontexist.net", 4711, newSeal("7413fe5a316c22970a4b1dc44a686a176f743618be4437131986d17df6841ec4443ec22994672322ed0c0ae78ba921072dd60c9d8e3adbfae9d6f9e16381ef5e")),
		};
	}


	private Seal newSeal(String sealString) {
		try {
			return new Seal(new ImmutableByteArray(my(Codec.class).hex().decode(sealString)));
		} catch (DecodeException e) {
			throw new IllegalStateException(e);
		}
	}


	static class ContactInfo {
		final String _nick;
		final String _host;
		final int _port;
		final Seal _seal;

		ContactInfo(String nick, String host, int port) {
			this(nick, host, port, null);
		}

		ContactInfo(String nick, String host, int port, Seal seal) {
			_nick = nick;
			_host = host;
			_port = port;
			_seal = seal;
		}
	}

}