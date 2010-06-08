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
package net.bpfurtado.tas.runner.combat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.bpfurtado.tas.model.combat.AttackResult;
import net.bpfurtado.tas.model.combat.AttackResultListener;
import net.bpfurtado.tas.model.combat.AttackResultType;
import net.bpfurtado.tas.model.combat.Fighter;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

import static javax.swing.BorderFactory.*;

public class FighterView implements AttackResultListener
{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(FighterView.class);

	private static final Font STRONG_FONT = new Font("Tahoma", 1, 14);
	private static final Font DEFAULT_FONT = new Font("Tahoma", 0, 14);
	private static final Color LIGHT_RED_COLOR = new Color(0xfa8181);
	
	private Fighter fighter;

	private JPanel panel;
	private JLabel diceLb;
	private JLabel staminaLb;
	private JToggleButton chooseBt;

	FighterView(Fighter fighter)
	{
		this.fighter = fighter;
		fighter.addAtackResultListener(this);

		widgets(fighter);
		
		fighter.setView(this);
	}

	private void widgets(Fighter f)
	{
	    
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		line(panel, f.getName(), STRONG_FONT);
		space(panel);

		line(panel, "Skill: " + f.getCombatSkillLevel());
		space(panel);

		this.staminaLb = line(panel, "Stamina: " + f.getStamina());
		space(panel);

		this.diceLb = line(panel, "");
		
        JPanel m = new JPanel();
        m.setLayout(new BoxLayout(m, BoxLayout.PAGE_AXIS));
		m.add(panel);
		
		JPanel s
		JLabel label = new JLabel(Util.getImage("small_skull.jpg"));
        label.setBorder(createEmptyBorder(9, 9, 9, 9));
		m.add(label);
		
		panel = m;
	}

	private JLabel line(JPanel p, String string)
	{
		return line(p, string, null);
	}

	private JLabel line(JPanel p, String s, Font font)
	{
		JLabel label = new JLabel(s);
		label.setBorder(createEmptyBorder(9, 9, 9, 9));
		label.setFont(font == null ? DEFAULT_FONT : font);
		p.add(label);

		return label;
	}

	private void space(JPanel p)
	{
		p.add(Box.createRigidArea(new Dimension(8, 0)));
	}

	public JPanel getPanel()
	{
		return panel;
	}

	public void attackResult(AttackResult r)
	{
		diceLb.setFont(STRONG_FONT);
		diceLb.setText(r.toString());

		if (r.getType().equals(AttackResultType.won)) {
			diceLb.setForeground(Color.green.darker());
		} else if (r.getType().equals(AttackResultType.loose)) {
			diceLb.setForeground(LIGHT_RED_COLOR);
		} else {
			diceLb.setForeground(Color.black);
		}

		staminaLb.setText("<html>Stamina: <strong>" + fighter.getStamina() + "</strong></html>");

		if (fighter.getStamina() <= 0) {
			if (chooseBt != null) {
				chooseBt.setVisible(false);
			}
			panel.setBackground(LIGHT_RED_COLOR);
			diceLb.setForeground(Color.black);
			panel.setBorder(createCompoundBorder(createBevelBorder(0), createEmptyBorder(3, 3, 3, 3)));
		}
	}
	
	public void roundEnded() 
    {
        //does nothing
    }

    @Override
	public String toString()
	{
		return "[FV: fighter=" + fighter + "]";
	}

	public void clean()
	{
		diceLb.setFont(DEFAULT_FONT);
		diceLb.setForeground(Color.black);
	}

	public boolean relatesTo(Fighter enemy)
	{
		return fighter.equals(enemy);
	}

	public void setCurrent(boolean isCurrent)
	{
		if(isCurrent) {
			panel.setBorder(createLineBorder(Color.blue.darker(), 3));
		} else {
			panel.setBorder(createEmptyBorder());
		}
	}
}
