/**
 * Copyright (C) 2005 Bruno Patini Furtado
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 09/10/2005 21:49:53
 */
package net.bpfurtado.tas.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.bpfurtado.tas.AdventureException;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class Util
{
    private static Logger logger = Logger.getLogger(Util.class);

    public static final Color oceanColor = new Color(184, 207, 229);

    public static void showComponent(JComponent component)
    {
        component.setVisible(false);
        component.setVisible(true);
    }

    public static final int SAVE_DIALOG_OPT_SAVE = 0;
    public static final int SAVE_DIALOG_OPT_CANCEL = 2;

    public static int showSaveDialog(JFrame parent, String message)
    {
        Object[] saveDialogOptions = new Object[] { "Save", "Discard Changes", "Cancel" };
        return JOptionPane.showOptionDialog(parent, "You have an unsaved adventure!\n" + message, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, saveDialogOptions, saveDialogOptions[0]);
    }

    public static ImageIcon getImage(String imageName)
    {
        try {
            return new ImageIcon(Util.class.getResource("/net/bpfurtado/tas/images/" + imageName));
        } catch (NullPointerException npe) {
            throw new AdventureException(imageName + " could no be loaded", npe);
        }
    }

    public static JFileChooser createFileChooser(String initDir)
    {
        JFileChooser fileChooser = new JFileChooser(new File(initDir));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f)
            {
                return f.getName().endsWith(".adv.xml") || f.isDirectory();
            }

            @Override
            public String getDescription()
            {
                return "Text Adventures Suite - Project";
            }
        });
        return fileChooser;
    }

    private static JMenu createHelpMenu(final JFrame frame)
    {
        JMenu help = new JMenu("Help");
        help.setMnemonic('h');

        JMenuItem welcomeMnIt = new JMenuItem("Welcome", Util.getImage("world.png"));
        welcomeMnIt.setMnemonic('w');
        welcomeMnIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                OpenningFrame.open(frame);
            }
        });
        help.add(welcomeMnIt);
        help.add(new JSeparator());

        JMenuItem aboutMnIt = new JMenuItem("About", Util.getImage("controller.png"));
        aboutMnIt.setMnemonic('a');
        aboutMnIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                new AboutFrame(frame);
            }
        });
        help.add(aboutMnIt);

        return help;
    }

    public static void addHelpMenu(JMenuBar menuBar, JFrame frame)
    {
        menuBar.add(Box.createHorizontalGlue());
        JMenu help = createHelpMenu(frame);
        menuBar.add(help);
    }

    public static void centerPosition(JFrame invokerFrame, Window w, int width, int height)
    {
        int x = invokerFrame.getX() + (invokerFrame.getWidth() - width) / 2;
        int y = invokerFrame.getY() + (invokerFrame.getHeight() - height) / 2;

        w.setBounds(x, y, width, height);
    }

    public static void terminateProcessIfAlone()
    {
        logger.debug("START");
        for (Frame f : Frame.getFrames()) {
            logger.debug("name=" + f.getTitle());
            logger.debug("\tactive=" + f.isActive());
            logger.debug("\tvalid=" + f.isValid());
            logger.debug("\tisDisplayable=" + f.isDisplayable());
            if (f.isDisplayable()) {
                logger.debug("CAN'T EXIT");
                return;
            }
        }
        System.exit(0);
    }

    public static JMenuItem menuItem(String text, char mnemonic, int key, String imageName, JMenu adventureMenu, ActionListener action)
    {
        JMenuItem it = new JMenuItem(text, getImage(imageName));
        it.setAccelerator(KeyStroke.getKeyStroke(key, ActionEvent.CTRL_MASK));
        it.setMnemonic(mnemonic);
        it.addActionListener(action);
        adventureMenu.add(it);

        return it;
    }

    public static JMenu menu(String text, char mnemonic, JMenuBar menuBar)
    {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);

        menuBar.add(menu);
        return menu;
    }

    public static void centerPosition(Window window, Window w, int width, int height)
    {
        int x = window.getX() + (window.getWidth() - width) / 2;
        int y = window.getY() + (window.getHeight() - height) / 2;

        w.setBounds(x, y, width, height);
    }

    public static void addWidth(JPanel panel)
    {
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
    }

    public static void addHeight(JPanel panel, int height)
    {
        panel.add(Box.createRigidArea(new Dimension(0, height)));
    }
}
