/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 01/10/2005 16:55:01                                                          
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.bpfurtado.tas.Util;
import net.bpfurtado.tas.model.combat.Combat;
import net.bpfurtado.tas.runner.CodeExecutionAnalyser;
import net.bpfurtado.tas.runner.PostCodeExecutionAction;

import org.apache.log4j.Logger;

/**
 * Warning: compareTo is not consistent with equals.
 * 
 * @author Bruno Patini Furtado
 */
public class Scene extends TextObject implements Comparable<Scene>
{
    private static final Logger logger = Logger.getLogger(Scene.class);

    private static int pathIdCounter;

    private String name;
    private String tags;
    private String code = "";

    private Combat combat;

    private SceneType type = SceneType.regular;

    private Skill skillToTest;

    /**
     * Just for the Runner module (bsh sub-module)
     * 
     * @return
     */
    private String originalText = null;

    private boolean isStart = false;

    private Map<Integer, IPath> paths = new HashMap<Integer, IPath>();
    private List<Scene> scenesFrom = new LinkedList<Scene>();

    private LinkedList<PathDepth> pathDepths = new LinkedList<PathDepth>();

    private String imageId;

    public List<PostCodeExecutionAction> executeActions(Game g)
    {
        return new CodeExecutionAnalyser().analyseCode(g, getCode(), getText());
    }

    public Scene(int id)
    {
        super(id);
        setName(id + "");
        setText("Your scene text goes here.");
    }

    public Scene(int id, boolean isEnd)
    {
        super(id);

        if (isEnd) {
            type = SceneType.end;
        } else {
            type = SceneType.regular;
        }
    }

    public Iterable<PathDepth> getPathDepths()
    {
        return pathDepths;
    }

    public void add(Collection<PathDepth> pathDepths)
    {
        this.pathDepths.addAll(pathDepths);
    }

    void setIsStart()
    {
        isStart = true;
    }

    public boolean isStart()
    {
        return isStart;
    }

    // TODO Abaixo os métodos antigos, por rever o que fazem manutencao nos Depths...

    public void addFrom(Scene from)
    {
        scenesFrom.add(from);
    }

    public void remove(IPath pathToRemove)
    {
        if (pathToRemove.getTo() != null) {
            pathToRemove.getTo().removeDepthsOf(pathToRemove);
        }
        paths.remove(pathToRemove.getId());
    }

    public void removeDepthsOf(IPath pathToBeRemoved)
    {
        logger.debug("REMOVING ### Scene: " + this);
        logger.debug("REMOVING ### Path " + pathToBeRemoved);
        for (ListIterator<PathDepth> ite = pathDepths.listIterator(); ite.hasNext();) {
            PathDepth pathDepth = ite.next();
            if (pathDepth.hasPath(pathToBeRemoved)) {
                // This path doesn't come to me anymore.
                pathDepth.removeItSelfFromDepth(pathToBeRemoved.getTo());
                logger.debug("REMOVING %%%: " + pathDepth);

                // TODO logica de se remover da scena feita na recursão
                // Não dá: java.util.ConcurrentModificationException

                logger.debug("&&& me: " + toString() + "removing pd: " + pathDepth);
                ite.remove();
            }
        }
    }

    public boolean isOrphan()
    {
        return scenesFrom.isEmpty();
    }

    public IPath createPath(String text)
    {
        Path path = new Path(pathIdCounter++, text, this);
        paths.put(path.getId(), path);

        return path;
    }

    /**
     * FIXME os from'scrollPane de uma scene virão das entidades PathDepth, refatorar isto em breve! TODO
     * could make read only
     */
    public List<Scene> getScenesFrom()
    {
        return scenesFrom;
    }

    public boolean hasScenesFrom()
    {
        return !scenesFrom.isEmpty();
    }

    public void markAsEndScene()
    {
        removeAllPaths();
        type = SceneType.end;
    }

    public void markAsCombatScene()
    {
        if (!type.equals(SceneType.combat)) {
            removeAllPathsButOne();
            setType(SceneType.combat);
        } else {
            logger.warn("Type is combat already, doing NOTHING");
        }
        // Mayber a better logic here.
        if (combat == null) {
            setCombat(new Combat());
        }
    }

