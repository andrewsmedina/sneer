package dfcsantos.tracks.client.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;

public class TrackClientTest extends BrickTest {

	@Bind private final FileClient _fileClient = mock(FileClient.class);
	@Bind private final FileWriter _fileWriter = mock(FileWriter.class);
	
	@Test(timeout=4000)
	public void trackDownload() throws IOException {
		my(OwnTracksFolderKeeper.class).setOwnTracksFolder(tmpFolder());
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[]{1});
		checking(new Expectations(){{
			exactly(1).of(_fileClient).fetchToCache(hash1);
			exactly(1).of(_fileWriter).writeAtomicallyTo(new File(candidatesFolder(), "foo.mp3"), 42, hash1);
		}});
		my(TrackClient.class);
		TrackEndorsement trackEndorsement = new TrackEndorsement("songs/subfolder/foo.mp3", 42, hash1);
		stamp(trackEndorsement);
		my(TupleSpace.class).acquire(trackEndorsement);
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		
	}

	private void stamp(TrackEndorsement trackEndorsement) {
		trackEndorsement.stamp(my(Seals.class).sealGiven(my(ContactManager.class).produceContact("Someone Else")), 1234);
	}

	private File candidatesFolder() {
		return my(OwnTracksFolderKeeper.class).peerTracksFolder().currentValue();
	}
}