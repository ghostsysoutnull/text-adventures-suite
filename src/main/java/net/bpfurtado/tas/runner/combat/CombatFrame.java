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
package net.bpfurtado.tas.runner.combat;

import static javax.swing.BorderFactory.createBevelBorder;
import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import net.bpfurtado.tas.model.Player;
import net.bpfurtado.tas.model.combat.AttackResult;
import net.bpfurtado.tas.model.combat.AttackResultListener;
import net.bpfurtado.tas.model.combat.Combat;
import net.bpfurtado.tas.model.combat.CombatType;
import net.bpfurtado.tas.model.combat.EndOfCombatListener;
import net.bpfurtado.tas.model.combat.Fighter;
import net.bpfurtado.tas.view.Util;

public class CombatFrame extends JDialog implements AttackResultListener
{
    private static final Logger logger = Logger.getLogger(CombatFrame.class);

    private static final long serialVersionUID = -3354524968137077741L;
    private static final Font DEFAULT_FONT = new Font("Tahoma", 1, 14);

    private int round = 1;

    private Combat combat;
    private Fighter player;
    private Fighter currentEnemy;

    private LinkedList<Fighter> enemies = new LinkedList<Fighter>();
    private EndOfCombatListener endOfCombatListener;

    private JButton fightBt;
    private JButton tilDeathBt;
    private JPanel mainPn;
    private JPanel buttonsPn;

    @SuppressWarnings("rawtypes")
    private JList attackResultsList;

    private DefaultListModel<String> attackResultsListModel;

    private JFrame invokerFrame;

    public CombatFrame(EndOfCombatListener listener, Fighter p, Combat c, JFrame invokerFrame)
    {
        this.endOfCombatListener = listener;
        this.player = p;
        this.combat = c;
        this.invokerFrame = invokerFrame;

        /**
         * Creating copies not to mess with the Builder, if the Runner starts from it.
         */
        for (Fighter f : c.getEnemies()) {
            enemies.add(f.createCopy());
        }

        currentEnemy = enemies.getFirst();
        logState("CombatFrame::constr");

        initMainPanel();
        initFightersViews(mainPn);

        if (c.getType() == CombatType.oneAtATime) {
            currentEnemy = enemies.removeFirst();
            logState("CombatFrame::constr");
        }

        currentEnemy.addAtackResultListener(this);
        player.addAtackResultListener(this);

        initView(invokerFrame);
    }

    private void logState(String from)
    {
        logger.debug("{" + from + "} list of enemies(" + enemies.size() + "): " + enemies);
        logger.debug("\tRound: " + round + ", Current enemy: " + currentEnemy);
    }

    private void initView(JFrame invokerFrame)
    {
        widgets();

        setTitle("Fight! - Text Adventures Suite");
        Util.centerPosition(invokerFrame, this, 393, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        setModal(true);

        myPack();
        setVisible(true);
    }

    private void myPack()
    {
        Util.centerPosition(invokerFrame, this, 393, 320);
        pack();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void widgets()
    {
        mainPn.add(Box.createRigidArea(new Dimension(0, 16)));

        addButtons(mainPn);
        mainPn.add(Box.createRigidArea(new Dimension(0, 8)));

        this.attackResultsListModel = new DefaultListModel();
        attackResultsList = new JList(attackResultsListModel);
        JScrollPane sp = new JScrollPane(attackResultsList);
        sp.setMinimumSize(new Dimension(320, 150));
        sp.setAlignmentX(LEFT_ALIGNMENT);
        mainPn.add(sp);

        add(mainPn);
    }

    private void initMainPanel()
    {
        this.mainPn = new JPanel();
        mainPn.setLayout(new BoxLayout(mainPn, BoxLayout.PAGE_AXIS));
        mainPn.setMinimumSize(new Dimension(350, 40));
        mainPn.setBorder(createEmptyBorder(8, 14, 8, 14));
    }

    private void addButtons(JPanel mainPanel)
    {
        this.buttonsPn = new JPanel();
        buttonsPn.setLayout(new BoxLayout(buttonsPn, BoxLayout.LINE_AXIS));
        buttonsPn.setMinimumSize(new Dimension(320, 40));
        buttonsPn.setAlignmentX(LEFT_ALIGNMENT);

        fightBt = new JButton("Next Round");
        fightBt.setMinimumSize(new Dimension(100, 30));
        fightBt.setMnemonic('n');
        fightBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                nextRoundButtonAction();
            }
        });
        buttonsPn.add(fightBt);
        space(buttonsPn);

