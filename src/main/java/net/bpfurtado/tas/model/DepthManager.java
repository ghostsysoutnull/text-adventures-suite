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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DepthManager
{
    /** Singleton stuff */
    private static DepthManager INSTANCE = new DepthManager();

    public static DepthManager getInstance()
    {
        return INSTANCE;
    }

    /** Attributes */
    private List<IDepth> depths;

    /** Constructor */
    private DepthManager()
    {
        depths = new LinkedList<IDepth>();
    }

    /** Methods */
    public void setStart(Scene start)
    {
        assert depths.isEmpty() : "Creating a new adventure, should have no depths, lacking a clean-up perhaps?";

        PathDepth pathDepth = new PathDepth(new NullPath(start), DepthManager.getInstance().getOrCreateDepth(0));
        start.add(Arrays.asList(pathDepth));
    }

    public Collection<Scene> getScenesFromDepth(int i)
    {
        return depths.get(i).getScenes();
    }

    public int getNumberOfScenesFromDepth(int idx)
    {
        return depths.get(idx).getNumberOfScenes();
    }

    public int getNumberOfDepths()
    {
        return depths.size();
    }

    public IDepth getOrCreateDepth(int i)
    {
        try {
            return depths.get(i);
        } catch (IndexOutOfBoundsException e) {
            if (i < 0) {
                throw e;
            }

            assert i == depths.size();

            Depth depth = new Depth(i);
            depths.add(depth);
            return depth;
        }
    }

    public int getFirstDepthOfScene(Scene s)
    {
        for (IDepth d : depths) {
            if (d.contains(s)) {
                return d.getLevel();
            }
        }
        return -1;
    }

    public void reset()
    {
        depths.clear();
    }

    @Override
    public String toString()
    {
        return "[DepthManager: depths+" + depths + "]";
    }
}
