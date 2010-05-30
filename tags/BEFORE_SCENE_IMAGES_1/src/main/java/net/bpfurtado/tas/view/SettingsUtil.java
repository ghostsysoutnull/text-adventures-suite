/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com] - 2005
 *
 * This file is part of the Text Adventures Suite.
 * 
 * Text Adventures Suite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Text Adventures Suite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Text Adventures Suite.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Project page: http://code.google.com/p/text-adventures-suite/
 */
package net.bpfurtado.tas.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.bpfurtado.tas.Conf;

public class SettingsUtil
{
	public static void addSettingsMenu(JMenuBar menuBar, final Conf conf)
	{
		JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setMnemonic('t');
        final JMenuItem openLastAdventureMenuItem = new JCheckBoxMenuItem("Open last adventure on start");
        
        openLastAdventureMenuItem.setSelected(conf.is("openLastAdventureOnStart", false));
        
        openLastAdventureMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openLastAdventureOnStartMenuItemAction(openLastAdventureMenuItem.isSelected(), conf);
			}
		});
		settingsMenu.add(openLastAdventureMenuItem);
        menuBar.add(settingsMenu);
	}

	private static void openLastAdventureOnStartMenuItemAction(Boolean isSelected, Conf conf)
	{
		conf.set("openLastAdventureOnStart", isSelected.toString());
		conf.save();
	}
}
