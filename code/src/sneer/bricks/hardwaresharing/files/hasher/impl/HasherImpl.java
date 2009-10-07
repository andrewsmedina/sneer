package sneer.bricks.hardwaresharing.files.hasher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Digester;
import sneer.bricks.pulp.crypto.Sneer1024;

class HasherImpl implements Hasher {

	@Override
	public Sneer1024 hash(byte[] contents) {
		return my(Crypto.class).digest(contents);
	}
	
	
	@Override
	public Sneer1024 hash(FolderContents folder) {
		Digester digester = my(Crypto.class).newDigester();
		for (FileOrFolder entry : folder.contents)
			digester.update(hash(entry).bytes());
		return digester.digest();
	}

	
	@Override
	public Sneer1024 hash(File file) throws IOException {
		return hash(my(IO.class).files().readBytes(file));
	}

	
	private static Sneer1024 hash(FileOrFolder entry) {
		Digester digester = my(Crypto.class).newDigester();
		digester.update(bytesUtf8(entry.name));
		digester.update(BigInteger.valueOf(entry.lastModified).toByteArray());
		digester.update(entry.hashOfContents.bytes());
		return digester.digest();
	}

	
	private static byte[] bytesUtf8(String string) {
		try {
			return string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}