/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 17/10/2005 23:03:11                                                          
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

package net.bpfurtado.tas.builder.scenespanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class SceneCellRenderer extends JLabel implements ListCellRenderer
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SceneCellRenderer.class);

    private static final long serialVersionUID = 1219990677420289072L;

    private Scene start;

    public SceneCellRenderer(Scene start)
    {
        setOpaque(true);
        this.start = start;
    }

    public SceneCellRenderer()
    {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        Scene scene = (Scene) value;

        setText(" [" + scene.getId() + "] " + scene.getName() + " ");

        Color orphanSceneColor = new Color(133, 213, 157);

        if (start != null && scene.equals(start)) {
            setBackground(isSelected ? Util.oceanColor : new Color(199, 160, 130));
            setForeground(Color.black);
        } else if (scene.isOrphan()) {
            setBackground(isSelected ? Util.oceanColor : orphanSceneColor);
        } else {
            setBackground(isSelected ? Util.oceanColor : Color.white);
        }
        setForeground(Color.black);
        return this;
    }
}