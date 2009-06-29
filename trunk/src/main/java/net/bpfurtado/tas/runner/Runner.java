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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.EntityPersistedOnFileOpenner;
import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.builder.EntityPersistedOnFileOpenActionListener;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.Game;
import net.bpfurtado.tas.model.GameImpl;
import net.bpfurtado.tas.model.GoToSceneListener;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.PlayerEvent;
import net.bpfurtado.tas.model.PlayerEventListener;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.Skill;
import net.bpfurtado.tas.model.SkillTestListener;
import net.bpfurtado.tas.model.combat.EndOfCombatListener;
import net.bpfurtado.tas.model.persistence.XMLAdventureReader;
import net.bpfurtado.tas.runner.combat.CombatFrame;
import net.bpfurtado.tas.view.ErrorFrame;
import net.bpfurtado.tas.view.RecentFilesMenuController;
import net.bpfurtado.tas.view.SettingsUtil;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public class Runner extends JFrame implements GoToSceneListener, EndOfCombatListener, SkillTestListener, PlayerEventListener
{
    private static final boolean DONT_EXEC_SCENE_ACTIONS = false;

    private static final long serialVersionUID = -2215614593644954452L;

    private static final Logger logger = Logger.getLogger(Runner.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

    private Game game;
    private Adventure adventure;

    private JPanel gamePanel;
    private JPanel scenesPn;

    private JLabel advName;
    private JTextArea sceneTA;
    private JPanel pathsPn;
    private JPanel endPn;

    private JPanel mainPanel;
    private JTextArea logTA;
    private JMenuItem saveMnIt;

    private final JFileChooser fileChooser = new JFileChooser();
    private List<EntityPersistedOnFileOpenActionListener> openAdventureListeners;
    private RecentFilesMenuController recentAdventuresMenuController;
    private RecentFilesMenuController recentSavedGamesMenuController;

    private PlayerPanelController statsView;
    protected CombatFrame combatFrame;
    protected Object skillToTestFrame;

    private List<EntityPersistedOnFileOpenActionListener> openSavedGamesListener;

    public static Runner runAdventure(File adventureFile)
    {
        Runner r = new Runner();
        r.open(adventureFile);
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
        if (!Conf.runner().is("openLastAdventureOnStart", false))
            return;

        File advFile = new File(Conf.runner().get("lastAdventure"));
        if (advFile.exists()) {
            open(advFile);
        }
    }

    private void init()
    {
        buildRecentMenus();
        initView();
    }

    private void buildRecentMenus()
    {
        EntityPersistedOnFileOpenner advOpenner = new EntityPersistedOnFileOpenner()
        {
            public String getApplicationName()
            {
                return Runner.this.getApplicationName();
            }

            public boolean hasAnOpenEntity()
            {
                return Runner.this.hasAnOpenEntity();
            }

            public boolean isDirty()
            {
                return Runner.this.isDirty();
            }

            public void open(File file)
            {
                Runner.this.open(file);
            }

            public void save(boolean isSaveAs)
            {
                Runner.this.save(isSaveAs);
            }
        };
        recentAdventuresMenuController = new RecentFilesMenuController(advOpenner, this, "recentAdventures.txt");

        EntityPersistedOnFileOpenner savedGamesOpenner = new EntityPersistedOnFileOpenner()
        {
            public String getApplicationName()
            {
                return Runner.this.getApplicationName();
            }

            public boolean hasAnOpenEntity()
            {
                return false;
            }

            public boolean isDirty()
            {
                return false;
            }

            public void open(File file)
            {
                Runner.this.openSaveGameFile(file);
            }

            public void save(boolean isSaveAs)
            {
                Runner.this.saveGameAction();
            }
        };

        recentSavedGamesMenuController = new RecentFilesMenuController(savedGamesOpenner, this, "recentSavedGames.txt");

        openAdventureListeners = new LinkedList<EntityPersistedOnFileOpenActionListener>();
        openAdventureListeners.add(recentAdventuresMenuController);

        this.openSavedGamesListener = new LinkedList<EntityPersistedOnFileOpenActionListener>();
        openSavedGamesListener.add(recentSavedGamesMenuController);
    }

    private void initView()
    {
        menu();

        add(createMainPanel());

        Util.setBoundsFrom(Conf.runner(), this);

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

    private JPanel createMainPanel()
    {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createGamePanel());
        mainPanel.add(createLogPanel(), BorderLayout.PAGE_END);

        mainPanel.setVisible(false);
        return mainPanel;
    }

    private Box createLogPanel()
    {
        Box box = Box.createVerticalBox();
        this.logTA = new JTextArea();
        logTA.setText("The adventure starts");
        JScrollPane sp = new JScrollPane(logTA);
        sp.setBorder(BorderFactory.createTitledBorder("Game log"));
        sp.setPreferredSize(new Dimension(100, 100));
        box.add(sp);

        return box;
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

        game.getPlayer().add(this);
    }

    public void open(File saveFile)
    {
        adventure = new XMLAdventureReader().read(saveFile);

        Conf.runner().set("lastOpenDir", saveFile.getParentFile().getAbsolutePath());
        Conf.runner().set("lastAdventure", saveFile.getAbsolutePath());
        Conf.runner().save();

        setTitle(adventure.getName() + " - Runner - Text Adventures Suite");
        saveMnIt.setEnabled(true);

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
     * TODO review this method usage
     */
    public void goTo(Scene sceneToOpen)
    {
        /*
         * logger.debug("Going to " + scene.getText());
         * 
         * sceneTA.setText("[" + scene.getId() + "]\n" + scene.getText());
         * sceneTA.setCaretPosition(0);
         * 
         * if(scene.isEnd()) { gameOver(); return; }
         */

        openScene(sceneToOpen);
    }

    private void createPathsPane(Scene scene)
    {
        pathsPn.removeAll();
        for (final IPath path : scene.getPaths()) {
            if (!path.isVisible()) {
                path.setVisible(true); // reset the path visibility status
                continue;
            }

            final JLabel pathLb = new JLabel(path.getText());
            pathLb.setFont(Builder.FONT);
            pathLb.setAlignmentX(LEFT_ALIGNMENT);

            if (path.getTo() == null) {
                pathLb.setText(path.getText() + " (no destiny...)");
                pathLb.setToolTipText("This path has no scene to go");
            }
            pathLb.setText("<html><strong>" + pathLb.getText() + "</strong></html>");

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

    private void openScene(Scene to)
    {
        openScene(to, true);
    }

    /**
     * Attention here!!!
     */
    private void openScene(Scene to, boolean execActions)
    {
        if (execActions) {
            game.open(to);
        } else {
            game.openNoActions(to);
        }

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
                    Runner.this.combatFrame = new CombatFrame(Runner.this, game.getPlayer(), game.getCurrentScene().getCombat(), Runner.this);
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
                // 333
                public void actionPerformed(ActionEvent e)
                {
                    Skill skill = game.getCurrentScene().getSkillToTest();
                    logger.debug("game.getCurrentScene().getSkillToTest()=" + skill.getName());
                    Runner.this.skillToTestFrame = new SkillTestFrame(Runner.this, game.getPlayer(), skill, Runner.this);
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

    public boolean hasAnOpenEntity()
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
        for (EntityPersistedOnFileOpenActionListener listener : openAdventureListeners) {
            listener.fileOpenedAction(adventureFile);
        }
    }

    private JPanel createGamePanel()
    {
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.LINE_AXIS));

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

        gamePanel.add(scenesPn);

        statsView = new PlayerPanelController();
        gamePanel.add(statsView.getPanel());

        return gamePanel;
    }

    private JPanel createEndPanel()
    {
        JPanel endPn = new JPanel();
        endPn.setLayout(new BoxLayout(endPn, BoxLayout.LINE_AXIS));

        JLabel endHereLb = new JLabel("Your adventure ends here!");
        endHereLb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        endHereLb.setFont(new Font("Tahoma", 0, 18));
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
        menuAdv(menuBar);
        menuSaveGame(menuBar);

        SettingsUtil.addSettingsMenu(menuBar, Conf.runner());
        Util.addHelpMenu(menuBar, this);

        setJMenuBar(menuBar);
    }

    private void menuSaveGame(JMenuBar menuBar)
    {
        JMenu saveGameMenu = new JMenu("Save Game");
        saveGameMenu.setMnemonic('G');
        menuBar.add(saveGameMenu);

        this.saveMnIt = Util.menuItem("Save Game", 'S', KeyEvent.VK_S, "disk.png", saveGameMenu, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                saveGameAction();
            }
        });

        Util.menuItem("Open Saved Game", 'O', KeyEvent.VK_O, "folder_table.png", saveGameMenu, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                loadSavedGameAction();
            }
        });

        saveGameMenu.add(new JSeparator());
        saveGameMenu.add(recentSavedGamesMenuController.getOpenRecentMenu());
    }

    private void menuAdv(JMenuBar menuBar)
    {
        JMenu advMenu = new JMenu("Adventure");
        advMenu.setMnemonic('A');

        menuBar.add(advMenu);

        JMenuItem startAgainMnIt = new JMenuItem("Start again", Util.getImage("arrow_redo.png"));
        startAgainMnIt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int answer = JOptionPane.showConfirmDialog(Runner.this, "Start Adventure again?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    startAgain();
                }
            }
        });
        advMenu.add(startAgainMnIt);

        JMenuItem openMnIt = new JMenuItem("Open", Util.getImage("folder_table.png"));
        openMnIt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                openMenuAction();
            }
        });
        advMenu.add(openMnIt);

        advMenu.add(recentAdventuresMenuController.getOpenRecentMenu());
        advMenu.add(new JSeparator());

        JMenuItem exitBt = new JMenuItem("Exit", 'x');
        exitBt.setIcon(Util.getImage("cancel.png"));

        exitBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                exitApplication();
            }
        });
        advMenu.add(exitBt);
    }

    protected void loadSavedGameAction()
    {
        logger.debug("Load...");
        fileChooser.setCurrentDirectory(Conf.getSavedGamesFolder());
        int returnVal = fileChooser.showOpenDialog(Runner.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File saveGameFile = fileChooser.getSelectedFile();
            logger.debug("Opening: " + saveGameFile.getName() + ".");
            openSaveGameFile(saveGameFile);
        }
    }

    private void openSaveGameFile(File saveGameFile)
    {
        try { // 222
            XStream xs = new XStream();
            SaveGame saveGame = (SaveGame) xs.fromXML(new FileReader(saveGameFile));
            Conf.runner().set("lastSavedGameFile", saveGameFile.getAbsolutePath());

            open(new File(saveGame.getAdventureFilePath()));
            game.open(saveGame);
            game.getPlayer().add(this);
            openScene(adventure.getScene(saveGame.getSceneId()), DONT_EXEC_SCENE_ACTIONS);

            fireOpenSavedGameEvent(saveGameFile);
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }

    private void fireOpenSavedGameEvent(File saveGameFile)
    {
        for (EntityPersistedOnFileOpenActionListener listener : openSavedGamesListener) {
            listener.fileOpenedAction(saveGameFile);
        }
    }

    private void saveGameAction()
    {
        SaveGame saveGame = new SaveGame(game.getPlayer(), game.getCurrentScene().getId());
        saveGame.setAdventureFilePath(Conf.runner().get("lastAdventure"));

        File savedGamesFolder = Conf.getSavedGamesFolder();

        // 111
        String saveGameName = savedGamesFolder.getAbsolutePath() + File.separator + adventure.getName();
        saveGameName += "#" + sdf.format(new Date());
        saveGameName = saveGameName.replaceAll(" ", "") + ".saveGame.tas";

        try {
            log("Saving game to file: [" + saveGameName + "]...");
            File saveGameFile = new File(saveGameName);
            saveGameFile.createNewFile();

            XStream xs = new XStream();
            String savedGameXML = xs.toXML(saveGame);
            PrintWriter writer = new PrintWriter(saveGameFile);
            writer.print(savedGameXML);
            writer.flush();
            writer.close();
            log("Game saved!");

            // Will signal the recent menu to update: FIXME: it's a bit
            // confusing...
            fireOpenSavedGameEvent(saveGameFile);
        } catch (IOException e) {
            throw new AdventureException(e);
        }
    }

    private void startAgain()
    {
        gamePanel.remove(endPn);
        endPn.setVisible(false);
        pathsPn.setVisible(true);
        statsView.startAgain();
        logTA.setText("");
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
            int answer = JOptionPane.showConfirmDialog(Runner.this, "Close current adventure?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.NO_OPTION)
                return;
        }

        fileChooser.setCurrentDirectory(new File(Conf.runner().get("lastOpenDir")));
        int returnVal = fileChooser.showOpenDialog(Runner.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File adventureFile = fileChooser.getSelectedFile();
            System.out.println("Opening: " + adventureFile.getName() + ".");
            open(adventureFile);
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

    public void save(boolean isSaveAs)
    {
        // Does nothing in this operations as a Adventure Runner
    }

    public void combatEnded(boolean keepAdventure)
    {
        if (keepAdventure) {
            openScene(game.getCurrentScene().getPaths().get(0).getTo());
        } else {
            gameOver();
        }
    }

    public void setSkillful(boolean skillful)
    {
        int sceneToGoIndex = 0;
        if (!skillful) {
            sceneToGoIndex = 1;
        }
        openScene(game.getCurrentScene().getPaths().get(sceneToGoIndex).getTo());
    }

    private void exitApplication()
    {
        Util.exitApplication(this, Conf.runner());
    }

    public void receive(PlayerEvent ev)
    {
        statsView.updateView(ev);
        log(ev.getDesc());
    }

    private void log(String msg)
    {
        try {
            Document doc = logTA.getDocument();
            doc.insertString(doc.getLength(), "\n" + msg, null);
            logTA.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            throw new AdventureException(e);
        }
    }
}
