/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
 * Created on 04/10/2005 11:14:38
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

import static net.bpfurtado.tas.view.Util.addHeight;
import static net.bpfurtado.tas.view.Util.menuItem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.Workspace;
import net.bpfurtado.tas.builder.combat.BuilderCombatPanelManager;
import net.bpfurtado.tas.builder.depth.DepthScenesFrame;
import net.bpfurtado.tas.builder.export.WorkspaceExporter;
import net.bpfurtado.tas.builder.scenespanel.ChooseSceneDialog;
import net.bpfurtado.tas.builder.scenespanel.ScenesList;
import net.bpfurtado.tas.builder.scenespanel.ScenesListControllerFactory;
import net.bpfurtado.tas.builder.scenespanel.ScenesListControllerFactory.ScenesListControllerResult;
import net.bpfurtado.tas.builder.scenespanel.ScenesSource;
import net.bpfurtado.tas.builder.scenetype.SceneTypesWidgets;
import net.bpfurtado.tas.builder.skilltest.SkillTestPanelManager;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.DepthManager;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.SceneType;
import net.bpfurtado.tas.model.combat.Fighter;
import net.bpfurtado.tas.model.persistence.AdventureReaderException;
import net.bpfurtado.tas.runner.Runner;
import net.bpfurtado.tas.view.HelpDialog;
import net.bpfurtado.tas.view.SettingsUtil;
import net.bpfurtado.tas.view.TextComponentForPasteMouseListener;
import net.bpfurtado.tas.view.Util;
import net.bpfurtado.tas.view.recentmenu.EntityPersistedOnFileOpenner;
import net.bpfurtado.tas.view.recentmenu.RecentFilesMenuController;

/**
 * @author Bruno Patini Furtado
 */
public class Builder extends JFrame implements EntityPersistedOnFileOpenner, ScenesSource, AdventureNeedsSavingController, IBuilder, ImageReceiver
{
    private static final Logger logger = Logger.getLogger(Builder.class);
    private static final long serialVersionUID = -3424967248011145801L;
    public static final Font FONT = new Font("Tahoma", 0, 14);
    private static final String TAB_TITLE_COMBAT = "Combat";
    private static final String TAB_TITLE_SKILL_TEST = "Skill Test";

    private Workspace workspace;
    private Adventure adventure;
    private Scene currentScene;

    private JPanel mainPanel;
    private JPanel mainPathsPane;
    private JPanel pathsPane;
    private JPanel pathsBtsPane;
    private JTextField adventureNameTF;
    private JTextField sceneNameTF;
    private JTextField tagsTF;
    private JTextArea sceneTA;
    private JTextArea codeTA;
    private JTextArea assertionsTA;

    private JTabbedPane sceneTabs;
    private JButton playBt;
    private JButton newPathBt;
    private JButton saveBt;
    private JButton goBackToSceneFromBt;
    private JButton playFromCurrentBt;

    private JComboBox backToFromScenesCb;

    private JMenuItem saveMnIt;
    private JMenuItem saveAsMnIt;
    private JMenuItem playMnIt;
    private JMenuItem playFromCurrentMnIt;
    private JMenuItem byDepthMnIt;

    private int lastSceneListIndex = -1;

    private RandomPathNameGenerator randomPathNameGenerator = new RandomPathNameGenerator();

    private List<PathView> pathViews = new LinkedList<PathView>();
    private List<EntityPersistedOnFileOpenActionListener> openAdventureListeners;

    private ScenesList scenesList = null;
    private RecentFilesMenuController recentMenuController;

    boolean isDirty = false;

    private SceneTypesWidgets sceneTypesWidgets;

    private ImagePanelBuilder imagePanelBuilder;

    public Builder()
    {
        recentMenuController = new RecentFilesMenuController(this, this, "recentAdventures.txt");

        openAdventureListeners = new LinkedList<EntityPersistedOnFileOpenActionListener>();
        openAdventureListeners.add(recentMenuController);

        initView();
        markAsClean();

        openLastWorkspace();
    }

    private void openLastWorkspace()
    {
        boolean openLastAdventureOnStart = true;
        String confKey = "openLastAdventureOnStart";
        try {
            openLastAdventureOnStart = Conf.builder().is(confKey, false);
            if (!openLastAdventureOnStart) {
                return;
            }
        } catch (AdventureException e) {
            Conf.builder().set(confKey, openLastAdventureOnStart);
        }

        workspace = Workspace.loadFrom(Conf.builder().get("lastWorkspaceId")); // 222
        open(workspace);
    }

    public void markAsDirty()
    {
        updateTitle(true);
        saveMnIt.setEnabled(true);
        saveBt.setEnabled(true);
        isDirty = true;
    }

