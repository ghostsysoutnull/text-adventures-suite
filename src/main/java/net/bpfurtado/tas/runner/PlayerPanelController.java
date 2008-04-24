/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 19/10/2005 23:05:17                                                          
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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.bpfurtado.tas.model.Game;
import net.bpfurtado.tas.model.Player;
import net.bpfurtado.tas.model.Skill;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class PlayerPanelController //implements PlayerStatsViewer
{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(PlayerPanelController.class);

	static final Font DEFAULT_FONT = new java.awt.Font("Arial Bold", 1, 14);

    private Game game;
    private JPanel panel;

    public PlayerPanelController()
    {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setMinimumSize(new Dimension(160, 300));
        panel.setMaximumSize(new Dimension(200, 800));
    }

    public JPanel getPanel()
    {
        return this.panel;
    }

    public void updateView(String executedActionText)
    {
        updateView();
        JOptionPane.showMessageDialog(panel, executedActionText);
    }

    public void updateView(Game g)
    {
        this.game = g;
        updateView();
    }

    public void updateView()
    {
        if (game == null) {
            return;
        }
        
        Player player = game.getPlayer();
        
        panel.removeAll();
        panel.add(createTitle("Skills"));
        PlayerAttributesPanel skillsPanel = new PlayerAttributesPanel();
        for(Skill s: player.getSkills()) {
        	skillsPanel.addLine(s.getName(), s.getLevel());
        }
		panel.add(skillsPanel.getPanel());
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		panel.add(createTitle("Attributes"));
		PlayerAttributesPanel attributesPn = new PlayerAttributesPanel();
        attributesPn.addLine("Stamina", player.getStamina());
        attributesPn.addLine("Luck", player.getLuck());
        attributesPn.addLine("Damage", player.getDamage());

        Set<Entry<String, Object>> unorderedAttributes = player.getAttributesEntrySet();
		if (!unorderedAttributes.isEmpty()) {

			List<Entry<String, Object>> orderedAttributes = new LinkedList<Entry<String, Object>>(unorderedAttributes);
			Collections.sort(orderedAttributes, new Comparator<Entry<String, Object>>()
			{
				public int compare(Entry<String, Object> o1, Entry<String, Object> o2)
				{
					return o1.getKey().compareTo(o2.getKey());
				}
			});

			for (Entry<String, Object> attributeEntry : orderedAttributes) {
				attributesPn.addLine(attributeEntry.getKey(), attributeEntry.getValue());
			}
		} /*else {
			System.out.println("PlayerPanelController.updateView()");
			attributesPn.setAsHavingNoAttributes();
		}*/

		panel.add(attributesPn.getPanel());
    }

	private JLabel createTitle(String title)
	{
		JLabel attributesLb = new JLabel(" " + title + " ");
		attributesLb.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		attributesLb.setAlignmentY(JPanel.TOP_ALIGNMENT);
        attributesLb.setFont(DEFAULT_FONT);
        attributesLb.setBorder(BorderFactory.createRaisedBevelBorder());
		return attributesLb;
	}

    public void setGame(Game game)
    {
        this.game = game;
    }

	public void startAgain()
	{
		panel.removeAll();
	}
}

class ItemMouseListener extends MouseAdapter
{
    JLabel name;
//    Item item;
    Game g;

    ItemMouseListener(JLabel name, /*Item item,*/ Game g)
    {
        this.name = name;
//        this.item = item;
        this.g = g;
    }

    public void mouseClicked(MouseEvent e)
    {
//        item.use(g);
    }

    public void mouseEntered(MouseEvent e)
    {
        name.setForeground(Color.BLUE);
        name.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e)
    {
        name.setForeground(Color.BLACK);
        name.setCursor(null);
    }
}