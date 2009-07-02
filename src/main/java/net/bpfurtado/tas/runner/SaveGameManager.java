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

package net.bpfurtado.tas.runner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.model.Game;
import net.bpfurtado.tas.model.PlayerEventListener;
import net.bpfurtado.tas.model.Skill;

import com.thoughtworks.xstream.XStream;

public class SaveGameManager
{
    private static final boolean DONT_EXEC_SCENE_ACTIONS = false;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
    
    private SaveGameListener listener;
    private Game game;
    
    public SaveGameManager(Game game, SaveGameListener list)
    {
        this.game = game;
        this.listener = list;
    }

    void openSaveGameFile(File saveGameFile, PlayerEventListener playerEventListener)
    {
        try { 
            XStream xs = new XStream();
            SaveGame saveGame = (SaveGame) xs.fromXML(new FileReader(saveGameFile));
            Conf.runner().set("lastSavedGameFile", saveGameFile.getAbsolutePath());

            for (Skill s : saveGame.getPlayer().getSkills()) {
                s.setPlayer(saveGame.getPlayer());
            }

            listener.open(new File(saveGame.getAdventureFilePath()));
            game.open(saveGame);
            game.getPlayer().add(playerEventListener);
            listener.openScene(game.getAdventure().getScene(saveGame.getSceneId()), DONT_EXEC_SCENE_ACTIONS);

            listener.fireOpenSavedGameEvent(saveGameFile);
        } catch (Exception e) {
            throw new AdventureException(e);
        }
    }

    void saveGameAction()
    {
        SaveGame saveGame = new SaveGame(game.getPlayer(), game.getCurrentScene().getId());
        saveGame.setAdventureFilePath(Conf.runner().get("lastAdventure"));

        File savedGamesFolder = Conf.getSavedGamesFolder();

        String saveGameName = savedGamesFolder.getAbsolutePath() + File.separator + game.getAdventure().getName();
        saveGameName += "#" + sdf.format(new Date());
        saveGameName = saveGameName.replaceAll(" ", "") + ".saveGame.tas";

        try {
            listener.log("Saving game to file: [" + saveGameName + "]...");
            File saveGameFile = new File(saveGameName);
            saveGameFile.createNewFile();

            XStream xs = new XStream();
            String savedGameXML = xs.toXML(saveGame);
            PrintWriter writer = new PrintWriter(saveGameFile);
            writer.print(savedGameXML);
            writer.flush();
            writer.close();
            listener.log("Game saved!");

            // Will signal the recent menu to update: FIXME: it's a bit
            // confusing...
            listener.fireOpenSavedGameEvent(saveGameFile);
        } catch (IOException e) {
            throw new AdventureException(e);
        }
    }
}
