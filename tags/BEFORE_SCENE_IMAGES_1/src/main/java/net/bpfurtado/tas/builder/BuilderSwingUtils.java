/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 05/05/2008 22:49:52
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

import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

public class BuilderSwingUtils
{
    public static ScrollTextArea createTextAreaWidgets(IBuilder builder, Toolkit toolkit)
    {
        ScrollTextArea sta = TextAreaWidgetFactory.create(Builder.FONT, toolkit);
        BuilderSwingUtils.addTextEventHandlers(sta.textArea, builder);
        return sta;
    }

    public static void addTextEventHandlers(JTextComponent textComponent, final IBuilder builder)
    {
        textComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() != 19) {
                    builder.markAsDirty();
                }
            }
        });
        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                builder.saveActualScene(); 
            }
        });
    }
}
