/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 27/10/2005 22:56:47                                                          
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

import net.bpfurtado.tas.runner.SaveGame;

/**
 * @author Bruno Patini Furtado
 */
public interface Game
{
    void open(Scene scene);
    void execPostCodeActions();

    Player getPlayer();
    void addGoToSceneListener(GoToSceneListener goToSceneListener);

	void setCurrentScene(Scene scene);
	Scene getCurrentScene();

	void setSceneToOpen(int sceneId);
	int getSceneIdToOpen();

	void addPathToHideByOrder(Collection<Integer> pathsToHide);
	void execAssertions();
    void open(SaveGame saveGame);
    void openNoActions(Scene to);
}
