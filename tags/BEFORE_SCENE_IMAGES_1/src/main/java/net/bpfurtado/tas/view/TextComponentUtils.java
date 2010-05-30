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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import net.bpfurtado.tas.AdventureException;

public class TextComponentUtils
{
    public static JPopupMenu addCopyAndPaste(final Toolkit toolkit, final JTextComponent ta)
    {
        final JPopupMenu popup = new JPopupMenu();
        popup.setMinimumSize(new Dimension(240, 0));

        Object[] result = createMenuItems(toolkit, ta, popup);
        final TextToPasteHolder codeHolder = (TextToPasteHolder) result[0];
        final JMenuItem copyMnIt = (JMenuItem) result[1];

        ta.addMouseListener(new MouseAdapter()
        {
            TextToPasteHolder holder = codeHolder;

            public void mousePressed(MouseEvent e)
            {
                maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e)
            {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e)
            {
                if (!e.isPopupTrigger()) {
                    return;
                }
                copyMnIt.setEnabled(ta.getSelectedText() != null);
                holder.setText(ta.getSelectedText());
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        return popup;
    }

    private static Object[] createMenuItems(final Toolkit toolkit, final JTextComponent ta, final JPopupMenu popup)
    {
        final TextToPasteHolder codeHolder = new TextToPasteHolder();

        JMenuItem copyMnIt = new JMenuItem("Copy", Util.getImage("copy.gif"));
        copyMnIt.addActionListener(new ActionListener()
        {
            TextToPasteHolder holder = codeHolder;

            public void actionPerformed(ActionEvent e)
            {
                toolkit.getSystemClipboard().setContents(new StringSelection(holder.getText()), null);
            }
        });
        popup.add(copyMnIt);

        JMenuItem pasteMnIt = new JMenuItem("Paste", Util.getImage("paste.gif"));
        pasteMnIt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Transferable contents = toolkit.getSystemClipboard().getContents(null);
                try {
                    String clipBoardContents = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    ta.getDocument().insertString(ta.getCaretPosition(), clipBoardContents, null);
                } catch (Exception ex) {
                    throw new AdventureException(ex.getMessage(), ex);
                }
            }
        });
        popup.add(pasteMnIt);
        return new Object[] { codeHolder, copyMnIt };
    }
}
