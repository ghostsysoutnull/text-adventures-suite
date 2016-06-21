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
package net.bpfurtado.tas.runner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.model.Player;
import net.bpfurtado.tas.model.Skill;
import net.bpfurtado.tas.model.SkillTestListener;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

public class SkillTestFrame extends JDialog
{
    private static Logger logger = Logger.getLogger(SkillTestFrame.class);

    private static final long serialVersionUID = -2131899249629843236L;

    private static final Font FONT = new Font("Tahoma", 0, 14);
    private static final Font FONT_BOLD = new Font("Tahoma", 1, 14);

    private static final String NEXT_NEED_TO_BE_PREFIX = "Next need to be less than or equal to: [";

    private JFrame invokerFrame;
    private JLabel dice2;
    private JLabel dice1;
    private JLabel expected;
    private Skill skillToTest;
    private JLabel nextNeedToBe;

    private Player player;
    private Integer firstDiceResult;

    private Integer levelToTest;

    private Random rnd = new Random();

    private SkillTestListener endOfSkillTestListener;

    public SkillTestFrame(SkillTestListener endOfSkillTestListener, Player player, Skill skillToTest,
            JFrame invokerFrame)
    {
        logger.debug("skillToTest.getName()=" + skillToTest.getName() + ":" + skillToTest.getLevel());
        this.invokerFrame = invokerFrame;
        this.skillToTest = skillToTest;
        this.player = player;
        this.endOfSkillTestListener = endOfSkillTestListener;

        initView();
    }

    private void initView()
    {
        widgets();

        setTitle("Test your Skill! - Text Adventures Suite");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        setModal(true);
        setResizable(false);

        pack();
        Util.centerPosition(invokerFrame, this, 340, 122);
        setVisible(true);
    }

    private void widgets()
    {
        final JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

        final JPanel p = new JPanel();
        p.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        final JButton b = new JButton("Play one dice");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (firstDiceResult == null) {
                    firstDiceResult = rnd.nextInt(6) + 1;
                    dice1.setText("[" + firstDiceResult + "] ");
                    nextNeedToBe.setText(NEXT_NEED_TO_BE_PREFIX + (getSkillLevelToTest() - getFirstResult()) + "] ");
                } else {
                    int secondDiceResult = rnd.nextInt(6) + 1;
                    int total = firstDiceResult + secondDiceResult;
                    dice2.setText("[" + secondDiceResult + "] = [" + total + "]");

                    String text = "YOU LOOSE";
                    boolean skillful = total <= getSkillLevelToTest();
                    if (skillful) {
                        SoundUtil.playInternalClip("dsboscub");
                        text = "YOU WON";
                        /*
                         * We need to find the skill again as this instance we have came from the scene XML
                         */
                        player.skill(skillToTest.getName()).dec(1);
                    } else {
                        SoundUtil.playInternalClip("dsclaw");
                    }
                    main.add(createEndButton(text, skillful), BorderLayout.PAGE_END);
                    main.add(Box.createRigidArea(new Dimension(0, 4)));

                    b.setEnabled(false);
                }
            }
        });
        p.add(b);

        dice1 = label("[X] ");
        p.add(dice1);

        dice2 = label("[X] ");
        p.add(dice2);

        expected = label("Skill: [" + getSkillLevelToTest() + "] ");
        p.add(expected);

        nextNeedToBe = label(NEXT_NEED_TO_BE_PREFIX + (getSkillLevelToTest() - getFirstResult()) + "] ");
        p.add(nextNeedToBe);

        main.add(p);
        add(main);
    }

    private int getFirstResult()
    {
        return (firstDiceResult == null ? 0 : firstDiceResult);
    }

    private int getSkillLevelToTest()
    {
        if (levelToTest != null) {
            return levelToTest;
        }
        for (Skill s : player.getSkills()) {
            if (s.getName().equals(skillToTest.getName())) {
                levelToTest = s.getLevel();
                return levelToTest;
            }
        }
        throw new AdventureException("Skill " + skillToTest.getName() + " not found in player skills.");
    }

    private JButton createEndButton(String txt, final boolean skillful)
    {
        JButton b = new JButton(txt);
        b.setFont(FONT_BOLD);
        b.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
                endOfSkillTestListener.setSkillful(skillful);
            }
        });

        return b;
    }

    private JLabel label(String txt)
    {
        JLabel l = new JLabel(txt);
        l.setFont(FONT);
        return l;
    }
}
