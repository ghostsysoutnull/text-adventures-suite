/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 10/10/2005 11:09:06                                                          
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

package net.bpfurtado.tas.view.recentmenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.Workspace;
import net.bpfurtado.tas.builder.EntityPersistedOnFileOpenAction;
import net.bpfurtado.tas.builder.EntityPersistedOnFileOpenActionListener;
import net.bpfurtado.tas.runner.savegame.SaveGamePersister;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class RecentFilesMenuController implements EntityPersistedOnFileOpenActionListener
{
    private static final Logger logger = Logger.getLogger(RecentFilesMenuController.class);

    private static final int MAX_ENTRIES_IN_RECENT_LIST = 10;

    private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

    private JMenu openRecentMenu;

    private LinkedList<EntityPersistedOnFileOpenAction> recentEntitiesPersisted = new LinkedList<EntityPersistedOnFileOpenAction>();

    private LinkedList<JMenuItem> menuItems = new LinkedList<JMenuItem>();

    private EntityPersistedOnFileOpenner entityOpenner;

    private File historyFile;

    private JFrame parentFrame;

    public RecentFilesMenuController(EntityPersistedOnFileOpenner openner, JFrame parent, String historyFileName)
    {
        this.entityOpenner = openner;
        this.parentFrame = parent;

        this.historyFile = buildHistoryFile(historyFileName);
        createOpenRecentAdventuresMenu();
    }

    public JMenu getOpenRecentMenu()
    {
        return openRecentMenu;
    }

    @Override
    public void fireEntityOpenedAction(EntityPersistedOnFileOpenAction e) // 111
    {
        addToRecent(e);
        openRecentMenu.setEnabled(true);
    }

    private void createOpenRecentAdventuresMenu()
    {
        try {
            openRecentMenu = new JMenu("Open Recent...");
            openRecentMenu.setIcon(Util.getImage("time.png"));
            openRecentMenu.setEnabled(false);

            openRecentMenu.setMnemonic('R');
            if (!historyFile.exists()) {
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(historyFile));
            for (String id = reader.readLine(); id != null; id = reader.readLine()) {
                logger.debug("id [" + id + "] histFile=" + historyFile);
                // addToRecent(id); // FIXME 666
                if (historyFile.getAbsolutePath().contains("SavedGames")) {
                    addToRecent(SaveGamePersister.read(new File(id)));
                } else if (historyFile.getAbsolutePath().contains("Adventures")) {
                    addToRecent(Workspace.loadFrom(id));
                }
            }
            reader.close();

            if (!recentEntitiesPersisted.isEmpty()) {
                openRecentMenu.setEnabled(true);
            }

        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    private void addToRecent(EntityPersistedOnFileOpenAction entityPersisted)
    {
        if (recentEntitiesPersisted.contains(entityPersisted)) {
            for (JMenuItem item : menuItems) {
                if (item.getText().equals(entityPersisted.getMenuItemText()) || item.getText().startsWith("<html>" + entityPersisted.getMenuItemText() + " <")) {
                    openRecentMenu.remove(item);
                    menuItems.remove(item);
                    recentEntitiesPersisted.remove(entityPersisted);

                    //item.setText("<html>" + entityPersisted.getMenuItemText() + " <DEFAULT_FONT size=-2 color=blue><b><i>" + sdf.format(new Date()) + "</i></b></DEFAULT_FONT> </html>");
                    item.setText(entityPersisted.getMenuItemText());
                    openRecentMenu.add(item, 0);
                    menuItems.add(0, item);
                    recentEntitiesPersisted.addFirst(entityPersisted);

                    openRecentMenu.setEnabled(true);

                    saveRecentEntriesFile();

                    return;
                }
            }
            throw new AdventureException("Should have found a menu item");
        }

        if (recentEntitiesPersisted.size() == MAX_ENTRIES_IN_RECENT_LIST) {
            recentEntitiesPersisted.removeLast();
        }
        recentEntitiesPersisted.addFirst(entityPersisted);

        if (menuItems.size() == MAX_ENTRIES_IN_RECENT_LIST) {
            openRecentMenu.remove(menuItems.removeLast());
        }
        menuItems.addFirst(buildRecentMenuItem(entityPersisted));

        saveRecentEntriesFile();
    }

    private void saveRecentEntriesFile()
    {
        try {
            if(historyFile.getAbsolutePath().contains("Adv")) {
                int a=0;
                a++;
            }
            PrintWriter writer = new PrintWriter(new FileWriter(historyFile));
            Collections.reverse(recentEntitiesPersisted);
            for (EntityPersistedOnFileOpenAction entityPersisted : recentEntitiesPersisted) {
                logger.debug("txt=[" + entityPersisted.getMenuItemText() + "], id=[" + entityPersisted.getId() + "]");
                writer.println(entityPersisted.getId());
            }
            writer.flush();
            writer.close();
            Collections.reverse(recentEntitiesPersisted);
        } catch (IOException ioe) {
            throw new AdventureException(ioe);
        }
    }

    private JMenuItem buildRecentMenuItem(EntityPersistedOnFileOpenAction recentFile)
    {
        JMenuItem menuItem = new JMenuItem(recentFile.getMenuItemText());
        menuItem.addActionListener(new OpenEntityPersistedOnFileAction(parentFrame, entityOpenner, recentFile));
        openRecentMenu.add(menuItem, 0);

        return menuItem;
    }

    private File buildHistoryFile(String fileName)
    {
        File appHomeDir = Conf.findOrCreateAplicationHomeDir(entityOpenner);
        return new File(appHomeDir + File.separator + fileName); // "/recent.txt"
    }
}
