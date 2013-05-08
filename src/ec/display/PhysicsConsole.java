/*
  Copyright 2006 by Sean Paus
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

/*
 * Created on Feb 3, 2005
 *
 */
package ec.display;

import java.awt.HeadlessException;
import java.io.File;

import ec.display.Console;


public class PhysicsConsole extends Console {
	public PhysicsConsole(String[] clArgs) throws HeadlessException {
		super(clArgs);
	}

	public static void main(String[] args) {
		PhysicsConsole application = new PhysicsConsole(args);
		application.setVisible(true);

		application
				.loadParameters(new File(
						"src/ec/app/knock/knock.params"));
		application.playButton.setEnabled(true);
		application.stepButton.setEnabled(true);
		application.conPanel.enableControls();

	}
} 
