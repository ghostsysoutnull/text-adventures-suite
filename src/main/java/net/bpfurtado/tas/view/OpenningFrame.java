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

import static net.bpfurtado.tas.view.Util.addHeight;
import static net.bpfurtado.tas.view.Util.addWidth;
import static net.bpfurtado.tas.view.Util.button;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.builder.Builder;
import net.bpfurtado.tas.runner.Runner;

import org.apache.log4j.Logger;

public class OpenningFrame extends JFrame
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(OpenningFrame.class);

    private static final long serialVersionUID = 7784790918999921301L;

    private static OpenningFrame INSTANCE = new OpenningFrame();

    public static void open()
    {
        INSTANCE.setVisible(true);
    }

    public static void open(JFrame invoker)
    {
        Util.centerPosition(invoker, INSTANCE, 585, 285);
        INSTANCE.setVisible(true);
    }

    /**
     * TODO: FIX this: Kept still public (besides being a Singleton) not to break the jnlp main class conf.
     */
    public OpenningFrame()
    {
        initView();
    }

    private void initView()
    {
        widgets();
        
        Util.setBoundsFrom(Conf.oppening(), this, 665, 287);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                Util.exitApplication(OpenningFrame.this, Conf.oppening());
            }
        });

        setTitle("Choose your path! - Text Adventures Suite");
        setDefaultLookAndFeelDecorated(false);
        setResizable(false);
        setVisible(true);
    }

    private void widgets()
    {
        JPanel mainPn = Util.createPageBoxPanel();

        JLabel openningImage = new JLabel(Util.getImage("openningFrame05_titled.png"));
        openningImage.setPreferredSize(new Dimension(580, 200));
        openningImage.setAlignmentX(CENTER_ALIGNMENT);
        mainPn.add(openningImage);

        addHeight(mainPn, 13);
        mainPn.add(createButtonsPn());

        add(mainPn);
    }

    private JPanel createButtonsPn()
    {
        Font font = new Font("Verdana", 1, 14);

        JPanel p = Util.createLineBoxPanel();
        p.setAlignmentX(CENTER_ALIGNMENT);

        JButton builderBt = button(p, "Create an Adventure", 'c', font);
        addWidth(p, 15);
        JButton runnerBt = button(p, "Play an Adventure", 'p', font);

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
