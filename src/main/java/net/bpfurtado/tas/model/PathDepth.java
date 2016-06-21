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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

/**
 * Just relates a Path with a Depth.
 * 
 * Always that a path leads a scene to be contained in a new depth (always when the path leads to a scene)
 * one new instance of this is created to make the precise link between the path that lead one Scene to one
 * Depth.
 */
public class PathDepth
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PathDepth.class);

    private static int idCounter = 0;

    private int id = idCounter++;
    private IPath path;
    private IDepth depth;

    private List<PathDepth> children = new LinkedList<PathDepth>();

    public PathDepth(IPath path, IDepth depth)
    {
        this.path = path;
        this.depth = depth;

        depth.add(this);
    }

    public PathDepth createChild(IPath path)
    {
        PathDepth child = new PathDepth(path, nextDepth());
        children.add(child);

        // logger.debug(id + "::I've got the child: " + child.toStringShort());

        return child;
    }

    private IDepth nextDepth()
    {
        return DepthManager.getInstance().getOrCreateDepth(depth.getLevel() + 1);
    }

    public boolean hasPath(IPath otherPath)
    {
        return path.equals(otherPath);
    }

    private static String tabs = "";

    public void removeItSelfFromDepth(Scene startScene)
    {
        logger.debug("###:= REMOVING: " + this);

        // TODO $$$ remove error
        // path.getTo().remove(this);

        tabs = "";
        removeItSelfFromDepth(this, startScene);
    }

    private static void removeItSelfFromDepth(PathDepth pathDepth, Scene startScene)
    {
        tabs += "\t";

        logger.debug(tabs + "I am " + pathDepth);
        logger.debug(tabs + "Going out of depth " + pathDepth.depth);
        pathDepth.depth.remove(pathDepth);

        logger.debug(
                "&&& Trying [me:" + pathDepth.toString() + "]to go out of my scene " + pathDepth.getPath().getTo());
        if (!pathDepth.getPath().getTo().equals(startScene)) {
            logger.debug("&&& YES me: " + pathDepth.toString() + "removing pd: " + pathDepth);
            pathDepth.getPath().getTo().remove(pathDepth); // 111
        } else {
            logger.debug(
                    "&&& NO me: " + pathDepth.toString() + " NOT BEING REMOVED from " + pathDepth.getPath().getTo());
        }

        logger.debug(tabs + "REMOVING (recursive): " + pathDepth);

        logger.debug(tabs + "My children=" + pathDepth.children);

        assert !pathDepth.depth.getScenes().contains(pathDepth);

        for (ListIterator<PathDepth> parentChildren = pathDepth.children.listIterator(); parentChildren.hasNext();) {
            PathDepth child = parentChildren.next();

            assert !child.equals(pathDepth);

            logger.debug(tabs + "removing depths from child " + child);

            logger.debug(tabs + " REMOVING, CALLING recursive with : " + child);
            removeItSelfFromDepth(child, startScene);
            parentChildren.remove();

            /*
             * if (!child.getPath().getTo().equals(startScene)) { logger.debug("&&& me: " + toString() +
             * "removing pd: " + child); child.getPath().getTo().remove(this); // TODO ver se funciona aqui
             * } else { System.out.println("here"); }
             */

            logger.debug(tabs + "child " + child + " removed");
        }

        logger.debug(tabs + "Now my children are " + pathDepth.children);

        tabs = tabs.substring(tabs.length() - 1);
    }

    public IPath getPath()
    {
        return path;
    }

    public Iterable<PathDepth> children()
    {
        return children;
    }

    @Override
    public String toString()
    {
        return "[PathDepth: id=" + id + ", path=" + path + ", depth=" + depth + "]";
    }

    public String toStringShort()
    {
        return "[" + id + ", dep=" + depth.getLevel() + "to=" + path.getTo() + "]";
    }

    public boolean relatesTo(Path otherPath)
    {
        return this.path.equals(otherPath);
    }

    public boolean isRelatedTo(Scene scene)
    {
        return path.getTo().equals(scene);
    }
}
