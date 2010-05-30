/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 11/10/2005 19:12:19                                                          
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

package net.bpfurtado.tas.builder.depth;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.Scene;

/**
 * @author Bruno Patini Furtado
 */
public class DepthScenesFrame extends JFrame
{
	private static final long serialVersionUID = -6765971296929822669L;
	
    private Scene s;

    private DepthScenesViewController viewController;

    private Builder builder;

    public DepthScenesFrame(Builder builder, Adventure a, Scene s)
    {
        this.builder = builder;
        this.s = s;
        initView();
    }

    private void initView()
    {
        setLayout(new BorderLayout());
        add(new JLabel("Scene: " + s.getName()), BorderLayout.PAGE_START);

        createLegendPanel();

        viewController = new DepthScenesViewController(this, s);
        add(viewController.getPanel());

        setBounds(53, 215, 656, 260);
        setTitle("Adventure scenes graphical viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setDefaultLookAndFeelDecorated(true);
        setVisible(true);
    }

    private void createLegendPanel()
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        JLabel l = new JLabel("Scene names Sufix symbols:");
        l.setBorder(BorderFactory.createEmptyBorder(5, 7, 3, 0));
        p.add(l);

        addSymbolDesc("]", "no paths", p);
        addSymbolDesc("#", "paths but to scene specified", p);

        add(p, BorderLayout.PAGE_END);
    }

    private void addSymbolDesc(String symbol, String description, JPanel p)
    {
        JLabel l = new JLabel("<html><DEFAULT_FONT color=blue>" + symbol + "</DEFAULT_FONT> : " + description);
        l.setBorder(BorderFactory.createEmptyBorder(0, 13, 0, 0));
        p.add(l);
    }

    public Builder getBuilder()
    {
        return this.builder;
    }

    public DepthScenesViewController getViewController()
    {
        return this.viewController;
    }
}
