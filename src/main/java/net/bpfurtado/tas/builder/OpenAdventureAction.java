/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 10/10/2005 13:04:38                                                          
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import net.bpfurtado.tas.AdventureOpenner;
import net.bpfurtado.tas.view.Util;

/**
 * @author Bruno Patini Furtado
 */
public class OpenAdventureAction implements ActionListener
{
	private static Logger logger = Logger.getLogger(OpenAdventureAction.class);
	
    private JFrame frame;

    private AdventureOpenner adventureOpenner;

    private String adventureFileName;

    public OpenAdventureAction(JFrame frame, AdventureOpenner adventureOpenner, String adventureFileName)
    {
        this.frame = frame;
        this.adventureOpenner = adventureOpenner;
        this.adventureFileName = adventureFileName;
    }

    public void actionPerformed(ActionEvent e)
    {
    	logger.debug("HEre");
    	
        if (adventureOpenner.isDirty()) {
            int answer = Util.showSaveDialog(frame, "Do you want to save it before openning another adventure?");
            if (answer == Util.SAVE_DIALOG_OPT_CANCEL)
                return;
            else if (answer == Util.SAVE_DIALOG_OPT_SAVE)
                adventureOpenner.saveAdventure(false);
        }
        adventureOpenner.openAdventure(new File(adventureFileName));
    }
}
