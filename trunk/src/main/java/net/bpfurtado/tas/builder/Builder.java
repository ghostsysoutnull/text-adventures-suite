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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.text.JTextComponent;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.AdventureOpenner;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.builder.combat.BuilderCombatPanelManager;
import net.bpfurtado.tas.builder.depth.DepthScenesFrame;
import net.bpfurtado.tas.builder.scenespanel.ChooseSceneDialog;
import net.bpfurtado.tas.builder.scenespanel.ScenesList;
import net.bpfurtado.tas.builder.scenespanel.ScenesListControllerFactory;
import net.bpfurtado.tas.builder.scenespanel.ScenesSource;
import net.bpfurtado.tas.builder.scenespanel.ScenesListControllerFactory.ScenesListControllerResult;
import net.bpfurtado.tas.builder.scenetype.SceneTypesWidgets;
import net.bpfurtado.tas.builder.skilltest.SkillTestPanelManager;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.DepthManager;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.SceneType;
import net.bpfurtado.tas.model.persistence.AdventureReaderException;
import net.bpfurtado.tas.model.persistence.XMLAdventureReader;
import net.bpfurtado.tas.model.persistence.XMLAdventureWriter;
import net.bpfurtado.tas.runner.Runner;
import net.bpfurtado.tas.view.HelpDialog;
import net.bpfurtado.tas.view.RecentAdventuresMenuController;
import net.bpfurtado.tas.view.SettingsUtil;
import net.bpfurtado.tas.view.TextComponentForPasteMouseListener;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class Builder extends JFrame implements AdventureOpenner, ScenesSource, AdventureNeedsSavingController
{
    private static final String CONF_OPEN_LAST_ADVENTURE_ON_START = "openLastAdventureOnStart";
	private static final ImageIcon PLAY_IMAGE = Util.getImage("control_play_blue.png");
    private static final ImageIcon PLAY_CURRENT_IMAGE = Util.getImage("control_fastforward_blue.png");

    private static final Logger logger = Logger.getLogger(Builder.class);

    private static final long serialVersionUID = -3424967248011145801L;

    public static final Font FONT = new java.awt.Font("Tahoma", 0, 14);

    private static final String TAB_TITLE_COMBAT = "Combat";
    private static final String TAB_TITLE_SKILL_TEST = "Skill Test";

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
    private JComboBox backToFromScenesCb;

    private JMenuItem saveMnIt;
    private JMenuItem saveAsMnIt;
    private JMenuItem playMnIt;
    private JMenuItem playFromCurrentMnIt;
    private JMenuItem byDepthMnIt;

    private int lastSceneListIndex = -1;

    private RandomPathNameGenerator randomPathNameGenerator = new RandomPathNameGenerator();

    private List<PathView> pathViews = new LinkedList<PathView>();
    private List<OpenAdventureListener> openAdventureListeners;
    private ScenesList scenesList = null;

    private RecentAdventuresMenuController recentMenuController;

    private File saveFile;
    boolean isDirty = false;
    private SceneTypesWidgets sceneTypesWidgets;

    private JButton playFromCurrentBt;

    public Builder()
    {
        openAdventureListeners = new LinkedList<OpenAdventureListener>();
        recentMenuController = new RecentAdventuresMenuController(this, this);
        openAdventureListeners.add(recentMenuController);

        initView();
        markAsClean();

        openLastAdventure();
    }

    private void openLastAdventure()
	{
		boolean openLastAdventureOnStart = true;
		try {
			openLastAdventureOnStart = Conf.builder().is(CONF_OPEN_LAST_ADVENTURE_ON_START, false);
			if (!openLastAdventureOnStart) {
				return;
			}
		} catch (AdventureException e) {
			Conf.builder().set(CONF_OPEN_LAST_ADVENTURE_ON_START, openLastAdventureOnStart);
		}

        File advFile = new File(Conf.builder().get("lastAdventure"));
        if (advFile.exists()) {
            openAdventure(advFile);
        }
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
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (Exception e) {
//            throw new AdventureException(e);
//        }
        
        menu();
        widgets();

        Conf conf = Conf.builder();
        int x = conf.getInt("bounds.x", 235);
        int y = conf.getInt("bounds.y", 260);
        int w = conf.getInt("bounds.w", 970);
        int h = conf.getInt("bounds.h", 615);

        setBounds(x, y, w, h); // setBounds(235, 260, 806, 430);
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
        if (saveFile != null) {
            title += " [" + saveFile.getName() + "] ";
        }
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

        addTextEventHandlers(adventureNameTF);
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

//        centerPn.add(Box.createRigidArea(new Dimension(3, 0)));
        centerPn.add(Box.createHorizontalGlue());

        this.playFromCurrentBt = new JButton(PLAY_CURRENT_IMAGE);
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

        playBt = new JButton(PLAY_IMAGE);
        playBt.setToolTipText("Saves the adventure and plays it from the Start Scene");
        playBt.setMnemonic('P');
        playBt.setEnabled(false);
        playBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                playAdventureAction();
            }
        });
