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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import net.bpfurtado.tas.builder.SceneCodeToPasteHolder;

import org.apache.log4j.Logger;

public class TextComponentForPasteMouseListener extends MouseAdapter
{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(TextComponentForPasteMouseListener.class);

	private JPopupMenu popup;

	private JMenuItem menuItem;

	private JTextComponent text;

	private SceneCodeToPasteHolder ho;

	public TextComponentForPasteMouseListener(JPopupMenu popupMenu, JMenuItem menuItem, JTextComponent origin, JTextComponent destiny, SceneCodeToPasteHolder ho)
	{
		this.popup = popupMenu;
		this.menuItem = menuItem;
		this.text = origin;
		this.ho = ho;
	}

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

		String selectedText = text.getSelectedText();
		if (selectedText == null) {
			ho.setText(selectedText);
			menuItem.setEnabled(false);
			menuItem.setText("Select some text...");
		} else {
			ho.setText(new String(selectedText));
			if (selectedText.length() > 40) {
				selectedText = selectedText.substring(0, 40);
			}	
			menuItem.setEnabled(true);
			menuItem.setText("Paste [" + selectedText + "]");
		}

		popup.show(e.getComponent(), e.getX(), e.getY());
	}
}
