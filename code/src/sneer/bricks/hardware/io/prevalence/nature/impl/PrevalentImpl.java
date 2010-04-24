package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.prevalence.map.PrevalentMap;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.hardware.io.prevalence.state.PrevalenceDispatcher;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class PrevalentImpl implements Prevalent {

	private final Prevayler _prevayler = createPrevayler(prevalenceBase());

	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;


	{
		_refToAvoidGc = my(Threads.class).crashing().addPulseReceiver(new Closure() { @Override public void run() {
			crash();
		}});
	}
	
	
	@Override
	public List<ClassDefinition> realize(ClassDefinition classDef) {
		return Arrays.asList(classDef);
	}

	
	@Override
	public synchronized <T> T instantiate(final Class<T> brick, Class<T> implClassIgnored, final Producer<T> instantiator) {
		final Producer<T> insidePrevalence = new RegisteringProducer<T>(instantiator);
		
		Producer<T> toEnterPrevalence = new Producer<T>() { @Override public T produce() {			
			PrevalentBuilding building = (PrevalentBuilding) _prevayler.prevalentSystem();
			T existing = building.provide(brick);
			return existing != null
				? existing
				: (T)_prevayler.execute(new InstantiateBrick<T>(brick, insidePrevalence));
		}};
		
		return Bubble.wrap(my(PrevalenceDispatcher.class).produce(toEnterPrevalence, insidePrevalence), _prevayler);
	}
	
	
	private static final class RegisteringProducer<T> implements Producer<T> {
		private final Producer<T> _delegate;

		private RegisteringProducer(Producer<T> instantiator) {
			_delegate = instantiator;
		}

		@Override public T produce() {
			return my(PrevalentMap.class).register(_delegate.produce());
		}
	}

	
	private <T> File prevalenceBase() {
		return my(FolderConfig.class).storageFolderFor(Prevalent.class);
	}

	
	private Prevayler createPrevayler(final File prevalenceBase) {
		final PrevaylerFactory factory = createPrevaylerFactory(new PrevalentBuilding(), prevalenceBase);

		return my(PrevalenceDispatcher.class).produce(new Producer<Prevayler>() { @Override public Prevayler produce() throws RuntimeException {
			try {
				return factory.create();
			} catch (IOException e) {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
			} catch (ClassNotFoundException e) {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
			}
		}});
	}

		
	private PrevaylerFactory createPrevaylerFactory(Object system, File prevalenceBase) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(system);
		factory.configurePrevalenceDirectory(prevalenceBase.getAbsolutePath());
		factory.configureTransactionFiltering(false);
		factory.configureJournalSerializer("xstreamjournal", new SerializerAdapter());
		return factory;
	}
	
	
	private void crash() {
		try {
			_prevayler.close();
		} catch (IOException e) {
			my(Logger.class).log("Exception closing prevayler: " + e);
		}
	}
}