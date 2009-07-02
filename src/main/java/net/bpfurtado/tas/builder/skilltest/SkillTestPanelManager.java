/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com] - 2005
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
package net.bpfurtado.tas.builder.skilltest;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.Skill;

public class SkillTestPanelManager
{
    private JTextField nameTf;
    private JPanel p;

    private Scene s;

    public SkillTestPanelManager(Scene s)
    {
        this.s = s;

        if (s.getSkillToTest() == null) {
            addInitialSkill();
        }

        widgets();
        events();

    }

    private void widgets()
    {
        p = new JPanel();
        p.add(new JLabel("Skill to test:"));

        nameTf = new JTextField(s.getSkillToTest().getName(), 35);
        p.add(nameTf);
    }

    private void events()
    {
        nameTf.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                System.out.println(".changedUpdate()");
            }

            public void insertUpdate(DocumentEvent e)
            {
                s.getSkillToTest().setName(nameTf.getText());
                System.out.println(".insertUpdate()");
            }

            public void removeUpdate(DocumentEvent e)
            {
                s.getSkillToTest().setName(nameTf.getText());
                System.out.println(".removeUpdate()");
            }
        });
    }

    private void addInitialSkill()
    {
        s.setSkillToTest(new Skill(""));
    }

    public void setSkill(String name)
    {
        s.getSkillToTest().setName(name);
        nameTf.setText(name);
    }

    public JPanel getPanel()
    {
        return p;
    }
}
