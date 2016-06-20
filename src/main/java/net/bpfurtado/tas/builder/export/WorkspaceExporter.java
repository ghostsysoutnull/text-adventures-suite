/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on Jun 20, 2016 3:23:54 PM
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

package net.bpfurtado.tas.builder.export;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Workspace;

public class WorkspaceExporter
{
    private static final Logger logger = Logger.getLogger(WorkspaceExporter.class);

    public static void export(Workspace workspace)
    {
        ZipOutputStream out;
        try {
            File exportedFolder = new File(Workspace.getWorkspacesHome() + File.separator + "exportedAdventures");
            if (!exportedFolder.exists()) {
                exportedFolder.mkdirs();
            }

            out = new ZipOutputStream(new FileOutputStream(
                    exportedFolder.getAbsolutePath() + File.separator + workspace.getAdventure().getName() + ".zip"));

            File root = new File(Workspace.getWorkspacesHome() + File.separator + workspace.getId());
            logger.debug("Root " + root);
            addZipsFromFolder(out, root, false);
            addZipsFromFolder(out, new File(root.getAbsolutePath() + File.separator + "images"), true);
            out.close();
            
            Desktop.getDesktop().open(exportedFolder);
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }

    private static void addZipsFromFolder(ZipOutputStream out, File root, boolean createFolder)
            throws IOException, FileNotFoundException
    {

        for (File f : root.listFiles()) {
            if (!f.isDirectory()) {
                String zipEntryName = f.getName();
                if (createFolder) {
                    zipEntryName = root.getName() + File.separator + f.getName();
                }
                ZipEntry zipEntry = new ZipEntry(zipEntryName);
                out.putNextEntry(zipEntry);

                FileInputStream input = new FileInputStream(f);
                int bytesSize = (int) input.getChannel().size();
                byte[] data = new byte[bytesSize];
                input.read(data);
                input.close();

                out.write(data, 0, bytesSize);
                out.closeEntry();
            }
            logger.debug(f.getName());
        }
    }
}
