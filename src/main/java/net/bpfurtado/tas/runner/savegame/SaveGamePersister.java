/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 06/07/2009 20:43:43
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
import java.io.FileWriter;
import java.util.List;
import java.util.Map.Entry;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.Workspace;
import net.bpfurtado.tas.model.Player;
import net.bpfurtado.tas.model.Skill;
import net.bpfurtado.tas.model.persistence.AdventureReaderException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class SaveGamePersister
{
    public static Document createXML(SaveGame saveGame)
    {
        Document xml = DocumentHelper.createDocument();

        Element root = xml.addElement("savegame");
        root.addAttribute("sceneId", saveGame.getSceneId() + "");
        root.addAttribute("creation", saveGame.getCreationAsString());
        root.addAttribute("workspaceId", saveGame.getWorkspace().getId());

        Player player = saveGame.getPlayer();

        Element xmlPlayer = root.addElement("player");
        xmlPlayer.addAttribute("stamina", player.getStamina() + "");
        xmlPlayer.addAttribute("damage", player.getDamage() + "");

        Element skills = xmlPlayer.addElement("skills");
        for (Skill sk : player.getSkills()) {
            Element skill = skills.addElement("skill");
            skill.addAttribute("name", sk.getName());
            skill.addAttribute("level", sk.getLevel() + "");
        }

        Element attributes = xmlPlayer.addElement("attributes");
        for (Entry<String, String> e : player.getAttributesEntrySet()) {
            Element attribute = attributes.addElement("attribute");
            attribute.addAttribute("key", e.getKey());
            attribute.addAttribute("value", e.getValue());
        }

        return xml;
    }

    public static SaveGame read(File saveGameFile)
    {
        try {
            SAXReader xmlReader = new SAXReader();
            Document xml = xmlReader.read(saveGameFile);

            Element root = xml.getRootElement();

            Node xmlPlayer = root.selectSingleNode("player");

            Player player = new Player("noName", 0, integer(xmlPlayer, "stamina"));
            player.setDamage(integer(xmlPlayer, "damage"));

            List<Node> skills = xmlPlayer.selectNodes("skills/skill");
            for (Node skill : skills) {
                player.addSkill(skill.valueOf("@name"), integer(skill, "level"));
            }

            List<Node> attributes = xmlPlayer.selectNodes("attributes/attribute");
            for (Node attribute : attributes) {
                String key = attribute.valueOf("@key");
                String val = attribute.valueOf("@value");
                try {
                    player.addAttribute(key, Integer.parseInt(val));
                } catch (NumberFormatException e) {
                    player.addAttribute(key, val);
                }
            }

            Workspace workspace = Workspace.loadFrom(root.valueOf("@workspaceId"));
            SaveGame saveGame = new SaveGame(workspace, player, integer(root, "sceneId"), root.valueOf("@creation"));
            saveGame.setFile(saveGameFile);
            return saveGame;
        } catch (DocumentException e) {
            throw new AdventureReaderException("Error reading XML document", e);
        }
    }

    public static void write(Document xml, File saveGameFile, SaveGameListener saveGameListener)
    {
        try {
            saveGameListener.log("Saving game to file: [" + saveGameFile + "]...");

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("ISO-8859-1");
            format.setNewlines(true);
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLWriter writer = new XMLWriter(new FileWriter(saveGameFile), format);

            writer.write(xml);
            writer.flush();
            writer.close();

            saveGameListener.log("Game saved!");
        } catch (Exception e) {
            throw new AdventureException("Error writing Save Game", e);
        }
    }

    public static int integer(Node node, String attribute)
    {
        return Integer.parseInt(node.valueOf("@" + attribute));
    }
}
