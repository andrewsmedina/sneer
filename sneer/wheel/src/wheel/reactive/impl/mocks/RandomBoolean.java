package wheel.reactive.impl.mocks;

import java.util.Random;

import wheel.lang.Daemon;
import wheel.lang.Threads;
import wheel.reactive.Register;
import wheel.reactive.Signal;
import wheel.reactive.impl.RegisterImpl;

public class RandomBoolean {

	private static final Random RANDOM = new Random();
	private Register<Boolean> _register = new RegisterImpl<Boolean>(false);

	{
		new Daemon("Random Boolean") { @Override public void run() {
			while (true) sleepAndFlip();
		}};
	}
	
	public Signal<Boolean> output() {
		return _register.output();
	}

	private void sleepAndFlip() {
		Threads.sleepWithoutInterruptions(RANDOM.nextInt(6000));
		_register.setter().consume(!_register.output().currentValue());
	}
	
}
