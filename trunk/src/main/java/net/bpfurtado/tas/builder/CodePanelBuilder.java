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
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import net.bpfurtado.bsh.indenter.Indenter;
import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.view.Util;

public class CodePanelBuilder
{
    private static void indentCodeAction(final JTextArea codeTA)
    {
        String indentedCode = new Indenter().indent(codeTA.getText());
        codeTA.setText(indentedCode);
    }

    private JTextArea textArea;
    private JPanel panel;

    public CodePanelBuilder(final IBuilder builder, Toolkit toolkit)
    {
        panel = new JPanel();
        ScrollTextArea codeSTA = BuilderSwingUtils.createTextAreaWidgets(builder, toolkit);
        textArea = codeSTA.textArea;
        getTextArea().setFont(new Font("Courier New", 0, 14));

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        addCodeSnippetsPanel();
        panel.add(codeSTA.scrollPane);

        JPanel buttonsPn = new JPanel();
        buttonsPn.setLayout(new BoxLayout(buttonsPn, BoxLayout.LINE_AXIS));

        addIndentBt(buttonsPn);
        addWidth(buttonsPn, 10);
        addHelpBt(builder, buttonsPn);

        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonsPn);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void addCodeSnippetsPanel()
    {
        JPanel p = new JPanel();
        final JComboBox cb = new JComboBox();
        cb.addItem("player.setStamina(player.getStamina()+6);");
        cb.addItem("player.setSkill(player.getSkill()-1);");
        cb.addItem("player.setStamina(20);");
        cb.addItem("player.setSkill(6);");
        cb.addItem("player.incStamina(1);");
        cb.addItem("player.decStamina(1);");
        cb.addItem("player.incCombatSkill(2);");
        cb.addItem("player.decCombatSkill(2);");
        cb.addItem("player.incLuck(3);");
        cb.addItem("player.decLuck(3);");
        cb.addItem("player.incDamage(4);");
        cb.addItem("player.decDamage(4);");
        cb.addItem("player.skill(\"strengh\").inc(4);");
        cb.addItem("player.skill(\"climb-walls\").dec(2);");
        cb.addItem("player.addAttribute(\"long-sword\");");
        cb.addItem("player.addAttribute(\"apples\", 3);");
        cb.addItem("player.addAttribute(\"potion\", \"red\");");
        cb.addItem("player.addAttribute(\"iron shield\");");
        cb.addItem("player.removeAttribute(\"horse\");");
        cb.addItem("player.addSkill(\"light-saber fight\", 9);");
        cb.addItem("player.addSkill(\"climb walls\", 8);");
        cb.addItem("if(player.has(\"retractable claws\"))");
        cb.addItem("if(player.getIntValue(\"apple\")==1) { pathsToHide.add(2);");
        cb.addItem("ammo = player.getIntValue(\"gun ammo\");");
        cb.addItem("player.addAttribute(\"gun ammo\", ammo + 6);");
        cb.addItem("player.incIntValue(\"coins\", 30);");
        cb.addItem("player.incIntValue(\"coins\", 30);");
        cb.addItem("n = player.getIntValue(\"health potion\");");
        cb.addItem("player.addAttribute(\"health potion\", n-1);");
        cb.addItem("player.decIntValue(\"stamina\", 6);");
        cb.addItem("if ( player.getIntValue(\"cat statues\")==2 ) {");
        cb.addItem("if ( player.getStamina() <= 0 ) { go=42;");

        cb.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                try {
                    String snippet = cb.getSelectedItem().toString() + "\n";
                    textArea.getDocument().insertString(textArea.getCaretPosition(), snippet, null);
                } catch (BadLocationException e) {
                    throw new AdventureException(e);
                }
            }
        });

        p.add(new JLabel("Code snippets:"));
        p.add(cb);
        panel.add(p);
    }

    private void addHelpBt(final IBuilder builder, JPanel buttonsPn)
    {
        JButton codeHelpBt = new JButton("Help and Code snippets", Util.getImage("help.png"));
        codeHelpBt.setMnemonic('H');
        codeHelpBt.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        codeHelpBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                builder.showSceneCodeHelpDialog();
            }
        });
        buttonsPn.add(codeHelpBt);
    }

    private void addIndentBt(JPanel buttonsPn)
    {
        JButton trimSpacesBt = new JButton("Indent", Util.getImage("indent-icon.png"));
        trimSpacesBt.setMnemonic('i');
        trimSpacesBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                indentCodeAction(getTextArea());
            }
        });
        buttonsPn.add(trimSpacesBt);
    }

    public JTextArea getTextArea()
    {
        return textArea;
    }

    public JPanel getPanel()
    {
        return panel;
    }
}
