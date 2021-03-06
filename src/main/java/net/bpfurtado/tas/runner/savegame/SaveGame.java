/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 27/06/2009 21:11:50
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

package net.bpfurtado.tas.runner.savegame;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Workspace;
import net.bpfurtado.tas.builder.EntityPersistedOnFileOpenAction;
import net.bpfurtado.tas.model.Player;

public class SaveGame implements Serializable, EntityPersistedOnFileOpenAction
{
    private static final long serialVersionUID = 6976207148524442036L;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-hh_mm_ss");

    private Player player;
    private int sceneId;

    private Workspace workspace;

    private File file;
    private Date creation = new Date();

    public SaveGame(Workspace workspace, Player player, int sceneId)
    {
        this.workspace = workspace;
        this.player = player;

        // Otherwise we get a ref to the Runner itself, through event listeners...
        this.player.clearEventListeners();

        this.sceneId = sceneId;
    }

    public SaveGame(Workspace workspace, Player player, int sceneId, String creationStr)
    {
        this(workspace, player, sceneId);
        try {
            this.creation = sdf.parse(creationStr);
        } catch (ParseException e) {
            throw new AdventureException("Could not parse Save Game creation field [" + creationStr + "]", e);
        }
    }

    @Override
    public String getMenuItemText()
    {
        return workspace.getAdventure().getName() + "__" + getCreationAsString();
    }

    @Override
    public String getId()
    {
        return file.getAbsolutePath();
    }

    public String getCreationAsString()
    {
        return sdf.format(creation);
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public Player getPlayer()
    {
        return player;
    }

    public int getSceneId()
    {
        return sceneId;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public void setWorkspace(Workspace workspace)
    {
        this.workspace = workspace;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SaveGame) {
            SaveGame other = (SaveGame) obj;
            return other.creation.equals(creation);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return creation.hashCode();
    }
}
