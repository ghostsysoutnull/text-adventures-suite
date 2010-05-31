/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on May 31, 2010 1:28:13 PM
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class OpenWorkspaceDialog extends JDialog
{
    private static final long serialVersionUID = -1955279929109799753L;

    private Workspace workspace;

    public OpenWorkspaceDialog(JFrame parent)
    {
        super(parent, true);
        initView();
    }

    private void initView()
    {
        setTitle("Choose a Workspace");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        setBounds(200, 200, 336, 200);
        setVisible(true);

        widgets();
    }

    private void widgets()
    {
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());

        final JList list = new JList(new WorkspacesListModel());
        list.setBorder(BorderFactory.createTitledBorder("Workspaces"));
        main.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttonsPn = new JPanel();
        
        JButton chooseBt = new JButton("Choose");
        chooseBt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                WorkspaceHolder holder = (WorkspaceHolder) list.getSelectedValue();
                OpenWorkspaceDialog.this.workspace = holder.getWorkspace();
                dispose();
            }
        });
        buttonsPn.add(chooseBt, BorderLayout.PAGE_END);
        
        JButton cancelBt = new JButton("Cancel");
        cancelBt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                dispose();
            }
        });
        buttonsPn.add(cancelBt, BorderLayout.PAGE_END);
        
        main.add(buttonsPn, BorderLayout.PAGE_END);

        add(main);
    }

    class WorkspacesListModel extends AbstractListModel
    {
        private static final long serialVersionUID = -8167581289776094824L;

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
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame f = new JFrame("Mock Parent");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setBounds(150, 150, 336, 200);
                f.setVisible(true);
                new OpenWorkspaceDialog(f);
            }
        });
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }
}
