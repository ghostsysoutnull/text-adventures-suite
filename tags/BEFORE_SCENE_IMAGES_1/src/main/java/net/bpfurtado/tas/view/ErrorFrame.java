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
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ErrorFrame extends JFrame
{
	private static final long serialVersionUID = 5890537060503281033L;

	private Exception exception;

	private JFrame parentFrame;

	private JTextArea ta;

	public ErrorFrame(JFrame frame, Exception e, String title)
	{
		this.parentFrame = frame;
		this.exception = e;
		initView(title);
	}

	private void initView(String title)
	{
		widgets();

		setTitle(title + " Error - Text Adventures Suite");
		setBounds(parentFrame.getX() + 30, parentFrame.getY() + 30, 620, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);

		setVisible(true);
	}

	private void widgets()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

		p.add(Box.createRigidArea(new Dimension(0, 5)));

		JLabel label = new JLabel("  " + exception.getMessage() + "  ");
		label.setBorder(BorderFactory.createRaisedBevelBorder());
		label.setAlignmentX(CENTER_ALIGNMENT);
		p.add(label);

		p.add(Box.createRigidArea(new Dimension(0, 5)));

		StringWriter writer = new StringWriter();
		exception.printStackTrace(new PrintWriter(writer));
		ta = new JTextArea(writer.getBuffer().toString());
		ta.setMaximumSize(new Dimension(400, 400));
		JScrollPane taScrollPane = new JScrollPane(ta);
		taScrollPane.setAlignmentX(CENTER_ALIGNMENT);
		p.add(taScrollPane);

		addListeners(ta);

		p.add(Box.createRigidArea(new Dimension(0, 5)));

		JButton closeBt = new JButton("Close");
		closeBt.setMnemonic('c');
		closeBt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		closeBt.setAlignmentX(CENTER_ALIGNMENT);
		p.add(closeBt);

		p.add(Box.createRigidArea(new Dimension(0, 5)));

		add(p);
	}

	private void addListeners(final JTextArea ta)
	{
		final JPopupMenu copyTracePopupMenu = new JPopupMenu();
		JMenuItem copyMnIt = new JMenuItem("Copy Stacktrace");
		copyMnIt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ta.getText()), null);
			}
		});

		copyTracePopupMenu.add(copyMnIt);
		ta.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				showPopup(copyTracePopupMenu, e);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				showPopup(copyTracePopupMenu, e);
			}

			private void showPopup(final JPopupMenu m, MouseEvent e)
			{
				if (!e.isPopupTrigger()) {
					return;
				}
				m.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame someFrame = new JFrame();
				someFrame.setBounds(50, 50, 200, 200);
				new ErrorFrame(someFrame, new Exception("Big error message"), "Scene script");
			}
		});
	}
}
