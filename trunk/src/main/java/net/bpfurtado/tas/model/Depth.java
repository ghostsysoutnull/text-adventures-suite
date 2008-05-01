/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 13/10/2005 15:41:58                                                          
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

import java.util.Collection;
import java.util.LinkedList;

import net.bpfurtado.tas.AdventureException;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class Depth implements IDepth
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(Depth.class);

    private Integer level = 0;

    private Collection<PathDepth> pathDepths;

    public Depth(int level)
    {
        this.level = level;
        pathDepths = new LinkedList<PathDepth>();
    }

    public void add(PathDepth pathDepth)
    {
        pathDepths.add(pathDepth);
    }

    public Collection<Scene> getScenes()
    {
        Collection<Scene> scenes = new LinkedList<Scene>();
        for (PathDepth pathDepth : pathDepths) {
            scenes.add(pathDepth.getPath().getTo());
        }
        return scenes;
    }

    public int getNumberOfScenes()
    {
        return pathDepths.size();
    }

    public void remove(PathDepth pathDepthToRemove)
    {
		boolean removed = pathDepths.remove(pathDepthToRemove);
		if (!removed) {
			//throw new AdventureException("Should have removed the " + pathDepthToRemove);
		    logger.error("Should have removed the " + pathDepthToRemove);
		    return;
		}
		logger.debug(this + "<<== Removed " + pathDepthToRemove + ": my pathDepths are: " + pathDepths);
	}

    public int getLevel()
    {
        return level;
    }

    public boolean contains(Scene scene)
	{
		for (PathDepth pd : pathDepths) {
			if (pd.isRelatedTo(scene)) {
				return true;
			}
		}
		return false;
	}

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Depth) {
            Depth other = (Depth) obj;
            return level.equals(other.level);
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return level.hashCode() * 57;
    }

    @Override
    public String toString()
    {
        return "[Depth: level=" + level + "]";
    }
}
