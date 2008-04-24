/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 06/10/2005 13:14:58                                                          
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

package net.bpfurtado.tas.builder.scenespanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;

import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.builder.scenespanel.ScenesListControllerFactory.Result;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.Scene;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class ChooseSceneDialog extends JDialog
{
	private static final long serialVersionUID = -5055062143576691936L;

	private static final Logger logger = Logger.getLogger(ChooseSceneDialog.class);

    private Scene choosenScene;

    public ChooseSceneDialog(Builder builder, Adventure adventure)
    {
        super(builder, true);

        initView(adventure, builder);
    }

    private void initView(Adventure adventure, Builder builder)
    {
		widgets(builder);

        setBounds(650, 430, 445, 226);
        setTitle("Choose a Scene");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        setVisible(true);
    }

	private void widgets(Builder builder)
	{
		Result result = ScenesListControllerFactory.create(builder, false);
		add(result.getPanel());

		ScenesList scenesList = result.getScenesList();
		scenesList.updateView();
		events(result.getFilterList());
		events(scenesList.getList());
		
        JPanel buttonsPn = new JPanel();
        JButton cancelBt = new JButton("Cancel");
        cancelBt.setMnemonic('c');
        cancelBt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				closeDialog();
			}
		});
        
		buttonsPn.add(cancelBt);

        add(buttonsPn, BorderLayout.PAGE_END);		
	}

	private void events(final JList list_)
	{
		list_.addMouseListener(new MouseAdapter()
        {
			private JList list;
            public void mouseClicked(MouseEvent e)
            {
            	this.list = list_;
                selectScene(this.list);
            }
        });
		
		list_.addKeyListener(new KeyAdapter()
        {
			private JList list;
            public void keyTyped(KeyEvent e)
            {
            	this.list = list_;
                listKeyAction(list, e);
            }
        });
	}

	private void listKeyAction(JList list2, KeyEvent e)
    {
        if (e.getKeyChar() == 27) { // ESC
            closeDialog();
        } else if (e.getKeyChar() == 10) { // ENTER
            selectScene(list2);
        }
    }

    private void selectScene(JList list)
    {
    	try {
			choosenScene = (Scene) list.getModel().getElementAt(list.getSelectedIndex());
		} catch (ClassCastException cce) {
			choosenScene = ((SceneRank) list.getModel().getElementAt(list.getSelectedIndex())).scene;
		}
		logger.debug("Selected scene: " + choosenScene.getName());
		closeDialog();
    }

    private void closeDialog()
    {
        dispose();
        setVisible(false);
    }

    public Scene getScene()
    {
        return this.choosenScene;
    }
}
