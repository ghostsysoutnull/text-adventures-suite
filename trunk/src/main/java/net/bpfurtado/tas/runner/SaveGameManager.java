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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Conf;
import net.bpfurtado.tas.model.Game;
import net.bpfurtado.tas.model.PlayerEventListener;
import net.bpfurtado.tas.model.Skill;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.thoughtworks.xstream.XStream;

public class SaveGameManager
{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
    
    private static final boolean DONT_EXEC_SCENE_ACTIONS = false;

    private Game game;
    private SaveGameListener listener;

    public SaveGameManager(Game game, SaveGameListener list)
    {
        this.game = game;
        this.listener = list;
    }

    void save()
    {
        SaveGame saveGame = buildSaveGame();
        Document xml = createXML(saveGame);
        File file = buildSaveGameFile();

        write(xml, file);

        listener.fireOpenSavedGameEvent(file);
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
        SaveGame saveGame = new SaveGame(game.getPlayer(), game.getCurrentScene().getId());
        saveGame.setAdventureFilePath(Conf.runner().get("lastAdventure"));
        return saveGame;
    }

    private Document createXML(SaveGame saveGame)
    {
        Document xml = DocumentHelper.createDocument();

        Element root = xml.addElement("savegame");
        root.addAttribute("sceneId", saveGame.getSceneId() + "");
        root.addAttribute("adventureFilePath", saveGame.getAdventureFilePath());

        Element p = root.addElement("player");

        Element skills = p.addElement("skills");
        for (Skill sk : saveGame.getPlayer().getSkills()) {
            Element skill = skills.addElement("skill");
            skill.addAttribute("name", sk.getName());
            skill.addAttribute("level", sk.getLevel() + "");
        }

        Element attributes = p.addElement("attributes");
        for (Entry<String, String> e : saveGame.getPlayer().getAttributesEntrySet()) {
            Element attribute = attributes.addElement("skill");
            attribute.addAttribute("key", e.getKey());
            attribute.addAttribute("value", e.getValue());
        }

        return xml;
    }

    private void write(Document xml, File saveFile)
    {
        try {
            listener.log("Saving game to file: [" + saveFile + "]...");

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("ISO-8859-1");
            format.setNewlines(true);
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLWriter writer = new XMLWriter(new FileWriter(saveFile), format);

            writer.write(xml);
            writer.flush();
            writer.close();

            listener.log("Game saved!");
        } catch (Exception e) {
            throw new AdventureException("Error writing Save Game", e);
        }
    }

    void open(File saveGameFile, PlayerEventListener playerEventListener)
    {
        try {
            SaveGame saveGame = read(saveGameFile);
            
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

    private SaveGame read(File saveGameFile) throws FileNotFoundException
    {
        //FIXME not using XStream anymore
        XStream xs = new XStream();
        SaveGame saveGame = (SaveGame) xs.fromXML(new FileReader(saveGameFile));
        return saveGame;
    }
}