    private void removeAllPathsButOne()
    {
        IPath remainingPath = null;
        for (IPath p : paths.values()) {
            if (remainingPath == null) {
                remainingPath = p;
            }
            if (p.getTo() != null) {
                p.getTo().scenesFrom.remove(this);
            }
        }
        paths.clear();
        remainingPath.setOrder(0);
        paths.put(remainingPath.getId(), remainingPath);
    }

    public void removeAllPaths()
    {
        for (IPath p : paths.values()) {
            if (p.getTo() != null) {
                p.getTo().scenesFrom.remove(this);
            }
        }
        paths.clear();
    }

    public int compareTo(Scene obj)
    {
        Scene other = (Scene) obj;
        if (this.name == null || other.name == null) {
            return 0;
        }
        if (Util.hasOnlyDigits(this.name) && Util.hasOnlyDigits(other.name)) {
            try {
                return new Integer(this.name).compareTo(new Integer(other.name));
            } catch (NumberFormatException nfe) {
                return new BigInteger(this.name).compareTo(new BigInteger(other.name));
            }
        }
        return this.name.compareTo(other.name);
    }

    public boolean isEnd()
    {
        return type == SceneType.end;
    }

    @Override
    public String toString()
    {
        // return "[Scene: id=" + getId() + ", name=" + getName() + ", from=" + scenesFrom + "]";
        return "[Scene: name=" + getName() + "]";
    }

    public List<IPath> getPaths()
    {
        List<IPath> sortedPaths = new LinkedList<IPath>(paths.values());
        Collections.sort(sortedPaths, new Comparator<IPath>() {
            public int compare(IPath p1, IPath p2)
            {
                return Integer.valueOf(p1.getOrder()).compareTo(p2.getOrder());
            }
        });
        return sortedPaths;
    }

    public boolean hasPaths()
    {
        return !paths.isEmpty();
    }

    public int getPathsSize()
    {
        return paths.values().size();
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTags()
    {
        return this.tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public void remove(Scene sceneToRemove)
    {
        for (IPath p : getPaths()) {
            if (p.getTo().equals(sceneToRemove)) {
                p.goToNowhere();
            }
        }
    }

    void remove(PathDepth pd)
    {
        pathDepths.remove(pd);
    }

    public void add(PathDepth pd)
    {
        pathDepths.add(pd);
    }

    public Collection<PathDepth> createPathDepthsCopy()
    {
        return new ArrayList<PathDepth>(pathDepths);
    }

    /**
     * For debug only
     */
    public int getPathDepthsSize()
    {
        return pathDepths.size();
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Just for the Runner module (bsh sub-module)
     * 
     * @return
     */
    public String getOriginalText()
    {
        return originalText;
    }

    @Override
    public void setText(String text)
    {
        if (getText() == null && originalText == null) {
            originalText = text;
        }
        logger.debug("id=[" + getId() + "], text=[" + text + "]");
        super.setText(text);
    }

    public Combat getCombat()
    {
        return combat;
    }

    public void setCombat(Combat combat)
    {
        this.combat = combat;
    }

    public SceneType getType()
    {
        return type;
    }

    public void setType(SceneType type)
    {
        this.type = type;
        if (type == SceneType.combat) {
            markAsCombatScene();
        } else if (type == SceneType.end) {
            markAsEndScene();
        }
    }

    public boolean canHaveMorePaths()
    {
        logger.debug("type=" + type);
        logger.debug("type.hasPathsNumberRestrictions()=" + type.hasPathsNumberRestrictions());
        logger.debug("getPathsSize()=" + getPathsSize());
        logger.debug("type.exactPathsNumberPermited()=" + type.exactPathsNumberPermited());

        if (type.hasPathsNumberRestrictions()) {
            return getPathsSize() < type.exactPathsNumberPermited();
        } else {
            return true;
        }
    }

    public Skill getSkillToTest()
    {
        return skillToTest;
    }

    public void setSkillToTest(Skill skillToTest)
    {
        this.skillToTest = skillToTest;
    }

    public String getImageId()
    {
        return imageId;
    }

    public void setImageId(String imageId)
    {
        this.imageId = imageId;
    }
}
