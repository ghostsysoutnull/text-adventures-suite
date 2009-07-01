/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 01/10/2005 16:54:28                                                          
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.bpfurtado.tas.AdventureException;

import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class Adventure
{
    private static final Logger logger = Logger.getLogger(Adventure.class);
    
    private String name;

    private Scene start;
    private Map<Integer, Scene> scenes = new HashMap<Integer, Scene>();

    private String assertions;

    private int scenesIdCounter = 1;

    public Adventure()
    {
        start = createScene(0, false);
        start.setName("Start");
        start.setTags("Start");
        start.setText("Your adventure starts here");
        start.setCode("//Code is not executed in the Start scene.");
        start.setIsStart();

        DepthManager.getInstance().setStart(start);

        addScene(start);
    }

    public Scene createScene()
    {
        Scene s = new Scene(scenesIdCounter++);
        scenes.put(s.getId(), s);
        return s;
    }

    /**
     * Main method to fix - 111
     */
    public Scene createSceneFrom(IPath path)
    {
        Scene newScene = createScene();
        newScene.setName(path.getText());
        newScene.setText(path.getText());

        path.setTo(newScene);

        return newScene;
    }

    public Scene split(Scene orig, String newText)
    {
        Scene newScene = createScene();
        newScene.setName("From Split...");
        newScene.setText(newText);
        String leftText = orig.getText().substring(0, orig.getText().indexOf(newText));

        logger.debug("leftText=" + leftText + "]");
        orig.setText(leftText);
        logger.debug("orig.getText()=" + orig.getText() + "]");

        for (IPath p : orig.getPaths()) {
            IPath newPath = newScene.createPath(p.getText());
            newPath.setTo(p.getTo());
            orig.remove(p);
        }
        
        IPath p = orig.createPath("to new scene...");
        p.setTo(newScene);
        return newScene;
    }

    /**
     * Used to bring scenes from a persistent base, like a XML file.
     */
    public Scene createScene(int id, boolean isEnd)
    {
        Scene s = new Scene(id, isEnd);
        addScene(s);

        if (id >= scenesIdCounter) {
            scenesIdCounter = id + 1;
        }

        return s;
    }

    public void addScene(Scene scene)
    {
        scenes.put(scene.getId(), scene);
    }

    /**
     * FIXME rename to cloneScenesList
     */
    public List<Scene> getScenes()
    {
        return new LinkedList<Scene>(this.scenes.values());
    }

    public void remove(Scene sceneToRemove)
    {
        if (sceneToRemove.equals(start)) {
            throw new AdventureException("Cannot remove the start scene");
        }

        // Retirando a cena dos caminhos que levam a esta cena.
        for (Scene from : sceneToRemove.getScenesFrom()) {
            from.remove(sceneToRemove);
        }

        sceneToRemove.getScenesFrom().clear();
        sceneToRemove.removeAllPaths();

        scenes.remove(sceneToRemove.getId());
    }

    public Scene getScene(int id)
    {
        return scenes.get(id);
    }

    public String getName()
    {
        return this.name;
    }

    public Scene getStart()
    {
        return this.start;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Collection<Scene> getScenesFromDepth(int i)
    {
        return Collections.unmodifiableCollection(DepthManager.getInstance().getScenesFromDepth(i));
    }

    public int getNumberOfScenesFromDepth(int i)
    {
        return DepthManager.getInstance().getNumberOfScenesFromDepth(i);
    }

    public int getNumberOfDepths()
    {
        return DepthManager.getInstance().getNumberOfDepths();
    }

    public String getAssertions()
    {
        return assertions;
    }

    public void setAssertions(String assertions)
    {
        this.assertions = assertions;
    }

    public void addCombatEnemy(String rawTextWithEnemy)
    {
        // TODO Auto-generated method stub
        
    }
}
