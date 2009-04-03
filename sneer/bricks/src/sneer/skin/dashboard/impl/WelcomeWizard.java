/**
 * 
 */
package sneer.skin.dashboard.impl;

import static sneer.commons.environments.Environments.my;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import sneer.commons.environments.Environment;
import sneer.commons.environments.Environments;
import sneer.pulp.dyndns.ownaccount.DynDnsAccount;
import sneer.pulp.dyndns.ownaccount.DynDnsAccountKeeper;
import sneer.pulp.own.name.OwnNameKeeper;
import sneer.pulp.port.PortKeeper;
import sneer.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.skin.widgets.reactive.TextWidget;
import wheel.reactive.impl.IntegerParser;

class WelcomeWizard extends JDialog {

	private final Environment _environment;
	
	private TextWidget<JTextField>  _yourOwnName;
	private TextWidget<JTextField> _sneerPort;
	
	private final JTextField _dynDnsHost = new JTextField();
	private final JTextField _dynDnsUser = new JTextField();
	private final JTextField _dnyDnsPassword = new JTextField();

	static void showIfNecessary() {
		new WelcomeWizard();
	}
	
	WelcomeWizard() {
		_environment = my(Environment.class);
		setModal(true);
		initGui();
		restoreFieldData();
		setVisible(true);
	}

	private void initDynDnsAccount(String dynDnsHost, String dynDnsUserName, String dynDnsPassword) {
		if (dynDnsUserName == null) return;
	
		my(DynDnsAccountKeeper.class).accountSetter().consume(
				new DynDnsAccount(dynDnsHost, dynDnsUserName, dynDnsPassword));
	}

	private void initGui() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Welcome, please inform your data:");
		setBounds(10, 10, 350, 300);

		java.awt.Container pnl = getContentPane();
		
		ReactiveWidgetFactory factory = my(ReactiveWidgetFactory.class);
		
		OwnNameKeeper nameKeeper = my(OwnNameKeeper.class);
		_yourOwnName = factory.newTextField(nameKeeper.name(), nameKeeper.nameSetter());
		
		PortKeeper portKeeper = my(PortKeeper.class);
		_sneerPort = factory.newTextField(portKeeper.port(), new IntegerParser(portKeeper.portSetter()));
		
		pnl.setLayout(new GridLayout(6,1));
		pnl.add(_yourOwnName.getComponent());
		pnl.add(_sneerPort.getComponent());
		pnl.add(_dynDnsHost);
		pnl.add(_dynDnsUser);
		pnl.add(_dnyDnsPassword);
		
		final JButton btn = new JButton("Start My Sneer!");
		btn.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent ignored) {
			submit();						
		}});
		pnl.add(btn);

		label(_yourOwnName.getComponent(), "Your Own Name");
		label(_sneerPort.getComponent(), "Your Sneer Port");
		label(_dynDnsHost, "Your DynDns Host [optional]");
		label(_dynDnsUser, "Your DynDns User [optional]");
		label(_dnyDnsPassword, "Your DynDns Password [optional]");
	}

	private void submit() {
		Environments.runWith(_environment, new Runnable(){ @Override public void run() {
			try {
				storeFieldData();
				setVisible(false);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(WelcomeWizard.this, ex.getMessage());
				ex.printStackTrace();
			}
		}});
	}
	
	private void restoreFieldData() {
		DynDnsAccount account = my(DynDnsAccountKeeper.class).ownAccount().currentValue();
		if (account == null) return;
		_dynDnsHost.setText(account.host);
		_dynDnsUser.setText(account.user);
		_dnyDnsPassword.setText(account.password);
	}

	private void storeFieldData() throws Exception {
		initDynDnsAccount(trim(_dynDnsHost), trim(_dynDnsUser), trim(_dnyDnsPassword));
	}

	private void label(JComponent field, String caption) {
		field.setBorder(new TitledBorder(caption));
	}

	private String trim(JTextField field) {
		return field.getText().trim();
	}
}