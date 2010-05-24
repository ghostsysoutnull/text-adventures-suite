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

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.view.Util;

public class ImagePanelBuilder
{
    private JPanel mainPanel;
    private JPanel centralPn;
    
    private JTextField imagePathTf;
    private JLabel imageLb;

    private ImageReceiver imageReceiver;

    public ImagePanelBuilder(ImageReceiver r, Scene s)
    {
        this.imageReceiver = r;
        mainPanel = new JPanel(new BorderLayout());

        JPanel top = new JPanel();
        this.imagePathTf = new JTextField(35);
        top.add(imagePathTf);
        JButton chooseBt = new JButton("Choose...");
        chooseBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(mainPanel);
                File imageFile = fc.getSelectedFile();

                updateImage(imageFile);
            }
        });
        top.add(chooseBt);
        mainPanel.add(top, BorderLayout.PAGE_START);

        centralPn = new JPanel();
        centralPn.setBorder(BorderFactory.createRaisedBevelBorder());
        if (s != null) {
            update(s);
        } else {
            this.imageLb = new JLabel(Util.getImage("chest.JPG"));
        }
        centralPn.add(imageLb);
        mainPanel.add(new JScrollPane(centralPn), BorderLayout.CENTER);
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

    JPanel getPanel()
    {
        return mainPanel;
    }
}