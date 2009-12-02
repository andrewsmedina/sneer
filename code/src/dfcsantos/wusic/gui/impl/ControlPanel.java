package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

abstract class ControlPanel extends JPanel {

	private static final Wusic _controller = my(Wusic.class);

	private final JButton _pauseResume	= new JButton();
//	private final JButton _back			= new JButton();
	private final JButton _skip			= new JButton();
	private final JButton _stop			= new JButton();

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	ControlPanel() {
		super(new FlowLayout(FlowLayout.LEFT, 9, 3));

	    toAvoidGC = _controller.isPlaying().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isPlaying) {
	    	if (isMyOperatingMode())
	    		_pauseResume.setText(isPlaying ? "||" : ">");
	    	else
	    		_pauseResume.setText(">");
		}});

	    _pauseResume.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent evt) {
	    	pauseResumeActionPerformed();
        }});
	    add(_pauseResume);
	
//	    _back.setText("<<");
//	    _back.addActionListener(new ActionListener() {
//	        public void actionPerformed(ActionEvent evt) {
//	            backActionPerformed();
//	        }
//	    });
//	    add(_back);
	
	    _skip.setText(">>");
	    _skip.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent evt) {
	    	skipActionPerformed();
	    }});
	    add(_skip);
	
	    _stop.setText("\u25A1"); // Unicode for 'square symbol'
	    _stop.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent evt) {
	    	stopActionPerformed();
	    }});
	    add(_stop);
	}

	void update(OperatingMode operatingMode) {
		if (isMyOperatingMode(operatingMode))
			enableButtons();
		else
			disableButtons();
	}

	void enableButtons() {
		_skip.setEnabled(true);
		_stop.setEnabled(true);
	}

	void disableButtons() {
		_skip.setEnabled(false);
		_stop.setEnabled(false);
	}

	private void pauseResumeActionPerformed() {
		switchOperatingModeIfNecessary();
		_controller.pauseResume();
	}

	private void switchOperatingModeIfNecessary() {
		if (isMyOperatingMode()) return;

		_controller.switchOperatingMode();
	}

//	private void backActionPerformed() {
//	    Wusic.back();
//	}

	private void skipActionPerformed() {
	    _controller.skip();
	}

	private void stopActionPerformed() {
	    _controller.stop();
	}

	private boolean isMyOperatingMode() {
		return isMyOperatingMode(_controller.operatingMode().currentValue());
	}

	abstract boolean isMyOperatingMode(OperatingMode operatingMode);

}