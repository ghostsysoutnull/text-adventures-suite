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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.runner.Runner;

import org.apache.log4j.Logger;

public class OpenningFrame extends JFrame
{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OpenningFrame.class);

	private static final long serialVersionUID = 7784790918999921301L;

	private static final Font FONT = new Font("Verdana", 1, 14);

    private static final int HEIGHT = 285;
	private static final int WIDTH = 585;

    private static OpenningFrame INSTANCE = new OpenningFrame();

    public static void open()
    {
        INSTANCE.setVisible(true);
    }

    public static void open(JFrame invoker)
    {
    	Util.centerPosition(invoker, INSTANCE, WIDTH, HEIGHT);
        INSTANCE.setVisible(true);
    }

    /**
     * TODO: FIX this: Kept still public not to break the jnlp main class conf.
     */
    public OpenningFrame()
    {
        initView();
    }

    private void initView()
    {
        widgets();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter()
        {
        	@Override
			public void windowClosed(WindowEvent e)
			{
				logger.debug("CLOSED");
				exitApplication();
				logger.debug("END CLOSED");
			}
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				logger.debug("CLOSING");
				//exitApplication();
			}
        });

        setBounds(235, 260, WIDTH, HEIGHT);
        setTitle("Choose your path! - Text Adventures Suite");
        setDefaultLookAndFeelDecorated(false);
        setResizable(false);
        setVisible(true);
    }

    protected void exitApplication()
	{
		dispose();

		Util.terminateProcessIfAlone();
	}

	private void widgets()
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        JLabel image = new JLabel(Util.getImage("openningFrame05_titled.png"));
        image.setPreferredSize(new Dimension(580, 200));
        image.setAlignmentX(CENTER_ALIGNMENT);
        p.add(image);

        JPanel bs = createButtonsPn();

        p.add(Box.createRigidArea(new Dimension(0, 13)));
        p.add(bs);

        add(p);
    }

    private JPanel createButtonsPn()
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        JButton builderBt = new JButton("Builder");
        builderBt.setFont(FONT);
        builderBt.setMnemonic('b');
        p.add(builderBt);
        p.add(Box.createRigidArea(new Dimension(15, 0)));

        JButton runnerBt = new JButton("Runner");
        runnerBt.setFont(FONT);
        runnerBt.setMnemonic('r');
        p.add(runnerBt);
        p.setAlignmentX(CENTER_ALIGNMENT);

        events(builderBt, runnerBt);

        return p;
    }

    private void events(JButton builderBt, JButton runnerBt)
    {
        builderBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                new Builder();
            }
        });

        runnerBt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Runner.runLastAdventure();
            }
        });
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                OpenningFrame.open();
            }
        });
    }
}
