/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
 * Created on 02/05/2008 17:09:12
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
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class is still not used.
 */
public class SceneHeaderBuilder extends JFrame
{
    private static final long serialVersionUID = 2678881421227174447L;

    private static final Font FONT = new Font("Tahoma", Font.PLAIN, 14);

    public SceneHeaderBuilder()
    {
        initView();
    }

    private void initView()
    {
        widgets();

        setBounds(200, 200, 700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void widgets()
    {
        initComponents();
        add(createMainPanel());
    }

    private Component createMainPanel()
    {
        JPanel main = new JPanel(new BorderLayout());
        FormLayout layout = new FormLayout("right:pref, 4dlu, 160dlu, 4dlu, p, 4dlu, 60dlu", "p, 6dlu, p");

        layout.setRowGroups(new int[][] { { 1, 3 } });

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();

        builder.add(label("Tags:"), cc.xy(1, 1));
        builder.add(new JTextField(), cc.xy(3, 1));

        builder.add(label("Scene Type:"), cc.xy(5, 1));
        builder.add(comboBox(), cc.xy(7, 1));

        builder.add(label("Title"), cc.xy(1, 3));
        builder.add(new JTextField(), cc.xyw(3, 3, 5));

        JPanel formPanel = builder.getPanel();

        main.add(formPanel);
        return main;
    }

    private JComboBox comboBox()
    {
        JComboBox comboBox = new JComboBox(new String[] { "Normal", "Combat", "Test your skill" });
        return comboBox;
    }

    private void initComponents()
    {

    }

    private JLabel label(String s)
    {
        JLabel l = new JLabel(s);
        l.setFont(FONT);
        return l;
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                new SceneHeaderBuilder();
            }
        });
    }
}
