/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
 * Created on 11/06/2008 18:59:23
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

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

public abstract class SceneListBase
{
    private static final Logger logger = Logger.getLogger(SceneListBase.class);

    protected JList list;

    public SceneListBase()
    {
        list = buildList();

        list.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_UP && list.getSelectedIndex() == 0) {
                    logger.debug("focus on txt field");
                }
            }

            public void keyReleased(KeyEvent e)
            {
            }

            public void keyTyped(KeyEvent e)
            {
            }
        });
    }

    public void focusOnList()
    {
        list.setSelectedIndex(0);
        list.requestFocusInWindow();
    }

    protected JList buildList()
    {
        JList scenesList = new JList();
        scenesList.setFont(new Font("Courier New", 0, 10));

        scenesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scenesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        return scenesList;
    }
}