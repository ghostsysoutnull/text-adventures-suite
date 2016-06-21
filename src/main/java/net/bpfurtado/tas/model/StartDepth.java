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
package net.bpfurtado.tas.model;

import java.util.ArrayList;
import java.util.Collection;

public class StartDepth implements IDepth
{
    private Collection<Scene> scenes = new ArrayList<Scene>(1);

    public StartDepth(Scene start)
    {
        assert start.isStart() : "Should be the start scene";
        scenes.add(start);
    }

    public void add(PathDepth pathDepth)
    {
        throw new UnsupportedOperationException();
    }

    public Collection<Scene> getScenes()
    {
        return scenes;
    }

    public int getNumberOfScenes()
    {
        return scenes.size();
    }

    public void remove(PathDepth pathDepth)
    {
        throw new UnsupportedOperationException();
    }

    public int getLevel()
    {
        return 0;
    }

    public boolean contains(Scene scene)
    {
        return scenes.contains(scene);
    }
}
