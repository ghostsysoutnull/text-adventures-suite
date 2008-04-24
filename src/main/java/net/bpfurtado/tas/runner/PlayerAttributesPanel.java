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
package net.bpfurtado.tas.runner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

public class PlayerAttributesPanel
{
	private static Logger logger = Logger.getLogger(PlayerAttributesPanel.class);
	
	private JPanel main;
	private GridBagConstraints c;
	
	private int line = 0;

	PlayerAttributesPanel()
	{
		main = new JPanel(new GridBagLayout());
		main.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		main.setAlignmentY(JPanel.TOP_ALIGNMENT);
		main.setBorder(BorderFactory.createRaisedBevelBorder());
		main.removeAll();

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
	}

	int addLine(Object key, Object value)
	{
		JLabel name = new JLabel();

		logger.debug("attributeEntry.getKey()=" + key);

		name.setFont(PlayerPanelController.DEFAULT_FONT);
		c.weightx = .75;
		c.gridx = 0;
		c.gridy = line;
		if (key.equals(value)) {
			name.setText(" "+ key.toString());
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.LINE_END;
			main.add(name, c);
			return ++line;
		} else {
			name.setText(" " + key + ": ");
			name.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.gray));
			c.anchor = GridBagConstraints.LINE_END;
			main.add(name, c);
		}

		String valueStr = " " + value + " ";
		JLabel valueLb = new JLabel(valueStr);
		valueLb.setHorizontalAlignment(SwingConstants.RIGHT);
		valueLb.setFont(PlayerPanelController.DEFAULT_FONT);
		c.weightx = .25;
		c.gridx = 1;
		c.gridy = line++;
		c.anchor = GridBagConstraints.LINE_END;
		main.add(valueLb, c);
		
		main.setMaximumSize(new Dimension(200, line * 25));
		
		return line;
	}
	
	void setAsHavingNoAttributes()
	{
		c.weightx = .75;
		c.gridx = 0;
		c.gridy = line;
		c.anchor = GridBagConstraints.LINE_END;
		main.add(new JLabel("No attributes"), c);
	}

	JComponent getPanel()
	{
		return main;
	}
}
