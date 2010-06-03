/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 23/05/2010 17:57:58
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

public class ImagePanelBuilder
{
    private static final Logger logger = Logger.getLogger(ImagePanelBuilder.class);
    private static final Conf conf = Conf.builder();

    private static final String SCENE_IMAGE_LAST_USED_FOLDER = "sceneImageLastUsedFolder";

    private static final JFileChooser fc = new JFileChooser();
    static {
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public String getDescription()
            {
                return "Image";
            }

            @Override
            public boolean accept(File f)
            {
                String n = f.getName().toLowerCase();
                return f.isDirectory() || n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".gif");
            }
        });
    }

    private JPanel mainPanel;
    private JPanel centralPn;

    private JTextField imagePathTf;
    private JLabel imageLb;

    private ImageReceiver imageReceiver;

    public ImagePanelBuilder(ImageReceiver receiver, Scene scene)
    {
        this.imageReceiver = receiver;

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTopPanel(), BorderLayout.PAGE_START);

        this.centralPn = createCentralPanel(scene);
        mainPanel.add(new JScrollPane(centralPn), BorderLayout.CENTER);
    }

    private JPanel createCentralPanel(Scene s)
    {
        JPanel centralPn = new JPanel();
        centralPn.setBorder(BorderFactory.createRaisedBevelBorder());
        if (s != null) {
            update(s);
        } else {
            this.imageLb = new JLabel(Util.getImage("chest.JPG"));
        }
        centralPn.add(imageLb);
        return centralPn;
    }

    private JPanel createTopPanel()
    {
        JPanel top = new JPanel();
        this.imagePathTf = new JTextField(35);
        top.add(imagePathTf);
        JButton chooseBt = new JButton("Choose...");
        chooseBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                chooseImageButtonAction(imageReceiver.getWorkspaceId());
            }
        });

        top.add(chooseBt);
        return top;
    }

    protected void updateImage(File imageFile)
    {
        imagePathTf.setText(imageFile.getAbsolutePath());
        imageLb.setIcon(new ImageIcon(imageFile.getAbsolutePath()));
        imageReceiver.fireNewImageSelectedAction(imageFile);
    }

    public void update(Scene currentScene)
    {
        Icon image = null;
        if (currentScene.getImageFile() == null) {
            image = Util.getImage("chest.JPG");
        } else {
            image = Util.imageFrom(currentScene);
        }

        this.imageLb = new JLabel(image);
        centralPn.removeAll();
        centralPn.add(imageLb);
    }

    private void chooseImageButtonAction(String workspaceId)
    {
        File lastFolder = new File(conf.get(SCENE_IMAGE_LAST_USED_FOLDER, System.getProperty("user.home")));
        if (lastFolder.exists()) {
            fc.setCurrentDirectory(lastFolder);
        }

        fc.showOpenDialog(mainPanel);
        File imageFile = fc.getSelectedFile();

        File newImageFile = copyImageToWorkspace(imageFile, workspaceId);

        conf.set(SCENE_IMAGE_LAST_USED_FOLDER, imageFile.getParentFile().getAbsolutePath());
        conf.save();

        updateImage(newImageFile);
    }

    private File copyImageToWorkspace(File imageFile, String workspaceId)
    {
        try {
            InputStream in = new FileInputStream(imageFile);
            File wsDir = new File(Conf.getHome() + File.separator + "workspaces");
            if (!wsDir.exists()) {
                wsDir.mkdirs();
            }

            File wDir = new File(wsDir + File.separator + workspaceId);
            if (!wDir.exists()) {
                wDir.mkdirs();
            }
            File copyOfImageFile = new File(wDir.getAbsolutePath() + File.separator + imageFile.getName());
            copyOfImageFile.createNewFile();

            byte[] data = new byte[in.available()];
            in.read(data);
            
            OutputStream out = new FileOutputStream(copyOfImageFile);
            out.write(data);
            out.flush();
            out.close();
            in.close();
            logger.debug("Saved image file [" + copyOfImageFile + "]");
            
            return copyOfImageFile;
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }

    JPanel getPanel()
    {
        return mainPanel;
    }
}