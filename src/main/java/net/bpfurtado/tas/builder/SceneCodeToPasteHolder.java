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

import java.io.IOException;
import java.io.StringReader;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.view.TextToPasteHolder;

public class SceneCodeToPasteHolder extends TextToPasteHolder
{
    @Override
    public void setText(String text)
    {
        if (text == null) {
            super.setText(text);
            return;
        }

        StringBuilder b = new StringBuilder();
        StringReader r = new StringReader(text);
        try {
            for (int c = r.read(); c != -1; c = r.read()) {
                b.append((char) c);
                if (c == '{' || c == ';') {
                    b.append((char) '\n');
                }
            }
        } catch (IOException e) {
            throw new AdventureException(e.getMessage(), e);
        }
        super.setText(b.toString());
    }
}
