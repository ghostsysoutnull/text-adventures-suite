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
package net.bpfurtado.tas.builder.depth;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;

public class ToCellRenderer extends JLabel implements ListCellRenderer
{
    private static final long serialVersionUID = -5508903623764928405L;

    private Collection<Scene> scenesTo;

    public ToCellRenderer(Scene to)
    {
        setOpaque(true);

        scenesTo = new LinkedList<Scene>();
        for (IPath p : to.getPaths()) {
            scenesTo.add(p.getTo());
        }
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        Scene scene = (Scene) value;
        setText(DepthScenesViewController.renderToList(scene));

        if (scenesTo.contains(scene)) {
            setBackground(Color.blue);
            setForeground(Color.white);
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }
        return this;
    }
}