    public void markAsClean()
    {
        updateTitle();
        saveMnIt.setEnabled(false);
        saveBt.setEnabled(false);
        isDirty = false;
    }

    public String getApplicationName()
    {
        return "builder";
    }

    public Adventure getAdventure()
    {
        return this.adventure;
    }

    private void initView()
    {
        menu();
        widgets();

        Util.setBoundsFrom(Conf.builder(), this);

        updateTitle();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                exitApplication();
            }
        });
        setDefaultLookAndFeelDecorated(false);

        saveMnIt.setEnabled(false);
        saveAsMnIt.setEnabled(false);

        setVisible(true);
    }

    private void widgets()
    {
        mainPanel = new JPanel(new BorderLayout());

        ScenesListControllerResult factory = ScenesListControllerFactory.create(this, true);
        scenesList = factory.getScenesList();
        JPanel scenesListPanel = factory.getPanel();

        JPanel scenePane = scenesPaneWidgets();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scenesListPanel, scenePane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);
        splitPane.setContinuousLayout(true);

        JPanel advTitlePn = createAdvTitlePanel();

        mainPanel.add(advTitlePn, BorderLayout.PAGE_START);

        mainPanel.add(splitPane);
        mainPanel.setVisible(false);
        add(mainPanel);
    }

    private void updateTitle()
    {
        updateTitle(false);
    }

    private void updateTitle(boolean isDirty)
    {
        String title = isDirty ? "Unsaved! " : "";
        title += adventureNameTF.getText();
        if (title.length() > 0) {
            title += " - ";
        }
        title += "Text Adventure Builder";
        setTitle(title);
    }

    private JPanel createAdvTitlePanel()
    {
        JPanel advTitlePn = new JPanel(/* new BorderLayout() */);
        advTitlePn.setLayout(new BoxLayout(advTitlePn, BoxLayout.LINE_AXIS));

        JPanel centerPn = new JPanel();
        centerPn.setLayout(new BoxLayout(centerPn, BoxLayout.LINE_AXIS));
        centerPn.setAlignmentX(CENTER_ALIGNMENT);
        centerPn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        centerPn.add(Box.createRigidArea(new Dimension(7, 0)));
        centerPn.add(new JLabel("Name:"));
        centerPn.add(Box.createRigidArea(new Dimension(5, 0)));

        adventureNameTF = new JTextField(50);
        adventureNameTF.setMinimumSize(new Dimension(160, 23));
        adventureNameTF.setMaximumSize(new Dimension(600, 23));

        BuilderSwingUtils.addTextEventHandlers(adventureNameTF, this);
        centerPn.add(adventureNameTF);

        adventureNameTF.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e)
            {
                updateTitle();
            }

            public void insertUpdate(DocumentEvent e)
            {
                updateTitle();
            }

            public void removeUpdate(DocumentEvent e)
            {
                updateTitle();
            }
        });

        centerPn.add(Box.createHorizontalGlue());

        this.playFromCurrentBt = new JButton(Util.getImage("control_fastforward_blue.png"));
        playFromCurrentBt.setToolTipText("Play from current scene");
        playFromCurrentBt.setEnabled(false);
        playFromCurrentBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                playAdventureFromActualSceneMenuAction();
            }
        });
        centerPn.add(playFromCurrentBt);
        centerPn.add(Box.createRigidArea(new Dimension(3, 0)));

        playBt = new JButton(Util.getImage("control_play_blue.png"));
        playBt.setToolTipText("Saves the adventure and plays it from the Start Scene");
        playBt.setMnemonic('P');
        playBt.setEnabled(false);
        playBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                playAdventureAction();
            }
        });
        centerPn.add(Box.createRigidArea(new Dimension(3, 0)));
        centerPn.add(playBt);
        centerPn.add(Box.createRigidArea(new Dimension(3, 0)));

        saveBt = new JButton("Save", Util.getImage("disk.png"));
        saveBt.setMnemonic('S');
        saveBt.setEnabled(false);
        saveBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                saveAdventureMenuAction(false);
            }
        });
        centerPn.add(Box.createRigidArea(new Dimension(5, 0)));
        centerPn.add(saveBt);

        advTitlePn.add(centerPn);

        return advTitlePn;
    }

    public Scene getCurrentScene()
    {
        return this.currentScene;
    }

    public void setLastSceneListIndex(int lastSceneListIndex)
    {
        this.lastSceneListIndex = lastSceneListIndex;
    }

    public int getLastSceneListIndex()
    {
        return lastSceneListIndex;
    }

    public void switchTo(Scene sceneToSwitch)
    {
        /**
         * Se for o inicio de uma nova aventura então nao temos ainda uma actualScene então não devemos
         * salvar. Se setarmos a scene start para a actual agora a mesma será salva com os dados da gui
         * vazios pois o updateView ainda não foi feito.
         */
        if (currentScene != null) {
            save(currentScene);
        }
        goTo(sceneToSwitch);
    }

    public void goTo(Scene sceneToGo)
    {
        currentScene = sceneToGo;
        Conf.builder().set("lastViewedSceneId", currentScene.getId());

        updateView();
    }

    private void updateView()
    {
        updateView(true);
    }

    private void updateView(boolean focusToSceneTextArea)
    {
        if (currentScene == null) {
            currentScene = adventure.getStart();
        }

        adventureNameTF.setText(adventure.getName());

        assertionsTA.setText(adventure.getAssertions());

        scenesList.updateView();
        updateSceneView();

        sceneTabs.setSelectedIndex(0);

        playFromCurrentMnIt.setText("Play from '" + currentScene.getName() + "'");

        if (focusToSceneTextArea) {
            sceneTA.requestFocusInWindow();
        }
    }

    private void updateSceneView()
    {
        sceneTA.setText(currentScene.getText());
        codeTA.setText(currentScene.getCode() + "");
        imagePanelBuilder.update(currentScene);

        sceneNameTF.setText(currentScene.getName());
        tagsTF.setText(currentScene.getTags());

        pathsPane.removeAll();
        pathViews.clear();
        for (IPath p : currentScene.getPaths()) {
            createPathFields(p);
        }

        if (currentScene.hasScenesFrom()) {
            pathsBtsPane.add(backToFromScenesCb);
            backToFromScenesCb.setModel(new DefaultComboBoxModel(currentScene.getScenesFrom().toArray()));
            pathsBtsPane.add(goBackToSceneFromBt);
        } else {
            pathsBtsPane.remove(backToFromScenesCb);
            pathsBtsPane.remove(goBackToSceneFromBt);
        }

        sceneNameTF.setEnabled(!currentScene.equals(adventure.getStart()));
        sceneTypesWidgets.updateView(currentScene, adventure.getStart());

        pathsPane.setVisible(!currentScene.isEnd());

        newPathBt.setEnabled(currentScene.canHaveMorePaths());

        removeTab(TAB_TITLE_COMBAT);
        removeTab(TAB_TITLE_SKILL_TEST);

        if (currentScene.getType().equals(SceneType.combat)) {
            createCombatTab(currentScene);
        } else if (currentScene.getType().equals(SceneType.skillTest)) {
            createSkillTestTab(currentScene);
        }

        Util.showComponent(pathsBtsPane);
        Util.showComponent(pathsPane);
    }

    private void createSkillTestTab(Scene scene, String skillPhraseRawText)
    {
        // 222
        changeSceneTypeEvent(SceneType.skillTest);

        SkillTestPanelManager m = createSkillTestTab(scene);
        if (skillPhraseRawText.toLowerCase().indexOf("luck") != -1) {
            m.setSkill("Luck");
        }
    }

    private SkillTestPanelManager createSkillTestTab(Scene currentSceneAux)
    {
        JPanel p = new JPanel();
        p.add(new JLabel("Test your Skill"));

        if (sceneTabs.indexOfTab(TAB_TITLE_COMBAT) != -1) {
            sceneTabs.removeTabAt(1);
        }

        SkillTestPanelManager skillTestPanelManager = new SkillTestPanelManager(currentSceneAux);
        sceneTabs.insertTab(TAB_TITLE_SKILL_TEST, null/* icon */, skillTestPanelManager.getPanel(), null/* tip */, 1);
        sceneTabs.setSelectedIndex(1);

        return skillTestPanelManager;
    }

    private BuilderCombatPanelManager createCombatTab(Scene scene)
    {
        BuilderCombatPanelManager combatPanelManager = new BuilderCombatPanelManager(this, currentScene.getCombat());
        if (sceneTabs.indexOfTab(TAB_TITLE_COMBAT) != -1) {
            sceneTabs.removeTabAt(1);
        }
        sceneTabs.insertTab(TAB_TITLE_COMBAT, null/* icon */, combatPanelManager.getPanel(), null/* tip */, 1);
        sceneTabs.setSelectedIndex(1);

        return combatPanelManager;
    }

    private void createCombatTab(Scene scene, String rawTextWithEnemy)
    {
        changeSceneTypeEvent(SceneType.combat);

        BuilderCombatPanelManager combatPanelManager = createCombatTab(scene);
        Fighter fighter = new CombatEnemyRawTextParser().parse(rawTextWithEnemy);
        combatPanelManager.addNewFighterAction(fighter);
    }

    private void removeTab(String tabName)
    {
        int idx = sceneTabs.indexOfTab(tabName);
        if (idx != -1) {
            sceneTabs.removeTabAt(idx);
        }
    }

    private void save(Scene sceneToSave)
    {
        adventure.setName(adventureNameTF.getText());
        adventure.setAssertions(assertionsTA.getText());

        sceneToSave.setText(sceneTA.getText());
        sceneToSave.setCode(codeTA.getText());
        sceneToSave.setName(sceneNameTF.getText());
        sceneToSave.setTags(tagsTF.getText());

        for (PathView view : pathViews) {
            view.path.setText(view.textField.getText());
        }
    }

    private JPanel scenesPaneWidgets()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.setBorder(BorderFactory.createTitledBorder("Scene"));

        ScrollTextArea sceneSTA = BuilderSwingUtils.createTextAreaWidgets(this, getToolkit());
        sceneTA = sceneSTA.textArea;

        JPopupMenu popupMenu = sceneSTA.popup;
        final JMenuItem createPathMnIt = new JMenuItem("Create Path...");
        createPathMnIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                String t = createPathMnIt.getText().trim();
                String s = t.substring(t.indexOf('\'') + 1, t.lastIndexOf('\''));
                createPath(s);
            }
        });
        popupMenu.add(createPathMnIt);

        final JMenuItem splitSceneMnIt = new JMenuItem("Split Scene from here");
        splitSceneMnIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                splitSceneAction();
            }
        });
        popupMenu.add(splitSceneMnIt);

        final JMenuItem addCombatEnemiesMnIt = new JMenuItem("Add Combat Enemy");
        addCombatEnemiesMnIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                addCombatEnemySceneTextAreaContextMenuItemAction();
            }
        });
        popupMenu.add(addCombatEnemiesMnIt);

        final JMenuItem testYourSkillMnIt = new JMenuItem("Add 'Test Your Skill'");
        testYourSkillMnIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                addTestYourSkillMnItSceneTextAreaContextMenuItemAction();
            }
        });
        popupMenu.add(testYourSkillMnIt);

        final JMenuItem addCodeToAddItemMnIt = new JMenuItem("Add 'Item'");
        addCodeToAddItemMnIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                // 000
                addCodeToAddItemMnItSceneTextAreaContextMenuItemAction();
            }
        });
        popupMenu.add(addCodeToAddItemMnIt);

        sceneTA.addMouseListener(new SceneTextAreaPopupListener(popupMenu, sceneTA, createPathMnIt, splitSceneMnIt, addCombatEnemiesMnIt, testYourSkillMnIt, addCodeToAddItemMnIt));

        panel.add(headerFieldsPanelWidgets());

        sceneTabs = new JTabbedPane();
        sceneTabs.addTab("Text", Util.getImage("script.gif"), sceneSTA.scrollPane);

        // 666
        this.imagePanelBuilder = new ImagePanelBuilder(this, currentScene);
        sceneTabs.addTab("Image", imagePanelBuilder.getPanel());

        CodePanelBuilder panelBuilder = new CodePanelBuilder(this, getToolkit());
        codeTA = panelBuilder.getTextArea();
        sceneTabs.addTab("Actions", Util.getImage("code.gif"), panelBuilder.getPanel());

        panelBuilder = new CodePanelBuilder(this, getToolkit());
        assertionsTA = panelBuilder.getTextArea();
        sceneTabs.addTab("Assertions", Util.getImage("code_people.gif"), panelBuilder.getPanel());

        panel.add(sceneTabs);

        addHeight(panel, 4);

        mainPathsPane = createPathsPane();
        panel.add(mainPathsPane);
        return panel;
    }

    protected void addCodeToAddItemMnItSceneTextAreaContextMenuItemAction()
    {
        currentScene.setCode(currentScene.getCode() + "\n player.addAttribute(\"" + sceneTA.getSelectedText().trim() + "\");\n");
        codeTA.setText(currentScene.getCode());
    }

    protected void addTestYourSkillMnItSceneTextAreaContextMenuItemAction()
    {
        createSkillTestTab(currentScene, sceneTA.getSelectedText());
    }

    protected void addCombatEnemySceneTextAreaContextMenuItemAction()
    {
        createCombatTab(currentScene, sceneTA.getSelectedText());
    }

    protected void splitSceneAction()
    {
        String newText = sceneTA.getSelectedText();
        Scene newScene = adventure.split(currentScene, newText);
        sceneTA.setText(currentScene.getText()); // It'll be saved with the TA
        // text in the switchTo method
        // invocation bellow.
        switchTo(newScene);
    }

    public void showSceneCodeHelpDialog()
    {
        HelpDialog helpDialog = HelpDialog.createNew();

        final SceneCodeToPasteHolder codeHolder = new SceneCodeToPasteHolder();

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyMnIt = menuItem("Copy", "copy.gif");
        copyMnIt.addActionListener(new ActionListener() {
            SceneCodeToPasteHolder holder = codeHolder;

            public void actionPerformed(ActionEvent e)
            {
                getToolkit().getSystemClipboard().setContents(new StringSelection(holder.getText()), null);
            }
        });
        popupMenu.add(copyMnIt);

        final JMenuItem copyAndPasteMnIt = menuItem("Copy and Paste", "paste.gif");
        copyAndPasteMnIt.addActionListener(new ActionListener() {
            SceneCodeToPasteHolder holder = codeHolder;

            public void actionPerformed(ActionEvent e)
            {
                try {
                    codeTA.getDocument().insertString(codeTA.getCaretPosition(), holder.getText(), null);
                } catch (BadLocationException ble) {
                    throw new AdventureException(ble.getMessage(), ble);
                }
            }
        });
        popupMenu.add(copyAndPasteMnIt);

        helpDialog.getEditorPane().addMouseListener(new TextComponentForPasteMouseListener(popupMenu, copyAndPasteMnIt, helpDialog.getEditorPane(), codeTA, codeHolder));
    }

    private JPanel headerFieldsPanelWidgets()
    {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel titlePn = sceneTitlePaneWidgets();
        pane.add(titlePn);

        JPanel tagsPn = sceneTagsPaneWidgets(pane);
        pane.add(tagsPn);

        sceneHeaderFieldsEvents();

        return pane;
    }

    private JPanel sceneTitlePaneWidgets()
    {
        JPanel titlePn = new JPanel();
        titlePn.setLayout(new BoxLayout(titlePn, BoxLayout.LINE_AXIS));
        titlePn.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JLabel titleLb = new JLabel("Title: ");
        titleLb.setMaximumSize(new Dimension(36, 20));
        titlePn.add(titleLb);

        sceneNameTF = new JTextField(/* 38 */);
        sceneNameTF.setMinimumSize(new Dimension(200, 20));
        sceneNameTF.setMaximumSize(new Dimension(800, 20));

        titlePn.add(sceneNameTF);

        return titlePn;
    }

    private JPanel sceneTagsPaneWidgets(JPanel pane)
    {
        JPanel tagsPn = new JPanel();
        tagsPn.setLayout(new BoxLayout(tagsPn, BoxLayout.LINE_AXIS));
        tagsPn.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JLabel tagsLb = new JLabel("Tags: ");
        tagsLb.setMaximumSize(new Dimension(36, 20));
        tagsPn.add(tagsLb);

        tagsTF = new JTextField(/* 33 */);
        // tagsTF.setMinimumSize(new Dimension(200, 20));
        // tagsTF.setMaximumSize(new Dimension(800, 20));
        tagsPn.add(tagsTF);

        Util.addWidth(tagsPn, 10);

        this.sceneTypesWidgets = new SceneTypesWidgets(tagsPn, this);

        return tagsPn;
    }

    private void sceneHeaderFieldsEvents()
    {
        sceneNameTF.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    save(currentScene);
                    updateView();
                    tagsTF.requestFocusInWindow();
                }
            }
        });

        BuilderSwingUtils.addTextEventHandlers(sceneNameTF, this);
        BuilderSwingUtils.addTextEventHandlers(tagsTF, this);
    }

    private JPanel createPathsPane()
    {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        pathsPane = new JPanel(new GridBagLayout());
        pane.add(pathsPane);

        newPathBt = new JButton("New Path");
        newPathBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                createPath();
            }
        });
        newPathBt.setMnemonic('P');

        backToFromScenesCb = new JComboBox();
        backToFromScenesCb.setToolTipText("This scene can be achieved from this ones");
        backToFromScenesCb.setRenderer(new BackToFromScenesCbCellRender());

        goBackToSceneFromBt = new JButton("Back", Util.getImage("backward_nav_003.gif"));
        goBackToSceneFromBt.setMnemonic('b');
        goBackToSceneFromBt.setToolTipText("Click to navigate to the selected scene");
        goBackToSceneFromBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                switchTo((Scene) backToFromScenesCb.getSelectedItem());
            }
        });

        pathsBtsPane = new JPanel();
        pathsBtsPane.add(newPathBt);
        pathsBtsPane.add(new JSeparator());
        pane.add(pathsBtsPane);

        return pane;
    }

    private void createPath()
    {
        createPath(randomPathNameGenerator.getSuggestion());
        logger.debug(currentScene.getPaths().size());

        newPathBt.setEnabled(currentScene.canHaveMorePaths());
    }

    private void createPath(String text)
    {
        IPath p = currentScene.createPath(text);
        createPathFields(p);
        markAsDirty();
        Util.showComponent(pathsPane);
    }

    private void createPathFields(final IPath path)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel orderLb = new JLabel("[" + (path.getOrder() + 1) + "]");
        c.weightx = 0.02;
        c.gridx = 0;
        c.gridy = pathViews.size();
        c.ipady = 5;
        orderLb.setFont(FONT);
        pathsPane.add(orderLb, c);

        JTextField pathText = new JTextField(path.getText());
        pathText.setMaximumSize(new Dimension(500, 20));
        c.weightx = 0.58;
        c.gridx = 1;
        c.gridy = pathViews.size();
        c.ipady = 5;
        pathText.setFont(FONT);
        BuilderSwingUtils.addTextEventHandlers(pathText, this);
        pathsPane.add(pathText, c);

        JButton sceneBt = null;
        if (path.getTo() == null) {
            sceneBt = new JButton("Create Scene");
        } else {
            sceneBt = new JButton("Go [" + path.getTo().getName() + "]");
        }

        c.weightx = 0;
        c.gridx = 2;
        c.gridy = pathViews.size();
        c.ipady = 0;
        pathsPane.add(sceneBt, c);

        JButton chooseSceneBt = new JButton(Util.getImage("search.gif"));
        c.weightx = 0;
        c.gridx = 3;
        c.gridy = pathViews.size();
        c.ipady = 0;
        pathsPane.add(chooseSceneBt, c);

        JButton deleteSceneBt = new JButton(Util.getImage("remove.gif"));
        c.weightx = 0;
        c.gridx = 4;
        c.gridy = pathViews.size();
        c.ipady = 0;
        pathsPane.add(deleteSceneBt, c);

        final PathView pathView = new PathView(orderLb, sceneBt, chooseSceneBt, deleteSceneBt, pathText, path);
        pathViews.add(pathView);

        deleteSceneBt.addActionListener(new ActionListener() {
            PathView _pathView = pathView;

            public void actionPerformed(ActionEvent e)
            {
                removePath(_pathView);
            }
        });

        if (path.getTo() == null) {
            sceneBt.addActionListener(new ActionListener() {
                PathView _pathView = pathView;

                public void actionPerformed(ActionEvent e)
                {
                    createSceneFromPath(_pathView);
                }
            });
        } else {
            sceneBt.addActionListener(new ActionListener() {
                PathView _pathView = pathView;

                public void actionPerformed(ActionEvent e)
                {
                    switchTo(_pathView.path.getTo());
                }
            });
        }

        chooseSceneBt.addActionListener(new ActionListener() {
            IPath _path = path;

            public void actionPerformed(ActionEvent e)
            {
                chooseSceneBtAction(_path);
            }
        });

        Util.showComponent(pathsPane);
        pathText.requestFocusInWindow();
    }

    public void saveActualScene()
    {
        save(currentScene);
    }

    private void chooseSceneBtAction(IPath path)
    {
        ChooseSceneDialog dialog = new ChooseSceneDialog(Builder.this);
        if (dialog.getScene() != null) {
            path.setTo(dialog.getScene());
            updateSceneView();
            markAsDirty();
        }
    }

    private void createSceneFromPath(PathView view)
    {
        switchTo(adventure.createSceneFrom(view.path));
    }

    private void removePath(PathView pathView)
    {
        pathsPane.remove(pathView.orderLb);
        pathsPane.remove(pathView.textField);
        pathsPane.remove(pathView.createSceneBt);
        pathsPane.remove(pathView.deleteSceneBt);
        pathsPane.remove(pathView.chooseSceneBt);

        pathView.path.getFrom().remove(pathView.path);
        Util.showComponent(pathsPane);

        newPathBt.setEnabled(currentScene.canHaveMorePaths());

        markAsDirty();
    }

    static class PathView
    {
        JLabel orderLb;

        JTextField textField;

        JButton createSceneBt;

        JButton chooseSceneBt;

        JButton deleteSceneBt;

        IPath path;

        public PathView(JLabel orderLb, JButton createBt, JButton deleteBt, JButton chooseSceneBt, JTextField textField, IPath path)
        {
            this.orderLb = orderLb;
            this.createSceneBt = createBt;
            this.chooseSceneBt = chooseSceneBt;
            this.deleteSceneBt = deleteBt;
            this.textField = textField;
            this.path = path;
        }
    }

    private void menu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu adventureMenu = Util.menu("Adventure", 'A', menuBar);

        playMnIt = Util.menuItem("Create a new Adventure", 'n', KeyEvent.VK_N, "control_play_blue.png", adventureMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                createNewAdventure();
            }
        });

        JMenu testMenu = Util.menu("Test", 't', menuBar);

        playMnIt = Util.menuItem("Play", 'p', KeyEvent.VK_P, "control_play_blue.png", testMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                playAdventureAction();
            }
        });
        playMnIt.setEnabled(false);

        playFromCurrentMnIt = Util.menuItem("Play from current scene", 'r', KeyEvent.VK_R, "control_fastforward_blue.png", testMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                playAdventureFromActualSceneMenuAction();
            }
        });
        playFromCurrentMnIt.setEnabled(false);

        adventureMenu.add(new JSeparator());

        saveMnIt = Util.menuItem("Save", 'S', KeyEvent.VK_S, "disk.png", adventureMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                saveAdventureMenuAction(false);
            }
        });

        JMenuItem exportMnIt = Util.menuItem("Export", 'x', KeyEvent.VK_X, "export_wiz.gif", adventureMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                exportWorkspaceAction();
            }
        });

        JMenuItem importMnIt = Util.menuItem("Import", 'i', KeyEvent.VK_I, "import_wiz.gif", adventureMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                importWorkspaceAction();
            }
        });

        saveAsMnIt = Util.menuItem("Save As...", 'a', KeyEvent.VK_A, "textfield_rename.png", adventureMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                saveAdventureMenuAction(true);
            }
        });

        adventureMenu.add(new JSeparator());

        Util.menuItem("Open", 'a', KeyEvent.VK_O, "folder_table.png", adventureMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                openAdventureMenuAction();
            }
        });

        adventureMenu.add(recentMenuController.getOpenRecentMenu());
        adventureMenu.add(new JSeparator());

        Util.menuItem("Quit", 'q', KeyEvent.VK_Q, "cancel.png", adventureMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                exitMenuItemAction();
            }
        });

        JMenu viewMenu = Util.menu("View", 'v', menuBar);

        byDepthMnIt = Util.menuItem("By Depth", 'd', KeyEvent.VK_D, "tree.gif", viewMenu, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                byDepthMenuAction();
            }
        });
        byDepthMnIt.setEnabled(false);

        SettingsUtil.addSettingsMenu(menuBar, Conf.builder());
        Util.addHelpMenu(menuBar, this);

        setJMenuBar(menuBar);
    }

    private void exitMenuItemAction()
    {
        exitApplication();
    }

    private void exitApplication()
    {
        if (isDirty) {
            int answer = Util.showSaveDialog(this, "Do you want to save it before going to your normal life?");
            if (answer == Util.SAVE_DIALOG_OPT_CANCEL)
                return;
            else if (answer == Util.SAVE_DIALOG_OPT_SAVE)
                saveAdventureMenuAction(false);
        }

        Util.exitApplication(this, Conf.builder());
    }

    private void openAdventureMenuAction()
    {
        if (isDirty) {
            int answer = Util.showSaveDialog(this, "Do you want to save it before openning another adventure?");
            if (answer == Util.SAVE_DIALOG_OPT_CANCEL)
                return;
            else if (answer == Util.SAVE_DIALOG_OPT_SAVE)
                saveAdventureMenuAction(false);
        }

        OpenWorkspaceDialog dialog = new OpenWorkspaceDialog(this);
        Workspace chosenWorkspace = dialog.getWorkspace();
        if (chosenWorkspace != null) {
            open(chosenWorkspace);
        }
    }

    private void byDepthMenuAction()
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                try {
                    DepthScenesFrame depthScenesFrame = new DepthScenesFrame(Builder.this, adventure, currentScene);
                    openAdventureListeners.add(depthScenesFrame.getViewController());
                } catch (AdventureReaderException e) {
                    logger.error("Error", e);
                }
            }
        });
    }

    private void playAdventureAction()
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                try {
                    if (isDirty) {
                        save(false);
                    }
                    Runner.runAdventure(workspace);
                } catch (AdventureReaderException e) {
                    logger.error("Error", e);
                }
            }
        });
    }

    private void playAdventureFromActualSceneMenuAction()
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                try {
                    new Runner(adventure, currentScene);
                } catch (AdventureReaderException e) {
                    logger.error("Error", e);
                }
            }
        });
    }

    public void openEntityPersisted(String workspaceId)
    {
        open(Workspace.loadFrom(workspaceId));
    }

    public void open(Workspace workspace)
    {
        this.workspace = workspace;

        DepthManager.getInstance().reset();

        adventure = workspace.getAdventure();

        fireOpenAdventureEvent(workspace);
        // saveFile = advFile; TODO Should save workspace reference here?

        enableOpenAdventureMenuItems();

        initViewWithNewAdventureObj();

        updateTitle();

        markAsClean();

        updateConfWithLastAdventureFile();
    }

    private void fireOpenAdventureEvent(Workspace workspace)
    {
        for (EntityPersistedOnFileOpenActionListener listener : openAdventureListeners) {
            listener.fireEntityOpenedAction(workspace);
        }
    }

    private void saveAdventureMenuAction(boolean isSaveAs)
    {
        save(isSaveAs);
    }

    public void save(boolean isSaveAs)
    {
        logger.debug("Saving..."); // 111

        save(currentScene);

        workspace.save();

        updateConfWithLastAdventureFile();

        updateTitle();

        markAsClean();
    }

    private void updateConfWithLastAdventureFile()
    {
        Conf.builder().set("lastWorkspaceId", workspace.getId());
        Conf.builder().save();
    }

    private void createNewAdventure()
    {
        if (adventure != null) {
            int answer = JOptionPane.showConfirmDialog(Builder.this, "Close current adventure?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (answer == JOptionPane.NO_OPTION)
                return;
        }

        adventure = new Adventure();
        adventure.setName("Your new Adventure name");

        workspace = Workspace.createWith(adventure);
        updateTitle();

        enableOpenAdventureMenuItems();

        initViewWithNewAdventureObj();
    }

    private void enableOpenAdventureMenuItems()
    {
        saveMnIt.setEnabled(true);
        saveAsMnIt.setEnabled(true);
        playMnIt.setEnabled(true);
        playBt.setEnabled(true);
        playFromCurrentBt.setEnabled(true);
        playFromCurrentMnIt.setEnabled(true);
        byDepthMnIt.setEnabled(true);
    }

    private void initViewWithNewAdventureObj()
    {
        mainPanel.setVisible(true);

        try {
            String lastViewedSceneId = Conf.builder().get("lastViewedSceneId");
            if (lastViewedSceneId == null) {
                goTo(adventure.getStart());
            } else {
                Scene scene = adventure.getScene(Integer.parseInt(lastViewedSceneId));
                if (scene != null) {
                    goTo(scene);
                } else {
                    goTo(adventure.getStart());
                }
            }
        } catch (AdventureException e) {
            logger.warn("Tried to open last viewed scene", e);
            goTo(adventure.getStart());
        }

        /**
         * Not called inside the method updateView since it needs only to be invoked once.
         */
        scenesList.prepareView(adventure.getStart());
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                new Builder();
            }
        });
    }

    public boolean hasAnOpenEntity()
    {
        return adventure != null;
    }

    public boolean isDirty()
    {
        return isDirty;
    }

    public Collection<Scene> getScenes()
    {
        return adventure.getScenes();
    }

    /**
     * Devemos guardar a [lastSceneListIndex] para mais tarde re-selecionar na jlist de cenas pois o
     * updateView refaz o modelo da jlist.
     */
    public void switchTo(Scene selectedScene, int selectedSceneIdx)
    {
        setLastSceneListIndex(selectedSceneIdx);
        switchTo(selectedScene);
    }

    public void changeSceneTypeEvent(SceneType type) // 333
    {
        boolean changeType = false;
        logger.debug("type=" + type);
        logger.debug("currentScene.getPathsSize()=" + currentScene.getPathsSize());
        logger.debug("type.exactPathsNumberPermited()=" + type.exactPathsNumberPermited());
        if (type.hasPathsNumberRestrictions() && currentScene.getPathsSize() > type.exactPathsNumberPermited()) {
            String msg = null;
            String secMsg = null;

            if (type.exactPathsNumberPermited() == 0) {
                msg = "A " + type + " Scene cannot have any paths.\n" + "If you confirm all current paths will be deleted\n" + "Are you sure?";
                secMsg = "This action will delete this scene paths";
            } else {
                msg = "A " + type + " Scene cannot have any paths.\n" + "If you confirm all current paths but " + type.exactPathsNumberPermited() + " " + "will be deleted\n" + "Are you sure?";
                secMsg = "Only " + type.exactPathsNumberPermited() + " paths will remain!";
            }
            int answer = JOptionPane.showConfirmDialog(Builder.this, msg, secMsg, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (answer == JOptionPane.YES_OPTION) {
                changeType = true;
            }
        } else {
            changeType = true;
        }
        if (changeType) {
            currentScene.setType(type);
        }
        updateView();
        markAsDirty();
    }

    @Override
    public void fireNewImageSelectedAction(String imageId)
    {
        currentScene.setImageId(imageId);
        markAsDirty();
    }

    @Override
    public Workspace getWorkspace()
    {
        return workspace;
    }

    private void exportWorkspaceAction()
    {
        WorkspaceExporter.export(getWorkspace());
    }

    private void importWorkspaceAction()
    {
        Workspace importedWorkspace = WorkspaceExporter.importWorkspace(this.mainPanel);
        if (isDirty) {
            int answer = Util.showSaveDialog(this, "Do you want to save it before openning another adventure?");
            if (answer == Util.SAVE_DIALOG_OPT_CANCEL)
                return;
            else if (answer == Util.SAVE_DIALOG_OPT_SAVE)
                saveAdventureMenuAction(false);
        }
        open(importedWorkspace);
    }
}