        tilDeathBt = new JButton("Fight until death!");
        tilDeathBt.setMinimumSize(new Dimension(130, 30));
        tilDeathBt.setMnemonic('f');
        tilDeathBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                tilDeathButtonAction();
            }
        });
        buttonsPn.add(tilDeathBt);

        mainPanel.add(buttonsPn);
    }

    private void tilDeathButtonAction()
    {
        Timer timer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev)
            {
                if (!nextRound()) {
                    nextEnemy();
                }
                
                if (player.isDead() || (enemies.isEmpty() && currentEnemy.isDead())) {
                    ((Timer) ev.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    private void nextRoundButtonAction()
    {
        if (!nextRound()) {
            nextEnemy();
        }
    }

    private void nextEnemy()
    {
        if (combat.getType() == CombatType.allAtTheSameTime)
            return;

        if (!enemies.isEmpty()) {
            if (combat.getType() == CombatType.oneAtATime) {
                currentEnemy = enemies.removeFirst();
                logState("nextEnemy:method");
            } else {
                int nextIdx = enemies.indexOf(currentEnemy) + 1;

                if (nextIdx == enemies.size())
                    currentEnemy = enemies.getFirst();
                else
                    currentEnemy = enemies.get(nextIdx);

                logState("nextEnemy:method_ELSE");
            }
            currentEnemy.addAtackResultListener(this);
        }
    }

    /**
     * @return if the combat has a nextRound
     */
    private boolean nextRound()
    {
        /* paint the current enemy view (FighterView) HERE */
        FighterView currentEnemyView = currentEnemy.getView();
        currentEnemyView.setCurrent(true);

        player.fightWith(currentEnemy);

        if (currentEnemy.isDead() && enemies.isEmpty()) {
            createEndOfCombatPanel("You won!", true);
        } else if (player.isDead()) {
            createEndOfCombatPanel("You died!", false);
        }

        if (combat.getType() == CombatType.allAtTheSameTime) {
            currentEnemyView.setCurrent(false);

            Fighter lastEnemy = currentEnemy;

            rotateToNextEnemy();

            currentEnemyView = currentEnemy.getView();
            currentEnemyView.setCurrent(true);

            if (lastEnemy.isDead()) {
                enemies.remove(lastEnemy);
                if (enemies.isEmpty()) {
                    createEndOfCombatPanel("You won!", true);
                    return false;
                }
            }
        }

        round++;

        myPack();
        
        logState("nextRound");

        return !(currentEnemy.isDead() || player.isDead());
    }

    private boolean rotateToNextEnemy()
    {
        logger.debug("START");
        int idx = enemies.indexOf(currentEnemy);
        if (idx != -1) {
            if (idx + 1 == enemies.size()) {
                currentEnemy = enemies.getFirst();
            } else {
                currentEnemy = enemies.get(idx + 1);
            }
            currentEnemy.addAtackResultListener(this);
            System.out.println("ROTATE --> " + currentEnemy);
            return true;
        } else {
            logger.warn(currentEnemy.getName() + " not found!");
            return false;
        }
    }

    private void notifyEndOfCombatListener(boolean keepAdventure)
    {
        endOfCombatListener.combatEnded(keepAdventure);
    }

    private void createEndOfCombatPanel(String string, final boolean keepAdventure)
    {
        mainPn.remove(buttonsPn);

        JButton closeBt = new JButton("Close");
        closeBt.setMnemonic('c');

        JPanel closePn = new JPanel();
        closeBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
                notifyEndOfCombatListener(keepAdventure);
            }
        });
        JLabel wonLn = new JLabel(string);
        wonLn.setFont(DEFAULT_FONT);
        closePn.add(wonLn);
        closePn.add(closeBt);
        add(closePn, BorderLayout.PAGE_END);

        pack();
    }

    private void initFightersViews(JPanel mainPn)
    {
        JPanel playerPanel = new FighterView(player).getPanel();
        playerPanel.setAlignmentX(LEFT_ALIGNMENT);
        playerPanel.setBorder(createCompoundBorder(createBevelBorder(0), createEmptyBorder(3, 3, 3, 3)));

        mainPn.add(playerPanel);
        mainPn.add(Box.createRigidArea(new Dimension(0, 12)));

        /* for each enemy create a fighter view */
        for (Fighter enemy : enemies) {
            FighterView fighterView = new FighterView(enemy);
            mainPn.add(fighterView.getPanel());
        }
    }

    private void space(JPanel p)
    {
        p.add(Box.createRigidArea(new Dimension(8, 0)));
    }

    public void attackResult(AttackResult attackResult)
    {
        /* JList output history only */

        attackResultsListModel.addElement(attackResult.roundInfoToString(round));

        attackResultsList.setSelectedIndex(attackResultsListModel.size() - 1);
        attackResultsList.ensureIndexIsVisible(attackResultsListModel.size() - 1);
    }

    public void roundEnded()
    {
        attackResultsListModel.addElement("==================");
    }

    public static void main(String[] args) throws Exception
    {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run()
            {
                Combat combat = new Combat();
                combat.add(new Fighter("Minotaur", 7, 6));
                combat.add(new Fighter("Hell Hound 1", 6, 5));
                combat.add(new Fighter("Hell Hound 2", 5, 4));
                combat.add(new Fighter("Goblin", 4, 5));
                combat.setType(CombatType.oneAtATime);

                Fighter player = new Player("Player", 4, 16);

                JFrame invokerFrame = new JFrame();
                invokerFrame.setBounds(100, 100, 800, 500);
                new CombatFrame(new EndOfCombatListener() {
                    public void combatEnded(boolean keepAdventure)
                    {
                        logger.debug("Keep adventure? " + keepAdventure);
                    }
                }, player, combat, invokerFrame);
                logger.debug("Swing thread END");
            }
        });
        logger.debug("End");
    }

    public void clean()
    {
        logger.warn("doing nothing");
    }

    public boolean relatesTo(Fighter enemy)
    {
        return false;
    }
}
