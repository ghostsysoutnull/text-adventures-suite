/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
 * Created on 29/10/2005 15:03:52
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
import java.util.List;

import net.bpfurtado.tas.runner.CodeExecutionAnalyser;
import net.bpfurtado.tas.runner.PostCodeExecutionAction;
import net.bpfurtado.tas.runner.savegame.SaveGame;

/**
 * @author Bruno Patini Furtado
 */
public class GameImpl implements Game
{
    private static final long serialVersionUID = -724652209259353975L;

    private static final int NO_SCENE_TO_GO = -1;

    private Adventure adventure;
    private Player player;
    private Scene currentScene;

    private Collection<GoToSceneListener> listeners = new LinkedList<GoToSceneListener>();

    /**
     * Related to response of Scene Code Actions
     */
    private int sceneIdToOpen = -1;

    /**
     * Related to response of Scene Code Actions
     */
    private Collection<Integer> pathsToHide = new LinkedList<Integer>();

    public GameImpl(Adventure a)
    {
        this.adventure = a;
        this.player = new Player("Player");
    }

    public void open(Scene scene)
    {
        innerOpenPath(scene);

        if (getSceneIdToOpen() != NO_SCENE_TO_GO) {
            Scene sceneOrientedByScriptCode = adventure.getScene(getSceneIdToOpen());
            innerOpenPath(sceneOrientedByScriptCode);
            setSceneToOpen(NO_SCENE_TO_GO);
        }
    }

    public void openNoActions(Scene to)
    {
        setCurrentScene(to);
    }

    public Adventure open(SaveGame saveGame)
    {
        // this.adventure = new XMLAdventureReader().read(saveGame.getAdventureFilePath());
        this.player = saveGame.getPlayer();

        return adventure;
    }

    private void innerOpenPath(Scene scene)
    {
        setCurrentScene(scene);

        execCode(scene, scene.executeActions(this));
        execPostCodeActions();

        execAssertions();
        execPostCodeActions();
    }

    private void execCode(Scene scene, List<PostCodeExecutionAction> actions)
    {
        for (PostCodeExecutionAction action : actions) {
            action.exec(this);
        }
        /*
         * for (GoToSceneListener goToSceneListener : listeners) { //Goto at the moment not being so
         * complete as the //OpenPath at the Runner Class. //111 Problem Here!!!
         * goToSceneListener.goTo(scene); }
         */
    }

    public void execPostCodeActions()
    {
        // OPEN SCENE: GOTOs
        /*
         * if (sceneIdToOpen != -1) { openPath(adventure.getScene(sceneIdToOpen)); sceneIdToOpen = -1; }
         */

        // PATH HIDEs
        if (!pathsToHide.isEmpty()) {
            int order = 0;
            for (IPath p : currentScene.getPaths()) {
                if (pathsToHide.contains(order++)) {
                    p.setVisible(false);
                }
            }
            pathsToHide.clear();
        }
    }

    public void execAssertions()
    {
        List<PostCodeExecutionAction> actions = new CodeExecutionAnalyser().analyseCode(this, adventure.getAssertions(), getCurrentScene().getText());

        execCode(getCurrentScene(), actions);
    }

    public Player getPlayer()
    {
        return player;
    }

    public void addGoToSceneListener(GoToSceneListener goToSceneListener)
    {
        listeners.add(goToSceneListener);
    }

    public void setSceneToOpen(int sceneId)
    {
        this.sceneIdToOpen = sceneId;
    }

    public void setCurrentScene(Scene currentScene)
    {
        this.currentScene = currentScene;
    }

    public void addPathToHideByOrder(Collection<Integer> pathsToHide)
    {
        this.pathsToHide = pathsToHide;
    }

    public Scene getCurrentScene()
    {
        return currentScene;
    }

    public int getSceneIdToOpen()
    {
        return sceneIdToOpen;
    }

    public Adventure getAdventure()
    {
        return adventure;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }
}
