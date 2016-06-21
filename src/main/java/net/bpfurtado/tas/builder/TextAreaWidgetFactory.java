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
package net.bpfurtado.tas.builder;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.bpfurtado.tas.view.TextComponentUtils;

public class TextAreaWidgetFactory
{
    static ScrollTextArea create(Font FONT, final Toolkit toolkit)
    {
        ScrollTextArea sta = new ScrollTextArea();

        final JTextArea ta = new JTextArea();
        ta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        ta.setFont(FONT);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane sceneTAScrollPane = new JScrollPane(ta);
        sceneTAScrollPane.setPreferredSize(new Dimension(690, 800));

        JPopupMenu popup = TextComponentUtils.addCopyAndPaste(toolkit, ta);

        sta.textArea = ta;
        sta.scrollPane = sceneTAScrollPane;
        sta.popup = popup;
        return sta;
    }
}

class ScrollTextArea
{
    JTextArea textArea;
    JScrollPane scrollPane;
    JPopupMenu popup;
}