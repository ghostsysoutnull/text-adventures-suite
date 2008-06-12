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
package net.bpfurtado.tas.builder.scenespanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import net.bpfurtado.tas.builder.scenespanel.ScenesListController.SortBy;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

public class ScenesFilter extends SceneListBase implements ScenesListControllerListener
{
    private static Logger logger = Logger.getLogger(ScenesFilter.class);

    private JPanel panel;

    private ScenesSource scenesSource;

    private boolean displayAddNewAndDeleteButtons;

    public ScenesFilter(ScenesSource scenesSource, boolean showButtons)
    {
        super();
        this.scenesSource = scenesSource;
        this.displayAddNewAndDeleteButtons = showButtons;
        initView();
        events();
    }

    private void initView()
    {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        list.setModel(new DefaultListModel());
        list.setCellRenderer(new FilteredScenesListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Filtered scenes"));
        scrollPane.setMinimumSize(ScenesListController.INTERNAL_LIST_DIMENSION);
        scrollPane.setPreferredSize(ScenesListController.INTERNAL_LIST_DIMENSION);
        panel.add(scrollPane);
    }

    private void events()
    {
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)
            {
                scenesListMouseClicked();
            }
        });
    }

    void scenesListMouseClicked()
    {
        if (displayAddNewAndDeleteButtons) {
            SceneRank sceneRank = (SceneRank) list.getModel().getElementAt(list.getSelectedIndex());
            scenesSource.switchTo(sceneRank.getScene(), list.getSelectedIndex());
        }
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public boolean isPanelVisible()
    {
        return false;
    }

    public void searchFieldUpdated(String filterExp)
    {
        if (filterExp.trim().length() > 0) {
            panel.setVisible(true);
            rankScenes(filterExp);
        } else {
            panel.setVisible(false);
        }
    }

    private void rankScenes(String filter)
    {
        List<SceneRank> ranked = new LinkedList<SceneRank>();
        for (Scene scene : scenesSource.getScenes()) {
            SceneRank sceneRank = new SceneRank();
            applyFirstRankRules(filter, scene, sceneRank);
            for (IPath p : scene.getPaths()) {
                rankRule(filter, sceneRank, p.getText(), 1);
            }
            if (sceneRank.getRank() > 0) {
                sceneRank.setScene(scene);
                ranked.add(sceneRank);
            }
        }
        Collections.sort(ranked);
        logger.debug(ranked);

        DefaultListModel model = (DefaultListModel) list.getModel();
        model.clear();
        for (SceneRank sr : ranked) {
            model.addElement(sr);
        }
    }

    private void applyFirstRankRules(String filter, Scene s, SceneRank sceneRank)
    {
        rankRule(filter, sceneRank, s.getId() + "", 11);
        rankRule(filter, sceneRank, s.getTags(), 8);
        rankRule(filter, sceneRank, s.getName(), 5);
        rankRule(filter, sceneRank, s.getText(), 3);
        rankRule(filter, sceneRank, s.getCode(), 3);
    }

    private void rankRule(String filter, SceneRank sceneRank, String text, int rankPoints)
    {
        if (text.trim().toLowerCase().indexOf(filter.toLowerCase()) != -1) {
            sceneRank.setRank(sceneRank.getRank() + rankPoints);
        }
    }

    public void sort(SortBy by)
    {
    }

    public JList getList()
    {
        return list;
    }
}

class FilteredScenesListCellRenderer extends JLabel implements ListCellRenderer
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SceneCellRenderer.class);

    private static final long serialVersionUID = -809320266653417815L;

    public FilteredScenesListCellRenderer()
    {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        SceneRank sceneRank = (SceneRank) value;

        setText(" [" + sceneRank.getScene().getId() + "] " + sceneRank.getScene().getName() + " (" + sceneRank.getRank() + ")");

        Color orphanSceneColor = new Color(133, 213, 157);

        if (sceneRank.getScene().isOrphan()) {
            setBackground(isSelected ? Util.oceanColor : orphanSceneColor);
        } else {
            setBackground(isSelected ? Util.oceanColor : Color.white);
        }
        setForeground(Color.black);
        return this;
    }
}