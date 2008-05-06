/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 05/05/2008 22:38:37
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

import static net.bpfurtado.tas.view.Util.addWidth;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class CodePanelBuilder
{
    public static JTextArea createCodePanel(JPanel codePn, final IBuilder builder, Toolkit toolkit)
    {
        ScrollTextArea codeSTA = BuilderSwingUtils.createTextAreaWidgets(builder, toolkit);
        final JTextArea codeTA = codeSTA.textArea;
        codeTA.setFont(new java.awt.Font("Courier New", 0, 14));
    
        codePn.setLayout(new BoxLayout(codePn, BoxLayout.PAGE_AXIS));
        codePn.add(codeSTA.scrollPane);
    
        JPanel buttonsPn = new JPanel();
        buttonsPn.setLayout(new BoxLayout(buttonsPn, BoxLayout.LINE_AXIS));
    
        JButton trimSpacesBt = new JButton("Trim Spaces");
        trimSpacesBt.setMnemonic('t');
        trimSpacesBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                trimCodeAction(codeTA);
            }
        });
        buttonsPn.add(trimSpacesBt);
    
        addWidth(buttonsPn);
    
        JButton codeHelpBt = new JButton("Help and Code snippets");
        codeHelpBt.setMnemonic('H');
        codeHelpBt.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        codeHelpBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                builder.showSceneCodeHelpDialog();
            }
        });
        buttonsPn.add(codeHelpBt);
        
        codePn.add(Box.createRigidArea(new Dimension(0, 5)));
        codePn.add(buttonsPn);
        codePn.add(Box.createRigidArea(new Dimension(0, 5)));
        return codeTA;
    }

    private static void trimCodeAction(final JTextArea codeTA)
    {
        StringBuilder b = new StringBuilder();
        StringTokenizer stk = new StringTokenizer(codeTA.getText(), "\n");
        while (stk.hasMoreTokens()) {
            b.append(stk.nextToken().trim());
            b.append("\n");
        }
        codeTA.setText(b.toString());
    }
}
