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

package net.bpfurtado.tas.view;

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
import net.bpfurtado.tas.AdventureOpenner;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.builder.FileOpennedListener;
import net.bpfurtado.tas.builder.OpenAdventureAction;

/**
 * @author Bruno Patini Furtado
 */
public class RecentAdventuresMenuController implements FileOpennedListener
{
    private static final int MAX_ENTRIES_IN_RECENT_LIST = 10;

    private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

    private JMenu openRecentMenu;

    private LinkedList<File> recentFiles = new LinkedList<File>();
    private LinkedList<JMenuItem> menuItems = new LinkedList<JMenuItem>();

    private AdventureOpenner adventureOpenner;
    private String recentFileName;

    private JFrame parentDialogFrame;

    public RecentAdventuresMenuController(JFrame parentDialogFrame, AdventureOpenner advOpenner)
    {
        this.parentDialogFrame = parentDialogFrame;
        this.adventureOpenner = advOpenner;
        recentFileName = obtainRecentAdventureFileName();
        createOpenRecentAdventuresMenu();
    }

    public JMenu getOpenRecentMenu()
    {
        return openRecentMenu;
    }

    public void fileOpenedAction(File file)
    {
        addToRecent(file);
    }

    private void createOpenRecentAdventuresMenu()
    {
        try {
            openRecentMenu = new JMenu("Open Recent...");
            openRecentMenu.setIcon(Util.getImage("time.png"));
            openRecentMenu.setEnabled(false);
            
            openRecentMenu.setMnemonic('R');
            File recentFile = new File(recentFileName);
            if (!recentFile.exists()) {
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(recentFile));
            for (String filePath = reader.readLine(); filePath != null; filePath = reader.readLine()) {
                File file = new File(filePath);
                if (file.exists()) {
					addToRecent(file);
				}
            }
            reader.close();
            
            if (!recentFiles.isEmpty()) {
                openRecentMenu.setEnabled(true);
            }
            
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }

    private void addToRecent(File recentFile)
    {
        if(recentFiles.contains(recentFile)) {
            for(JMenuItem item : menuItems) {
                if (item.getText().equals(recentFile.getName())
                    || item.getText().startsWith("<html>"+recentFile.getName()+" <")) {
                    openRecentMenu.remove(item);
                    menuItems.remove(item);
                    recentFiles.remove(recentFile);

                    item.setText("<html>"+recentFile.getName()+" <DEFAULT_FONT size=-2 color=blue><b><i>"+
                            sdf.format(new Date())+"</i></b></DEFAULT_FONT> </html>");

                    openRecentMenu.add(item, 0);
                    menuItems.add(0, item);
                    recentFiles.addFirst(recentFile);

                    openRecentMenu.setEnabled(true);
                    
                    saveRecentEntriesFile();

                    return;
                }
            }
            throw new AdventureException("Should have found a menu item");
        }

        if (recentFiles.size() == MAX_ENTRIES_IN_RECENT_LIST) {
            recentFiles.removeLast();
        }
        recentFiles.addFirst(recentFile);

        if (menuItems.size() == MAX_ENTRIES_IN_RECENT_LIST) {
            openRecentMenu.remove(menuItems.removeLast());
        }
        menuItems.addFirst(buildRecentMenuItem(recentFile));

        saveRecentEntriesFile();
    }

    private void saveRecentEntriesFile()
    {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(new File(recentFileName)));
            Collections.reverse(recentFiles);
            for (File f : recentFiles) {
                writer.println(f.getAbsolutePath());
            }
            writer.flush();
            writer.close();
            Collections.reverse(recentFiles);
        } catch (IOException ioe) {
            throw new AdventureException(ioe);
        }
    }

    private JMenuItem buildRecentMenuItem(File recentFile)
    {
        JMenuItem menuItem = new JMenuItem(recentFile.getName());
        menuItem.addActionListener(
                new OpenAdventureAction(
                        parentDialogFrame,
                        adventureOpenner,
                        recentFile.getAbsolutePath()));
        openRecentMenu.add(menuItem, 0);

        return menuItem;
    }

    private String obtainRecentAdventureFileName()
    {
        File appHomeDir = Conf.findOrCreateAplicationHomeDir(adventureOpenner);
        return appHomeDir + "/recent";
    }
}
