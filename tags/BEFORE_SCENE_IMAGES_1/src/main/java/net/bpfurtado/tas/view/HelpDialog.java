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
package net.bpfurtado.tas.view;

import static net.bpfurtado.commons.io.FileUtils.linesFromClasspath;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class HelpDialog extends JFrame
{
    private static final Logger logger = Logger.getLogger(HelpDialog.class);
    private static final long serialVersionUID = 124896221854096052L;

    private String title;
    private String classpathHelpFile;
    private JEditorPane editorPane;

    public static HelpDialog createNew()
    {
        return new HelpDialog("Scene Code programming", "/net/bpfurtado/tas/builder/codeHelp.html");
    }

    public HelpDialog(String title, String fileInClassPath) throws HeadlessException
    {
        this.title = title;
        this.classpathHelpFile = fileInClassPath;
        initView();
    }

    private void initView()
    {
        widgets();

        setTitle(title + " - Text Adventures Suite");
        setBounds(235, 260, 652, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);

        setVisible(true);
    }

    private void widgets()
    {
        logger.debug("help file = " + classpathHelpFile);

        StringBuilder text = new StringBuilder();
        for (String line : linesFromClasspath(classpathHelpFile)) {
            text.append(line);
        }

        editorPane = new JEditorPane("text/html", text.toString());
        editorPane.setEditable(false);
        add(new JScrollPane(editorPane));

        TextComponentUtils.addCopyAndPaste(getToolkit(), getEditorPane());

        JPanel p = new JPanel();
        JButton closeBt = new JButton("Close");
        closeBt.setMnemonic('C');
        closeBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        p.add(closeBt);

        add(p, BorderLayout.PAGE_END);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                new HelpDialog("Help", "/net/bpfurtado/tas/builder/codeHelp.html");
            }
        });
    }

    public JEditorPane getEditorPane()
    {
        return editorPane;
    }
}
