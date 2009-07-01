/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 08/04/2006 14:59:05                                                          
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

package net.bpfurtado.tas.builder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
class SceneTextAreaPopupListener extends MouseAdapter
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SceneTextAreaPopupListener.class);

    private JPopupMenu popup;
    private JTextArea sceneTA;
    
    private JMenuItem createPathMnIt;
    private JMenuItem splitSceneMnIt;
    private JMenuItem combatMnIt;

    SceneTextAreaPopupListener(JPopupMenu popupMenu, JTextArea sceneTA, JMenuItem createPathMnIt, JMenuItem splitSceneMnIt, JMenuItem combatMnIt)
    {
        popup = popupMenu;
        this.sceneTA = sceneTA;
        
        this.createPathMnIt = createPathMnIt;
        this.splitSceneMnIt = splitSceneMnIt;
        this.combatMnIt = combatMnIt;
    }

    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e)
    {
        if (!e.isPopupTrigger()) {
            return;
        }

        if (sceneTA.getSelectedText() == null) {
            createPathMnIt.setEnabled(false);
            splitSceneMnIt.setEnabled(false);
            createPathMnIt.setText("Select some text...");
        } else {
            combatMnIt.setEnabled(true);
            splitSceneMnIt.setEnabled(true);
            createPathMnIt.setEnabled(true);
            createPathMnIt.setText("Create Path '" + sceneTA.getSelectedText().trim() + "'");
        }

        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}