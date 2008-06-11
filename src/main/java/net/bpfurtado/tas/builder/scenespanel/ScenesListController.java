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
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.view.Util;

import org.apache.log4j.Logger;

public class ScenesListController
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ScenesListController.class);

    static final Dimension INTERNAL_LIST_DIMENSION = new Dimension(200, 360);
    static final Dimension MAX_INTERNAL_LIST_DIMENSION = new Dimension(400, 810);

    private JPanel mainPn = null;
    private JPanel centerPn = null;

    private JTextField searchTF;
    private JPanel onePn = null;
    private JPanel anotherPn = null;

    private Collection<ScenesListControllerListener> listeners = new LinkedList<ScenesListControllerListener>();

    private SortBy sortBy = SortBy.Name;

    private JLabel sortLb;

    public ScenesListController()
    {
        initView();
    }

    public JPanel getPanel()
    {
        assert onePn != null && anotherPn != null;
        return mainPn;
    }

    public void add(ScenesListControllerListener l)
    {
        if (onePn == null) {
            onePn = l.getPanel();
            onePn.setVisible(l.isPanelVisible());
            centerPn.add(onePn);
        } else if (anotherPn == null) {
            anotherPn = l.getPanel();
            anotherPn.setVisible(l.isPanelVisible());
            centerPn.add(anotherPn);
        } else {
            throw new IllegalStateException();
        }
        listeners.add(l);
    }

    private void initView()
    {
        widgets();
    }

    private void widgets()
    {
        mainPn = new JPanel();
        mainPn.setLayout(new BoxLayout(mainPn, BoxLayout.PAGE_AXIS));

        searchFieldWidget(mainPn);

        centerPn = new JPanel();
        centerPn.setLayout(new BoxLayout(centerPn, BoxLayout.PAGE_AXIS));
        centerPn.setBackground(Color.blue);
        mainPn.add(centerPn);
    }

    private void searchFieldWidget(JPanel panelToAdd)
    {
        searchTF = new JTextField(15);
        searchTF.setMaximumSize(new Dimension(500, 21));

        searchTF.setToolTipText("Type some words to filter the scenes scenesList");

        JPanel searchBarPn = new JPanel();
        searchBarPn.setLayout(new BoxLayout(searchBarPn, BoxLayout.LINE_AXIS));
        searchBarPn.add(Box.createRigidArea(new Dimension(4, 0)));
        searchBarPn.add(searchTF);

        searchBarPn.add(buildClearBt(searchBarPn));
        sortLb = sortLbWidget();
        searchBarPn.add(sortLb);

        panelToAdd.add(searchBarPn);

        searchTF.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e)
            {
            }

            public void insertUpdate(DocumentEvent e)
            {
                searchFieldUpdateEvent();
            }

            public void removeUpdate(DocumentEvent e)
            {
                searchFieldUpdateEvent();
            }
        });

        searchTF.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    for(ScenesListControllerListener l: listeners) {
                        l.focusOnList();
                    }
                }
            }

            public void keyReleased(KeyEvent e)
            {
            }

            public void keyTyped(KeyEvent e)
            {
            }
        });
    }

    private JLabel buildClearBt(JPanel searchBarPn)
    {
        JLabel clearLb = new JLabel(Util.getImage("clear.gif"));
        clearLb.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        clearLb.setToolTipText("Clear the filter text field");
        clearLb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                searchTF.setText("");
            }
        });
        return clearLb;
    }

    private JLabel sortLbWidget()
    {
        JLabel sortLb = new JLabel(sortBy.next().image());
        sortLb.setPreferredSize(new Dimension(30, 25));
        sortLb.setToolTipText("Sort by Scene number or name");
        sortLb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                sortLabelMouseClicked();
            }
        });
        return sortLb;
    }

    private void sortLabelMouseClicked()
    {
        for (ScenesListControllerListener l : listeners) {
            l.sort(sortBy);
        }
        sortBy = sortBy.next();
        sortLb.setIcon(sortBy.image());
    }

    protected void searchFieldUpdateEvent()
    {
        for (ScenesListControllerListener l : listeners) {
            l.searchFieldUpdated(searchTF.getText());
        }
    }

    enum SortBy
    {
        Name
        {
            Comparator<Scene> getComparator()
            {
                return new Comparator<Scene>() {
                    public int compare(Scene o1, Scene o2)
                    {
                        return o1.getName().compareTo(o2.getName());
                    }
                };
            }

            SortBy next()
            {
                return Number;
            }

            ImageIcon image()
            {
                return Util.getImage("alphab_sort.gif");
            }
        },
        Number
        {
            Comparator<Scene> getComparator()
            {
                return new Comparator<Scene>() {
                    public int compare(Scene o1, Scene o2)
                    {
                        return Integer.valueOf(o1.getId()).compareTo(o2.getId());
                    }
                };
            }

            SortBy next()
            {
                return Name;
            }

            ImageIcon image()
            {
                return Util.getImage("numeric_sort.gif");
            }
        };
        abstract Comparator<Scene> getComparator();

        abstract SortBy next();

        abstract ImageIcon image();
    }
}
