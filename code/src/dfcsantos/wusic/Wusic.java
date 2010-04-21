package dfcsantos.wusic;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.PickyConsumer;
import dfcsantos.tracks.Track;

@Brick
public interface Wusic {

	enum OperatingMode { OWN, PEERS };
	void setOperatingMode(OperatingMode operatingMode);
	Signal<OperatingMode> operatingMode();

	File playingFolder();
	void setPlayingFolder(File selectedFolder);

	Signal<File> sharedTracksFolder();
	void setSharedTracksFolder(File selectedFolder);

	void setShuffle(boolean shuffle);

	void start();
	void pauseResume();
//	void back();
	void skip();
	void stop();

	void meToo();
	void deleteTrack();

	Signal<Boolean> isPlaying();
	Signal<Track>	playingTrack();
	Signal<Integer> playingTrackTime();

	Signal<Integer> numberOfOwnTracks();
	Signal<Integer> numberOfPeerTracks();

	Signal<Boolean> isTrackDownloadActive();
	Consumer<Boolean> trackDownloadActivator();
	SetSignal<Download> activeTrackDownloads();

	int DEFAULT_TRACKS_DOWNLOAD_ALLOWANCE = 100; // MBs
	Signal<Integer> trackDownloadAllowance();
	PickyConsumer<Integer> trackDownloadAllowanceSetter();
	void volumePercent(int level);
	int volumePercent();
}