//        centerPn.add(Box.createHorizontalGlue());
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

    public Scene getActualScene()
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
         * Se for o inicio de uma nova aventura então nao temos ainda uma actualScene então não
         * devemos salvar. Se setarmos a scene start para a actual agora a mesma será salva com os
         * dados da gui vazios pois o updateView ainda não foi feito.
         */
        if (currentScene != null) {
            save(currentScene);
        }
        goTo(sceneToSwitch);
    }

    public void goTo(Scene sceneToGo)
    {
        currentScene = sceneToGo;
        updateView();
    }

    private void updateView()
    {
        updateView(true);
    }

    private void updateView(boolean focusToSceneName)
    {
        logger.debug("here");
        if (currentScene == null) {
            currentScene = adventure.getStart();
        }

        adventureNameTF.setText(adventure.getName());

        assertionsTA.setText(adventure.getAssertions());

        scenesList.updateView();
        updateSceneView();

        playFromCurrentMnIt.setText("Play from '" + currentScene.getName() + "'");

        if (focusToSceneName) {
            sceneNameTF.requestFocusInWindow();
        }
    }

    private void updateSceneView()
    {
        sceneTA.setText(currentScene.getText());
        codeTA.setText(currentScene.getCode() + "");

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
        newPathBt.setVisible(currentScene.canHaveMorePaths());

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

    private void createSkillTestTab(Scene actualScene2)
    {
        JPanel p = new JPanel();
        p.add(new JLabel("Test your Skill"));

        if (sceneTabs.indexOfTab(TAB_TITLE_COMBAT) != -1) {
            sceneTabs.removeTabAt(1);
        }
        sceneTabs.insertTab(TAB_TITLE_SKILL_TEST, null/* icon */, new SkillTestPanelManager(currentScene).getPanel(), null/* tip */, 1);
        sceneTabs.setSelectedIndex(1);
    }

    private void createCombatTab(Scene scene)
    {
        BuilderCombatPanelManager combatPanelManager = new BuilderCombatPanelManager(this, currentScene.getCombat());
        if (sceneTabs.indexOfTab(TAB_TITLE_COMBAT) != -1) {
            sceneTabs.removeTabAt(1);
        }
        sceneTabs.insertTab(TAB_TITLE_COMBAT, null/* icon */, combatPanelManager.getPanel(), null/* tip */, 1);
        sceneTabs.setSelectedIndex(1);
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

        ScrollTextArea sceneSTA = createTextAreaWidgets();
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
        
        sceneTA.addMouseListener(new SceneTextAreaPopupListener(popupMenu, createPathMnIt, splitSceneMnIt, sceneTA));

        panel.add(headerFieldsPanelWidgets());

        sceneTabs = new JTabbedPane();
        sceneTabs.addTab("Text", Util.getImage("script.gif"), sceneSTA.scrollPane);

        JPanel codePn = new JPanel();
        codeTA = createCodePanel(codePn);
        sceneTabs.addTab("Actions", Util.getImage("code.gif"), codePn);

        JPanel assertionsPn = new JPanel();
        assertionsTA = createCodePanel(assertionsPn);
        sceneTabs.addTab("Assertions", Util.getImage("code_people.gif"), assertionsPn);
        panel.add(sceneTabs);

        panel.add(Box.createRigidArea(new Dimension(0, 4)));

        mainPathsPane = createPathsPane();
        panel.add(mainPathsPane);
        return panel;
    }

    ///111
    protected void splitSceneAction()
    {
        String newText = sceneTA.getSelectedText();
        Scene newScene = adventure.split(currentScene, newText);
        sceneTA.setText(currentScene.getText()); //It'll be saved with the TA text in the switchTo method invocation bellow. 
        switchTo(newScene);
    }

    private ScrollTextArea createTextAreaWidgets()
    {
        ScrollTextArea sta = TextAreaWidgetFactory.create(FONT, getToolkit());
        addTextEventHandlers(sta.textArea);
        return sta;
    }

    private JTextArea createCodePanel(JPanel codePn)
    {
        ScrollTextArea codeSTA = createTextAreaWidgets();
        JTextArea codeTA = codeSTA.textArea;
        codeTA.setFont(new java.awt.Font("Courier New", 0, 14));

        codePn.setLayout(new BoxLayout(codePn, BoxLayout.PAGE_AXIS));
        codePn.add(codeSTA.scrollPane);

        JPanel buttonsPn = new JPanel();
        buttonsPn.setBackground(Color.blue);
        buttonsPn.setLayout(new BoxLayout(buttonsPn, BoxLayout.LINE_AXIS));

        JButton codeHelpBt = new JButton("Help and Code snippets");
        codeHelpBt.setMnemonic('H');
        codeHelpBt.setAlignmentX(RIGHT_ALIGNMENT);
        codeHelpBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                showSceneCodeHelpDialog();
            }
        });
        buttonsPn.add(codeHelpBt);

        codePn.add(Box.createRigidArea(new Dimension(0, 5)));
        codePn.add(buttonsPn);
        codePn.add(Box.createRigidArea(new Dimension(0, 5)));
        return codeTA;
    }

    private void showSceneCodeHelpDialog()
    {
        HelpDialog helpDialog = new HelpDialog("Scene Code programming", "/net/bpfurtado/tas/builder/codeHelp.html");

        final SceneCodeToPasteHolder codeHolder = new SceneCodeToPasteHolder();

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyMnIt = new JMenuItem("Copy", Util.getImage("copy_edit_003.gif"));
        copyMnIt.addActionListener(new ActionListener() {
            SceneCodeToPasteHolder holder = codeHolder;

            public void actionPerformed(ActionEvent e)
            {
                getToolkit().getSystemClipboard().setContents(new StringSelection(holder.getText()), null);
            }
        });
        popupMenu.add(copyMnIt);

        final JMenuItem copyAndPasteMnIt = new JMenuItem("Copy and Paste", Util.getImage("paste_edit_003.gif"));
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
        //111
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
        //111
//        tagsTF.setMinimumSize(new Dimension(200, 20));
//        tagsTF.setMaximumSize(new Dimension(800, 20));
        tagsPn.add(tagsTF);

        tagsPn.add(Box.createRigidArea(new Dimension(10, 0)));

        this.sceneTypesWidgets = new SceneTypesWidgets(tagsPn, this); // 222

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

        addTextEventHandlers(sceneNameTF);
        addTextEventHandlers(tagsTF);
    }

    private void addTextEventHandlers(JTextComponent textComponent)
    {
        textComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() != 19) {
                    markAsDirty();
                }
            }
        });
        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveActualScene();
            }
        });
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
        addTextEventHandlers(pathText);
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

    protected void saveActualScene()
    {
        save(currentScene);
    }

    private void chooseSceneBtAction(IPath path)
    {
        ChooseSceneDialog dialog = new ChooseSceneDialog(Builder.this, adventure);
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

        Conf conf = Conf.builder();
        conf.set("bounds.x", getX());
        conf.set("bounds.y", getY());
        conf.set("bounds.w", getWidth());
        conf.set("bounds.h", getHeight());
        conf.save();

        dispose();

        Util.terminateProcessIfAlone();
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

        JFileChooser fileChooser = createFileChooserWithDirFromConfigItem("lastOpenDir");
        if (fileChooser.showOpenDialog(Builder.this) == JFileChooser.APPROVE_OPTION) {
            saveFile = fileChooser.getSelectedFile();
            // saveConfigItem("lastOpenDir"); //111 TODO Remove me

            openAdventure(saveFile);
        }
    }

    private JFileChooser createFileChooserWithDirFromConfigItem(String key)
    {
        JFileChooser fileChooser = Util.createFileChooser(Conf.builder().get(key, System.getProperty("user.home")));
        return fileChooser;
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
                        saveAdventure(false);
                    }
                    Runner.runAdventure(saveFile);
                    // new Runner(adventure); TODO Remove this
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

    public void openAdventure(File advFile)
    {
        DepthManager.getInstance().reset();

        adventure = new XMLAdventureReader().read(advFile);

        fireOpenAdventureEvent(advFile);
        saveFile = advFile;

        enableOpenAdventureMenuItems();

        initViewWithNewAdventureObj();

        updateTitle();

        markAsClean();

        updateConfWithLastAdventureFile(saveFile);
    }

    private void fireOpenAdventureEvent(File adventureFile)
    {
        for (OpenAdventureListener listener : openAdventureListeners) {
            listener.adventureOpenned(adventureFile);
        }
    }

    private void saveAdventureMenuAction(boolean isSaveAs)
    {
        saveAdventure(isSaveAs);
    }

    public void saveAdventure(boolean isSaveAs)
    {
        logger.debug("Saving...");
        boolean wasNewSaveFile = false;
        if (saveFile == null || isSaveAs) {
            JFileChooser fileChooser = createFileChooserWithDirFromConfigItem("lastSaveDir");
            if (fileChooser.showSaveDialog(Builder.this) == JFileChooser.APPROVE_OPTION) {
                saveFile = fileChooser.getSelectedFile();
                if (saveFile.exists()) {
                    int answer = JOptionPane.showConfirmDialog(Builder.this, "This file already exists!\n" + "Do you want to overwrite it?", "File already exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                wasNewSaveFile = true;
            }
        }
        save(currentScene);
        saveFile = new XMLAdventureWriter(adventure, saveFile).write();

        updateConfWithLastAdventureFile(saveFile);

        updateTitle();
        if (wasNewSaveFile) {
            fireOpenAdventureEvent(saveFile);
        }

        markAsClean();
    }

    private void updateConfWithLastAdventureFile(File advFile)
    {
        Conf.builder().set("lastSaveDir", advFile.getParentFile().getAbsolutePath());
        Conf.builder().set("lastAdventure", saveFile.getAbsolutePath());
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

        saveFile = null;
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
        goTo(adventure.getStart());

        /** Não está dentro do método updateView pois só precisa ser invocado uma vez */
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

    public boolean hasAnOpenAdventure()
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
}