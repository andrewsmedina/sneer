package sneer.bricks.snapps.wackup.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.After;
import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.snapps.wackup.BlockNumberOutOfRange;
import sneer.bricks.snapps.wackup.Wackup;

public class WackupTest extends BrickTestWithTuples {

	private static final int BLOCK_SIZE = 8 * 1024;

	private static final byte[] BLANK_BLOCK = new byte[BLOCK_SIZE];

	private final Wackup _subject = my(Wackup.class);

	@Test (expected = BlockNumberOutOfRange.class)
	public void readOutOfRange() throws Exception {
		_subject.read(42);
	}

	@Test
	public void readWithoutWrite() throws Exception {
		_subject.setSize(1);
		assertArrayEquals(BLANK_BLOCK, _subject.read(0));

		_subject.setSize(42);
		assertArrayEquals(BLANK_BLOCK, _subject.read(41));
	}

	@Test
	public void write() throws Exception {
		_subject.setSize(10);
		_subject.write(7, new byte[] { 0, 1, 2 });
		
		byte[] block = _subject.read(7);
		assertStartsWith(new byte[] { 0, 1, 2 }, block);
		for (int i = 3; i < block.length; i++)
			assertEquals(0, block[i]);
	}

	@Test
	public void resizing() throws Exception {
		_subject.setSize(10);
		_subject.write(7, new byte[] { 42 });

		_subject.setSize(20);

		byte[] block = _subject.read(7);
		assertStartsWith(new byte[] { 42 }, block);

	}

	@After
	public void afterWackupTest() {
		_subject.crash();
	}

}