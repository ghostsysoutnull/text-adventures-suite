/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
 * Created on 17/10/2005 21:57:59
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

package net.bpfurtado.tas.builder.scenespanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.builder.scenespanel.ScenesListController.SortBy;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class ScenesList extends SceneListBase
{
    private static final Logger logger = Logger.getLogger(ScenesList.class);

    private JPanel mainPanel;
    private JButton removeSceneBt;

    private Builder builder;
    private SortBy sortBy = SortBy.Name;

    private boolean displayAddNewAndDeleteButtons;

    public ScenesList(Builder builder, boolean showButtonsPane)
    {
        super();
        this.builder = builder;

        this.displayAddNewAndDeleteButtons = showButtonsPane;
        initView(showButtonsPane);
    }

    void keepRightSelectionOnSceneList()
    {
        for (int i = 0; i < list.getModel().getSize(); i++) {
            Scene s = (Scene) list.getModel().getElementAt(i);
            if (builder.getCurrentScene().equals(s)) {
                list.setSelectedIndex(i);
                list.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    /**
     * Update graphically the JList component.
     *
     * Can't be called in this object construction because at this time the Builder instance has not yet all
     * the proper attributes setted, which would cause a NPE.
     *
     * So after this instance contruction you have to manually call this method to properly graphical
     * render.
     */
    @SuppressWarnings("unchecked")
    public void updateView()
    {
        DefaultListModel listModel = new DefaultListModel();

        if (displayAddNewAndDeleteButtons) {
            List<Scene> scenes = new LinkedList<Scene>(builder.getAdventure().getScenes());
            Collections.sort(scenes, sortBy.getComparator());

            for (Scene s : scenes) {
                listModel.addElement(s);
            }

            list.setCellRenderer(new SceneCellRenderer(builder.getAdventure().getStart()));

            removeSceneBt.setEnabled(!builder.getCurrentScene().equals(builder.getAdventure().getStart()));
        } else {
            List<Scene> forbiddenScenes = new LinkedList<Scene>();
            for (IPath p : builder.getCurrentScene().getPaths()) {
                if (p.getTo() != null)
                    forbiddenScenes.add(p.getTo());
            }
            forbiddenScenes.add(builder.getCurrentScene());

            for (Scene s : builder.getAdventure().getScenes()) {
                if (!forbiddenScenes.contains(s))
                    listModel.addElement(s);
            }
            list.setCellRenderer(new SceneCellRenderer());
        }

        list.setModel(listModel);

        if (displayAddNewAndDeleteButtons) {
            keepRightSelectionOnSceneList();
        }
    }

    /**
     * Prepares the Scene JList component with the start scene, needed to create the Scene cell render, to
     * properly render the start scene with a different color.
     */
    public void prepareView(Scene start)
    {
        list.setCellRenderer(new SceneCellRenderer(start));
    }

    private void initView(boolean showButtonsPane)
    {
        widgets(showButtonsPane);
    }

    private void widgets(boolean showButtonsPane)
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)
            {
                scenesListMouseClicked();
            }
        });
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(ScenesListController.INTERNAL_LIST_DIMENSION);

        mainPanel.add(scrollPane);

        if (showButtonsPane) {
            JPanel buttonsPanel = widgetButtonsPanel();
            mainPanel.add(buttonsPanel);
        }
    }

    private JPanel widgetButtonsPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JButton newSceneBt = new JButton("New Scene", Util.getImage("new.gif"));
        newSceneBt.setMnemonic('N');
        newSceneBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                newSceneButtonActionPerformed();
            }
        });
        panel.add(newSceneBt);

        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        removeSceneBt = new JButton(Util.getImage("delete.gif"));
        removeSceneBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                removeSceneBtAction();
            }
        });

        panel.add(removeSceneBt);
        return panel;
    }

    private void removeSceneBtAction()
    {
        Scene toRemove = (Scene) list.getSelectedValue();
        logger.debug(toRemove.getName());
        builder.getAdventure().remove(toRemove);
        builder.switchTo(builder.getAdventure().getStart());
        builder.markAsDirty();
    }

    private void newSceneButtonActionPerformed()
    {
        builder.switchTo(builder.getAdventure().createScene());
        keepRightSelectionOnSceneList();
        builder.markAsDirty();
    }

    void scenesListMouseClicked()
    {
        if (displayAddNewAndDeleteButtons) {
            Scene selectedScene = (Scene) list.getModel().getElementAt(list.getSelectedIndex());
            builder.switchTo(selectedScene, list.getSelectedIndex());
        }
    }

    public JPanel getPanel()
    {
        return mainPanel;
    }

    public boolean isPanelVisible()
    {
        return true;
    }

    public void searchFieldUpdated(String filterExp)
    {
        mainPanel.setVisible(filterExp.trim().length() == 0);
    }

    public JList getList()
    {
        return list;
    }

    public void sort(SortBy criterion)
    {
        sortBy = criterion;

        List<Scene> sortedScenes = new LinkedList<Scene>();
        for (int i = 0; i < list.getModel().getSize(); i++) {
            sortedScenes.add((Scene) list.getModel().getElementAt(i));
        }
        Collections.sort((List<Scene>) sortedScenes, sortBy.getComparator());
        DefaultListModel sortedModel = new DefaultListModel();
        for (Scene s : sortedScenes) {
            sortedModel.addElement(s);
        }
        list.setModel(sortedModel);

        list.setVisible(true);
    }
}
