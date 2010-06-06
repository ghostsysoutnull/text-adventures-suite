/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 02/07/2009 19:04:31
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

package net.bpfurtado.tas.runner.savegame;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.Workspace;
import net.bpfurtado.tas.model.Game;
import net.bpfurtado.tas.model.Player;
import net.bpfurtado.tas.model.PlayerEventListener;
import net.bpfurtado.tas.model.Skill;

import org.apache.log4j.Logger;
import org.dom4j.Document;

public class SaveGameManager
{
    private static final Logger logger = Logger.getLogger(SaveGameManager.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-hh_mm_ss");
    private static final boolean DONT_EXEC_SCENE_ACTIONS = false;

    private Game game;
    private SaveGameListener runner;
    private Workspace workspace;
    
    public SaveGameManager(Workspace workspace, Game game, SaveGameListener list)
    {
        this.workspace = workspace;
        this.game = game;
        this.runner = list;
    }

    public File save()
    {
        SaveGame saveGame = buildSaveGame();
        Document xml = SaveGamePersister.createXML(saveGame);
        File file = buildSaveGameFile();

        SaveGamePersister.write(xml, file, runner);

        //FIXME solve line bellow
        runner.fireOpenSavedGameEvent(saveGame);

        return file;
    }

    private File buildSaveGameFile()
    {
        File savedGamesFolder = Conf.getSavedGamesFolder();

        String saveGameName = savedGamesFolder.getAbsolutePath() + File.separator + game.getAdventure().getName();
        saveGameName += "#" + sdf.format(new Date());
        saveGameName = saveGameName.replaceAll(" ", "") + ".saveGame.tas";

        File saveGameFile = new File(saveGameName);
        return saveGameFile;
    }

    private SaveGame buildSaveGame()
    {
        return new SaveGame(workspace, game.getPlayer(), game.getCurrentScene().getId());
    }

    public SaveGame open(String saveGameFilePath, PlayerEventListener playerEventListener) //999
    {
        try {
            File saveGameFile = new File(saveGameFilePath);
            
            SaveGame saveGame = SaveGamePersister.read(saveGameFile);

            Conf.runner().set("lastSavedGameFile", saveGameFile.getAbsolutePath());

            for (Skill s : saveGame.getPlayer().getSkills()) {
                s.setPlayer(saveGame.getPlayer());
            }

            // creates a new gameImpl // FIXME
            this.game = runner.gameFrom(saveGame.getWorkspace().getId());
            
            Player player = game.getPlayer();
            player.add(playerEventListener);

            runner.openScene(game.getAdventure().getScene(saveGame.getSceneId()), DONT_EXEC_SCENE_ACTIONS);

            //FIXME: solve line bellow
            runner.fireOpenSavedGameEvent(saveGame);
            return saveGame;
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }
}