/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.bpfurtado.tas.AdventureOpenner;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.builder.OpenAdventureListener;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.Game;
import net.bpfurtado.tas.model.GameImpl;
import net.bpfurtado.tas.model.GoToSceneListener;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.Skill;
import net.bpfurtado.tas.model.SkillTestListener;
import net.bpfurtado.tas.model.combat.EndOfCombatListener;
import net.bpfurtado.tas.model.persistence.XMLAdventureReader;
import net.bpfurtado.tas.runner.combat.CombatFrame;
import net.bpfurtado.tas.view.ErrorFrame;
import net.bpfurtado.tas.view.RecentAdventuresMenuController;
import net.bpfurtado.tas.view.SettingsUtil;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

public class Runner extends JFrame 
	implements AdventureOpenner, GoToSceneListener, EndOfCombatListener, SkillTestListener 
{
    private static final long serialVersionUID = -2215614593644954452L;

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(Runner.class);

    private Game game;
    private Adventure adventure;

    private JPanel mainPanel;
	private JPanel scenesPn;

	private JLabel advName;
    private JTextArea sceneTA;
    private JPanel pathsPn;
    private JPanel endPn;

    private final JFileChooser fileChooser = new JFileChooser();
    private List<OpenAdventureListener> openAdventureListeners;
    private RecentAdventuresMenuController recentMenuController;

    private PlayerPanelController statsView;

	protected CombatFrame combatFrame;

	protected Object skillToTestFrame;
	
	public static Runner runAdventure(File adventureFile) 
	{
		Runner r = new Runner();
		r.openAdventure(adventureFile);
		return r;
	}
	
	public static Runner runLastAdventure() 
	{
		Runner r = new Runner();
		r.openLastAdventure();
		return r;
	}

    private Runner()
    {
        init();
    }

    public Runner(Adventure adventure)
    {
        start(adventure, adventure.getStart());
    }

    public Runner(Adventure adventure, Scene actualScene)
    {
        start(adventure, actualScene);
    }
	
    private void openLastAdventure()
	{
    	if(!Conf.runner().is("openLastAdventureOnStart")) return;
    	
		File advFile = new File(Conf.runner().get("lastAdventure"));
		if (advFile.exists()) {
			openAdventure(advFile);
		}
	}

    private void init()
    {
        openAdventureListeners = new LinkedList<OpenAdventureListener>();
        recentMenuController = new RecentAdventuresMenuController(this, this);
        openAdventureListeners.add(recentMenuController);

        initView();
    }

    private void initView()
    {
        menu();

        createMainPanel();
        
        Conf conf = Conf.runner();
		int x = conf.getInt("bounds.x", 235);
		int y = conf.getInt("bounds.y", 260);
		int w = conf.getInt("bounds.w", 665);
		int h = conf.getInt("bounds.h", 400);

        setBounds(x, y, w, h); //setBounds(235, 260, 806, 430);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
        	/**
        	 * TODO CHANGE TO WINDOW CLOSED!
        	 */
            @Override
            public void windowClosing(WindowEvent e)
            {
                exitApplication();
            }
        });

        setTitle("Runner - Text Adventures Suite");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(false);
        setVisible(true);
    }

    private void start(Adventure adventure, Scene sceneWhereToStart)
    {
        init();
        this.adventure = adventure;
        createGame(adventure);
        openScene(sceneWhereToStart);
    }

    private void createGame(Adventure a)
    {
        this.game = new GameImpl(a);
        statsView.setGame(game);
        game.addGoToSceneListener(this);
    }

    public void openAdventure(File saveFile)
    {
        adventure = new XMLAdventureReader().read(saveFile);

        Conf.runner().set("lastOpenDir", saveFile.getParentFile().getAbsolutePath());
        Conf.runner().set("lastAdventure", saveFile.getAbsolutePath());
		Conf.runner().save();
		
		setTitle(adventure.getName() + " - Runner - Text Adventures Suite");

        fireOpenAdventureEvent(saveFile);
        startGame();
    }
    
    private void startGame()
    {
		mainPanel.setVisible(true);
		createGame(adventure);
		openSceneLight(adventure.getStart());
    }

    /**
     * TODO rever o uso deste método
     */
    public void goTo(Scene sceneToOpen)
    {
        /*logger.debug("Going to " + scene.getText());

        sceneTA.setText("[" + scene.getId() + "]\n" + scene.getText());
        sceneTA.setCaretPosition(0);

        if(scene.isEnd()) {
            gameOver();
            return;
        }*/

    	openScene(sceneToOpen);
    }

	private void createPathsPane(Scene scene)
	{
		pathsPn.removeAll();
        for (final IPath path : scene.getPaths()) {
            if(!path.isVisible()) {
            	path.setVisible(true); //reset the path visibility status
            	continue;
            }

            final JLabel pathLb = new JLabel(path.getText());
            pathLb.setFont(Builder.FONT);
            pathLb.setAlignmentX(LEFT_ALIGNMENT);

            if (path.getTo() == null) {
                pathLb.setText(path.getText() + " (no destiny...)");
                pathLb.setToolTipText("This path has no scene to go");
            }
            pathLb.setText("<html><strong>"+pathLb.getText()+"</strong></html>");

            pathLb.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
            addEvents(path, pathLb);
            pathsPn.add(pathLb);
        }
	}

	private void addEvents(final IPath path, final JLabel pathLb)
	{
		pathLb.addMouseListener(new MouseAdapter()
		{
		    IPath _path = path;
		    public void mouseClicked(MouseEvent e)
		    {
		        if (path.getTo() != null) {
		            pathLbMouseClicked(_path);
		        }
		    }

		    public void mouseEntered(MouseEvent e)
		    {
		        if (path.getTo() == null) {
		            pathLb.setForeground(Color.RED);
		            pathLb.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		        } else {
		            pathLb.setForeground(Color.BLUE);
		            pathLb.setCursor(new Cursor(Cursor.HAND_CURSOR));
		        }
		    }

		    public void mouseExited(MouseEvent e)
		    {
		        pathLb.setForeground(Color.BLACK);
		        pathLb.setCursor(null);
		    }
		});
	}

	private void pathLbMouseClicked(IPath path)
	{
		try {
			openScene(path.getTo());
		} catch (Exception e) {
			new ErrorFrame(this, e, "Open Scene");
		}
	}

	/**
	 * Attention here!!!
	 */
	private void openScene(Scene to)
	{
		game.open(to);

		to = game.getCurrentScene();
		openSceneLight(to);

		if (to.isEnd()) {
			gameOver();
			return;
		} else if (to.getCombat() != null) {
			pathsPn.removeAll();
			JButton combatBt = new JButton("Combat");
			combatBt.setMnemonic('c');
			combatBt.setAlignmentX(CENTER_ALIGNMENT);
			combatBt.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Runner.this.combatFrame =
						new CombatFrame(
								Runner.this,
								game.getPlayer(),
								game.getCurrentScene().getCombat(),
								Runner.this);
				}
			});
			pathsPn.add(combatBt);
		} else if (to.getSkillToTest() != null) {
			pathsPn.removeAll();
			JButton skillToTestBt = new JButton("Test your " + to.getSkillToTest().getName() + " Skill!");
			skillToTestBt.setMnemonic('t');
			skillToTestBt.setAlignmentX(CENTER_ALIGNMENT);
			skillToTestBt.addActionListener(new ActionListener()
			{
				//333
				public void actionPerformed(ActionEvent e)
				{
					Skill skill = game.getCurrentScene().getSkillToTest();
					logger.debug("game.getCurrentScene().getSkillToTest()="+skill.getName());
					Runner.this.skillToTestFrame =
						new SkillTestFrame(
								Runner.this,
								game.getPlayer(),
								skill,
								Runner.this);
				}
			});
			pathsPn.add(skillToTestBt);
		}

        updateView();
	}

	private void openSceneLight(Scene sceneToOpen)
	{
		sceneTA.setText("[" + sceneToOpen.getId() + "]\n" + sceneToOpen.getText());
		sceneTA.setCaretPosition(0);

		createPathsPane(sceneToOpen);
		updateView();
	}

	public boolean hasAnOpenAdventure()
    {
        return adventure != null;
    }

    private void updateView()
    {
        advName.setText(adventure.getName());
        statsView.updateView();

        Util.showComponent(mainPanel);
    }

    private void fireOpenAdventureEvent(File adventureFile)
    {
        for(OpenAdventureListener listener : openAdventureListeners) {
            listener.adventureOpenned(adventureFile);
        }
    }

    private void createMainPanel()
    {
        mainPanel = new JPanel();
    	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

    	scenesPn = new JPanel();
    	scenesPn.setAlignmentX(LEFT_ALIGNMENT);
        scenesPn.setLayout(new BoxLayout(scenesPn, BoxLayout.PAGE_AXIS));

        advName = new JLabel();
        advName.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        advName.setAlignmentX(LEFT_ALIGNMENT);
        scenesPn.add(advName);

        sceneTA = new JTextArea();
        sceneTA.setFont(Builder.FONT);
        sceneTA.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        sceneTA.setEditable(false);
        sceneTA.setLineWrap(true);
        sceneTA.setWrapStyleWord(true);
        JScrollPane textScroll = new JScrollPane(sceneTA);
        textScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textScroll.setAlignmentX(LEFT_ALIGNMENT);
        scenesPn.add(textScroll);

        pathsPn = new JPanel();
        pathsPn.setLayout(new BoxLayout(pathsPn, BoxLayout.PAGE_AXIS));
        pathsPn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pathsPn.setAlignmentX(LEFT_ALIGNMENT);
        scenesPn.add(pathsPn);

        endPn = createEndPanel();
        endPn.setVisible(false);
        endPn.setAlignmentX(LEFT_ALIGNMENT);
        scenesPn.add(endPn);

        mainPanel.add(scenesPn);

        statsView = new PlayerPanelController();
        mainPanel.add(statsView.getPanel());

        mainPanel.setVisible(false);
        add(mainPanel);
    }

    private JPanel createEndPanel()
    {
        JPanel endPn = new JPanel();
        endPn.setLayout(new BoxLayout(endPn, BoxLayout.LINE_AXIS));

        JLabel endHereLb = new JLabel("Your adventure ends here!");
        endHereLb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        endHereLb.setFont(new java.awt.Font("Tahoma", 0, 18));
        endHereLb.setAlignmentX(CENTER_ALIGNMENT);
        endPn.add(endHereLb);

        JButton startAgainBt = new JButton("Start again");
        startAgainBt.setMnemonic('S');
        startAgainBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startAgain();
            }
        });
        endPn.add(startAgainBt);

        return endPn;
    }

    private void menu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Adventure");
        menu.setMnemonic('A');

        menuBar.add(menu);

        JMenuItem startAgainMnIt = new JMenuItem("Start again", Util.getImage("arrow_redo.png"));
        startAgainMnIt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int answer = JOptionPane.showConfirmDialog(
                        Runner.this,
                        "Start Adventure again?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    startAgain();
                }
            }
        });
        menu.add(startAgainMnIt);

        JMenuItem openMnIt = new JMenuItem("Open", Util.getImage("folder_table.png"));
        openMnIt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                openMenuAction();
            }
        });
        menu.add(openMnIt);
        menu.add(recentMenuController.getOpenRecentMenu());
        menu.add(new JSeparator());

        JMenuItem exitBt = new JMenuItem("Exit", 'x');
        exitBt.setIcon(Util.getImage("cancel.png"));

        exitBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                exitApplication();
            }
        });
        menu.add(exitBt);

        SettingsUtil.addSettingsMenu(menuBar, Conf.runner());
        Util.addHelpMenu(menuBar, this);

        setJMenuBar(menuBar);
    }

    private void startAgain()
    {
        mainPanel.remove(endPn);
        endPn.setVisible(false);
        pathsPn.setVisible(true);
        statsView.startAgain();
        startGame();
    }

    private void gameOver()
    {
        scenesPn.add(endPn);
        pathsPn.setVisible(false);
        endPn.setVisible(true);
    }

    private void openMenuAction()
    {
        if (adventure != null) {
            int answer = JOptionPane.showConfirmDialog(Runner.this,
                    "Close current adventure?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.NO_OPTION) return;
        }

        fileChooser.setCurrentDirectory(new File(Conf.runner().get("lastOpenDir")));
        int returnVal = fileChooser.showOpenDialog(Runner.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File adventureFile = fileChooser.getSelectedFile();
            System.out.println("Opening: " + adventureFile.getName() + ".");
            openAdventure(adventureFile);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
			public void run()
			{
				Runner runner = null;
				try {
					runner = new Runner();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(runner, e.getMessage());
				}
			}
		});
    }

    public String getApplicationName()
    {
        return "runner";
    }

    public boolean isDirty()
    {
        return false;
    }

    public void saveAdventure(boolean isSaveAs)
    {
        //Does nothing in this operations as a Adventure Runner
    }

	public void combatEnded(boolean keepAdventure)
	{
		if(keepAdventure) {
			openScene(game.getCurrentScene().getPaths().get(0).getTo());
		} else {
			gameOver();
		}
	}
	
	public void setSkillful(boolean skillful)
	{
		int sceneToGoIndex = 0;
		if(!skillful) {
			sceneToGoIndex = 1;
		}
		openScene(game.getCurrentScene().getPaths().get(sceneToGoIndex).getTo());
	}

	private void exitApplication()
	{
		setVisible(false);

		Conf conf = Conf.runner();
		conf.set("bounds.x", getX());
		conf.set("bounds.y", getY());
		conf.set("bounds.w", getWidth());
		conf.set("bounds.h", getHeight());
		conf.save();

		dispose();

		Util.terminateProcessIfAlone();
	}
}