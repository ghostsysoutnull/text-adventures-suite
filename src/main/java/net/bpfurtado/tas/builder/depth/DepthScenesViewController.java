/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 12/10/2005 11:59:14                                                          
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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.bpfurtado.tas.builder.EntityPersistedOnFileOpenAction;
import net.bpfurtado.tas.builder.EntityPersistedOnFileOpenActionListener;
import net.bpfurtado.tas.model.DepthManager;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class DepthScenesViewController implements EntityPersistedOnFileOpenActionListener
{
    private static final Logger logger = Logger.getLogger(DepthScenesViewController.class);

    private JPanel panel;
    private JPanel listsPanel;
    private JButton prev = new JButton("Previous", Util.getImage("nav_backward_002.gif"));
    private JButton next = new JButton("Next", Util.getImage("nav_forward_002.gif"));
    private JButton jumpToBt = new JButton("Jump to", Util.getImage("jump.gif"));

    private LinkedList<ListLevel> depthLists = new LinkedList<ListLevel>();

    private Scene selectedScene;
    private DepthScenesFrame scenesDepthFrame;

    private JScrollPane scrollPane;

    static String renderToList(Scene scene)
    {
        boolean hasNoTos = true;
        for (IPath p : scene.getPaths()) {
            if (p.getTo() != null) {
                hasNoTos = false;
                break;
            }
        }
        String sufix = scene.getPaths().isEmpty() ? "] " : hasNoTos ? "#" : " ";
        return (scene.getScenesFrom().isEmpty() ? " [" : " ") + scene.getName() + sufix;
    }

    public DepthScenesViewController(DepthScenesFrame scenesDepthFrame, Scene actualScene)
    {
        this.scenesDepthFrame = scenesDepthFrame;

        panel = new JPanel(new BorderLayout());
        mountButtonsPanel();
        mountDepthListsPanel(actualScene);
    }

    private void mountDepthListsPanel(Scene currentScene)
    {
        listsPanel = new JPanel();
        listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.LINE_AXIS));

        this.scrollPane = new JScrollPane(listsPanel);
        panel.add(scrollPane);

        mountDepthLists(currentScene);
        checkCanStillGoPrev();
        checkStillGoNext();
    }

    private void mountDepthLists(Scene currentScene)
    {
        int numLevels = DepthManager.getInstance().getNumberOfDepths();

        int idx = DepthManager.getInstance().getFirstDepthOfScene(currentScene);
        idx = idx == -1 ? 0 : idx;

        int first = idx - 3 < 0 ? 0 : idx - 3;
        int last = idx + 3 > numLevels - 1 ? numLevels - 1 : idx + 3;
        logger.debug("currentScene=" + currentScene + " ,first=" + first + ", last=" + last);

        for (int i = first; i <= last; i++) {
            mountListToEnd(i, i == idx, currentScene);
        }
    }

    private void checkCanStillGoPrev()
    {
        if (depthLists.isEmpty()) {
            prev.setEnabled(false);
            return;
        }
        ListLevel listLevel = depthLists.getFirst();
        if (listLevel.level == 0) {
            prev.setEnabled(false);
        }
    }

    private void checkStillGoNext()
    {
        if (depthLists.isEmpty()) {
            next.setEnabled(false);
            return;
        }
        if (depthLists.getLast().level + 1 == DepthManager.getInstance().getNumberOfDepths()) {
            next.setEnabled(false);
        }
    }

    private void mountButtonsPanel()
    {
        next.setVerticalTextPosition(AbstractButton.CENTER);
        next.setHorizontalTextPosition(AbstractButton.LEADING);
        next.setMnemonic('n');

        prev.setMnemonic('p');

        jumpToBt.setMnemonic('j');
        jumpToBt.setVerticalTextPosition(AbstractButton.CENTER);
        jumpToBt.setHorizontalTextPosition(AbstractButton.LEADING);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        buttonsPanel.add(prev);
        prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                prevButtonAction();
            }
        });
        Dimension space = new Dimension(5, 0);
        buttonsPanel.add(Box.createRigidArea(space));

        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                nextButtonAction();
            }
        });
        buttonsPanel.add(next);
        buttonsPanel.add(Box.createRigidArea(space));

        jumpToBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                jumpToButtonAction();
            }
        });

        buttonsPanel.add(jumpToBt);
        jumpToBt.setEnabled(false);

        panel.add(buttonsPanel, BorderLayout.PAGE_START);
    }

    protected void jumpToButtonAction()
    {
        scenesDepthFrame.dispose();
        scenesDepthFrame.getBuilder().goTo(selectedScene);
    }

    private void nextButtonAction()
    {
        mountListToEnd(depthLists.getLast().level + 1);
        checkStillGoNext();
        navigationButtonScreenAdjustments(ScrollTo.last);
    }

    private void prevButtonAction()
    {
        int level = depthLists.getFirst().level - 1;
        mountListToStart(level);
        checkCanStillGoPrev();
        navigationButtonScreenAdjustments(ScrollTo.first);
    }

    private void mountListToEnd(int i)
    {
        mountListToEnd(i, false, null);
    }

    private enum ScrollTo {
        last {
            JList getList(LinkedList<ListLevel> depthLists)
            {
                return depthLists.getLast().list;
            }
        },
        first {
            JList getList(LinkedList<ListLevel> depthLists)
            {
                return depthLists.getFirst().list;
            }
        };
        abstract JList getList(LinkedList<ListLevel> depthLists);
    };

    private void navigationButtonScreenAdjustments(ScrollTo scrollTo)
    {
        scenesDepthFrame.pack();

        double width = scenesDepthFrame.getSize().getWidth();
        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        if (width > screenWidth) {
            Rectangle bounds = scenesDepthFrame.getBounds();
            bounds.setRect(bounds.getX(), bounds.getY(), screenWidth - 50, bounds.getHeight());
            scenesDepthFrame.setBounds(bounds);
        }

        JList list = scrollTo.getList(depthLists);
        list.requestFocusInWindow();
        list.setSelectedIndex(0);

        scenesDepthFrame.setVisible(true);

        // Must come before the setVisible method above
        scrollToTheLastList(scrollPane, scrollTo);
    }

    private void scrollToTheLastList(JScrollPane scroll, ScrollTo to)
    {
        JScrollBar bar = scroll.getHorizontalScrollBar();
        bar.setValue(to.equals(ScrollTo.last) ? bar.getMaximum() : bar.getMinimum());
    }

    private Collection<Scene> mountListToStart(int depthNumber)
    {
        Collection<Scene> nextDepthScenes = DepthManager.getInstance().getScenesFromDepth(depthNumber);

        JList list = createList();
        list.setModel(createOrderedSceneListModel(nextDepthScenes));
        addListToStart(depthNumber, list);
        return nextDepthScenes;
    }

    private void mountListToEnd(int depthNumber, boolean selectScene, Scene sceneToSelect)
    {
        Collection<Scene> nextDepthScenes = DepthManager.getInstance().getScenesFromDepth(depthNumber);

        JList list = createList();
        list.setModel(createOrderedSceneListModel(nextDepthScenes));
        if (selectScene) {
            for (int i = 0; i < list.getModel().getSize(); i++) {
                Scene s = (Scene) list.getModel().getElementAt(i);
                if (s.equals(sceneToSelect)) {
                    list.setSelectedIndex(i);
                    break;
                }
            }
        }
        addListToEnd(depthNumber, list);
    }

    private void addListToStart(int level, JList depthList)
    {
        listsPanel.add(new JScrollPane(depthList), 0);
        depthLists.add(0, new ListLevel(level, depthList));
    }

    private void addListToEnd(int level, JList depthList)
    {
        listsPanel.add(new JScrollPane(depthList));
        depthLists.add(new ListLevel(level, depthList));
    }

    @SuppressWarnings("unchecked")
    private DefaultListModel createOrderedSceneListModel(Collection<Scene> origScenes)
    {
        DefaultListModel listModel = new DefaultListModel();
        List<Scene> scenes = new LinkedList<Scene>(origScenes);
        Collections.sort(scenes);

        for (Scene s : scenes) {
            listModel.addElement(s);
        }
        return listModel;
    }

    private JList createList()
    {
        JList list = new JList();
        list.setCellRenderer(new SceneDepthListCellRender());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setMinimumSize(new Dimension(120, 140));

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e)
            {
                listMouseClicked(e);
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                listMouseClicked(e);
            }
        });

        return list;
    }

    public JPanel getPanel()
    {
        return this.panel;
    }

    protected void listMouseClicked(EventObject e)
    {
        for (ListLevel listLevelTuple : depthLists) {
            listLevelTuple.list.setCellRenderer(new SceneDepthListCellRender());
        }

        JList list = (JList) e.getSource();
        selectedScene = (Scene) list.getSelectedValue();
        jumpToBt.setEnabled(true);
        jumpToBt.setText("Jump to [" + selectedScene.getName() + "]");

        int idx = depthLists.indexOf(new ListLevel(-1, list));
        assert idx != -1 : "Should have found a scenesList";

        if (idx == 0) {
            JList startList = depthLists.get(1).list;
            startList.setCellRenderer(new ToCellRenderer(selectedScene));
            Util.showComponent(startList);
        } else if (idx > 0) {
            JList fromList = depthLists.get(idx - 1).list;
            logger.debug("Before: " + fromList.getSelectedValue());

            fromList.setCellRenderer(new FromCellRenderer(selectedScene));
            Util.showComponent(fromList);

            if (idx + 1 < depthLists.size()) {
                JList afterList = depthLists.get(idx + 1).list;
                logger.debug("After: " + afterList.getSelectedValue());

                afterList.setCellRenderer(new ToCellRenderer(selectedScene));
                Util.showComponent(afterList);
            }
        }

        Util.showComponent(panel);
    }

    @Override
    public void fireEntityOpenedAction(EntityPersistedOnFileOpenAction e)
    {
        depthLists.clear();
    }
}
