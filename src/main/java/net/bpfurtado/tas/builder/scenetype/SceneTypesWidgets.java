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
package net.bpfurtado.tas.builder.scenetype;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.SceneType;

import org.apache.log4j.Logger;

public class SceneTypesWidgets
{
    private static Logger logger = Logger.getLogger(SceneTypesWidgets.class);

    final JComboBox cb;

    public SceneTypesWidgets(JPanel panel, final Builder builder)
    {
        panel.add(new JLabel("Scene type: "));
        cb = new JComboBox(SceneType.values());
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                SceneType type = (SceneType) cb.getSelectedItem();
                builder.changeSceneTypeEvent(type);
                logger.debug(type);
            }
        });
        cb.setMaximumSize(new Dimension(100, 23));
        panel.add(cb);
    }

    public void setEnabled(boolean b)
    {
        cb.setEnabled(b);
    }

    public void updateView(Scene currentScene, Scene start)
    {
        cb.setEnabled(!currentScene.equals(start));
        cb.setSelectedItem(currentScene.getType());
    }
}
