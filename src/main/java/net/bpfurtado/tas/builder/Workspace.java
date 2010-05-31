/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 30/05/2010 15:43:53
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.persistence.XMLAdventureReader;
import net.bpfurtado.tas.model.persistence.XMLAdventureWriter;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

public class Workspace
{
    private static final Logger logger = Logger.getLogger(Workspace.class);
    private static final String sep = File.separator;

    @SuppressWarnings("unused")
    private static final Conf conf = Conf.builder();

    private String id = null;
    private Adventure adventure;

    private File workspaceHome;

    public static Workspace loadFrom(String workspaceId)
    {
        return new Workspace(workspaceId);
    }

    public static Workspace createWith(Adventure adventure)
    {
        return new Workspace(adventure);
    }

    private Workspace(Adventure adventure)
    {
        id = UUID.randomUUID().toString();
        this.adventure = adventure;
    }

    public Workspace(String workspaceId)
    {
        this.id = workspaceId;
        adventure = new XMLAdventureReader().read(adventureFileFrom(workspaceId));
    }

    public void save()
    {
        File f = buildAdventureFile();
        logger.debug("Saving to [" + f + "]");
        XMLAdventureWriter writter = new XMLAdventureWriter(adventure, f);
        writter.write();
    }

    private File adventureFileFrom(String workspaceId)
    {
        String path = getWorkspacesHome() + sep + workspaceId + sep + "adventure.adv.xml";
        File f = new File(path);
        if (!f.exists()) {
            throw new AdventureException("There's no adventure file at the workspace [" + workspaceId + "]");
        }
        return f;
    }

    private File buildAdventureFile()
    {
        return new File(getWorkspaceHome().getAbsolutePath() + sep + "adventure");
    }

    private File getWorkspaceHome()
    {
        if (workspaceHome == null) {
            String path = getWorkspacesHome() + sep + getId();
            workspaceHome = new File(path);
            if (!workspaceHome.exists()) {
                workspaceHome.mkdirs();
            }
        }
        return workspaceHome;
    }

    public static String getWorkspacesHome()
    {
        String path = Conf.getHome() + sep + "builder" + sep + "workspaces";
        File ws = new File(path);
        if (!ws.exists()) {
            ws.mkdirs();
        }
        return path;
    }

    /**
     * @return imageId: a UUID string.
     */
    public String copy(File imageFile)
    {
        try {
            InputStream in = new FileInputStream(imageFile);
            String imageId = generateImageId(imageFile);
            File copyOfImageFile = new File(getImagesHomePath() + sep + imageId);
            copyOfImageFile.createNewFile();

            byte[] data = new byte[in.available()];
            in.read(data);

            OutputStream out = new FileOutputStream(copyOfImageFile);
            out.write(data);
            out.flush();
            out.close();
            in.close();
            logger.debug("Saved image file [" + copyOfImageFile + "]");

            return imageId;
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }

    private static String generateImageId(File imageFile)
    {
        String name = imageFile.getName();
        int idx = name.length() - 4;
        String ext = name.substring(idx);
        String imageId = name.substring(0, idx) + "_" + UUID.randomUUID().toString() + ext;
        logger.debug("imageId = " + imageId);
        return imageId;
    }

    private String getImagesHomePath()
    {
        String path = getWorkspaceHome().getAbsolutePath() + sep + "images";
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return path;
    }

    public String imagePathFrom(String imageId)
    {
        return getImagesHomePath() + sep + imageId;
    }

    public String getId()
    {
        return id;
    }

    public Adventure getAdventure()
    {
        return adventure;
    }

    public Icon imageFrom(Scene s)
    {
        ImageIcon img = null;
        File f = new File(imagePathFrom(s.getImageId()));
        if (!f.exists()) {
            img = Util.getImage("sceneImageNotFound.jpg");
        } else {
            img = new ImageIcon(f.getAbsolutePath());
        }
        return img;
    }

    public static List<Workspace> listAll()
    {
        List<Workspace> ws = new LinkedList<Workspace>();
        File workspacesHomeFolder = new File(Workspace.getWorkspacesHome());
        for (File f : workspacesHomeFolder.listFiles()) {
            if (f.isDirectory()) {
                logger.debug("Reading [" + f + "]...");
                ws.add(Workspace.loadFrom(f.getName()));
            }
        }
        return ws;
    }
}
