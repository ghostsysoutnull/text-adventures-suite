/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 31/05/2010 18:48:49
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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Workspace;
import net.bpfurtado.tas.view.Util;

public class OpenWorkspaceDialog extends JDialog
{
    private static final long serialVersionUID = -6728322169763109479L;
    private Workspace workspace;

    public OpenWorkspaceDialog(JFrame parent)
    {
        initView(parent);
    }

    private void initView(JFrame parent)
    {
        setTitle("Choose a Workspace");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        Util.centerPosition(parent, this, 336, 200);
        setModal(true);
        setModalityType(ModalityType.TOOLKIT_MODAL);

        widgets();

        setVisible(true);
    }

    private void widgets()
    {
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());

        main.add(buildWorkspacesHomeRef(), BorderLayout.PAGE_START);

        final JList list = new JList(new WorkspacesListModel());
        list.setBorder(BorderFactory.createTitledBorder("Workspaces"));
        list.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e)
            {
                selectedWorkspaceAction(list);
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
            }
        });
        main.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttonsPn = new JPanel();

        JButton chooseBt = new JButton("Choose");
        chooseBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                selectedWorkspaceAction(list);
            }
        });
        buttonsPn.add(chooseBt, BorderLayout.PAGE_END);

        JButton cancelBt = new JButton("Cancel");
        cancelBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                dispose();
            }
        });
        buttonsPn.add(cancelBt, BorderLayout.PAGE_END);

        main.add(buttonsPn, BorderLayout.PAGE_END);

        list.setSelectedIndex(0);
        list.requestFocusInWindow();

        add(main);
    }

    private JComponent buildWorkspacesHomeRef()
    {
        JLabel lb = new JLabel(Workspace.getWorkspacesHome());
        lb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                try {
                    Desktop.getDesktop().open(new File(Workspace.getWorkspacesHome()));
                } catch (IOException e1) {
                    throw new AdventureException(e1);
                }
            }
        });
        return lb;
    }

    private void selectedWorkspaceAction(final JList list)
    {
        WorkspaceHolder holder = (WorkspaceHolder) list.getSelectedValue();
        OpenWorkspaceDialog.this.workspace = holder.getWorkspace();
        dispose();
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    private static class WorkspacesListModel extends AbstractListModel
    {
        private static final long serialVersionUID = 1L;

        private List<WorkspaceHolder> all;

        public WorkspacesListModel()
        {
            all = new LinkedList<WorkspaceHolder>();
            List<Workspace> ws = Workspace.listAll();
            for (Workspace w : ws) {
                all.add(new WorkspaceHolder(w));
            }
        }

        @Override
        public Object getElementAt(int idx)
        {
            return all.get(idx);
        }

        @Override
        public int getSize()
        {
            return all.size();
        }
    }

    private static class WorkspaceHolder
    {
        private Workspace workspace;

        public WorkspaceHolder(Workspace w)
        {
            this.workspace = w;
        }

        @Override
        public String toString()
        {
            return workspace.getAdventure().getName();
        }

        public Workspace getWorkspace()
        {
            return workspace;
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                JFrame f = new JFrame("Mock Parent");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setBounds(150, 150, 336, 200);
                f.setVisible(true);
                OpenWorkspaceDialog dia = new OpenWorkspaceDialog(f);
                Workspace w = dia.getWorkspace();
                System.out.println(w.getAdventure().getName());
            }
        });
    }
}
