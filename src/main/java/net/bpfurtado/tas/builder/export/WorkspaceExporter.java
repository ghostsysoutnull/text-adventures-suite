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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Workspace;

public class WorkspaceExporter
{
    public static final String ADV_ZIP_EXTENSION = ".tas-adv.zip";

    private static final Logger logger = Logger.getLogger(WorkspaceExporter.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hhmmss");

    public static Workspace importWorkspace(JPanel mainPanel)
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription()
            {
                return "TAS Exported Adventure";
            }

            @Override
            public boolean accept(File f)
            {
                String n = f.getName().toLowerCase();
                return f.isDirectory() || n.endsWith(WorkspaceExporter.ADV_ZIP_EXTENSION);
            }
        });
        fc.showOpenDialog(mainPanel);
        File advExportedFile = fc.getSelectedFile();

        logger.debug(advExportedFile);

        try {
            File workspaceFolder = new File(Workspace.getWorkspacesHome() + File.separator + UUID.randomUUID());
            workspaceFolder.mkdirs();
            new File(workspaceFolder.getAbsolutePath() + File.separator + "images").mkdirs();

            byte[] buffer = new byte[4096];
            ZipInputStream zip = new ZipInputStream(new FileInputStream(advExportedFile));
            for (ZipEntry ze = zip.getNextEntry(); ze != null; ze = zip.getNextEntry()) {
                File file = new File(workspaceFolder.getAbsolutePath() + File.separator + ze.getName());
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);

                logger.debug("Writing file [" + file + "]");
                int len;
                while ((len = zip.read(buffer)) > 0) {
                    logger.debug("Writing " + buffer.length + " bytes...");
                    out.write(buffer, 0, len);
                }
                out.close();
            }
            zip.close();

            Workspace worskpace = Workspace.loadFrom(workspaceFolder.getName());
            return worskpace;
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }

    public static void export(Workspace workspace)
    {
        try {
            String adventureOriginalName = workspace.getAdventure().getName();
            String timestamp = sdf.format(new Date());
            workspace.getAdventure().setName(adventureOriginalName + " {Exported at " + timestamp + "}");
            workspace.save();

            File exportedFolder = new File(Workspace.getWorkspacesHome() + File.separator + "exportedAdventures");
            if (!exportedFolder.exists()) {
                exportedFolder.mkdirs();
            }

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(exportedFolder.getAbsolutePath()
                    + File.separator + adventureOriginalName + "_Exported_" + timestamp + ADV_ZIP_EXTENSION));

            File root = new File(Workspace.getWorkspacesHome() + File.separator + workspace.getId());
            logger.debug("Root " + root);
            addZipsFromFolder(out, root, false);
            addZipsFromFolder(out, new File(root.getAbsolutePath() + File.separator + "images"), true);
            out.close();

            workspace.getAdventure().setName(adventureOriginalName);
            workspace.save();

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