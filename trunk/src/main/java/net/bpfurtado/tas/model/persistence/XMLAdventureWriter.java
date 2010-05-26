/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 06/10/2005 17:13:44                                                          
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

package net.bpfurtado.tas.model.persistence;

import java.io.File;
import java.io.FileWriter;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.SceneType;
import net.bpfurtado.tas.model.combat.Fighter;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author Bruno Patini Furtado
 */
public class XMLAdventureWriter
{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(XMLAdventureWriter.class);
	
    private File saveFile;

    private Adventure adventure;

    public XMLAdventureWriter(Adventure adventure, File saveFile)
    {
        this.saveFile = saveFile;
        this.adventure = adventure;
    }

    public File write()
    {
        Document doc = createXML();

        return save(doc);
    }

	private Document createXML()
	{
		Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("adventure");
        root.addAttribute("startScene", adventure.getStart().getId() + "");
        root.addElement("name").setText(adventure.getName());
        root.addElement("id").setText(adventure.getId());

        Element assertions = root.addElement("assertions");
        assertions.addCDATA(adventure.getAssertions());
        
        Element scenes = root.addElement("scenes");
        createSceneNodes(scenes);
        
		return doc;
	}

	private void createSceneNodes(Element scenes)
	{
		for (Scene s : adventure.getScenes()) {
            StringBuilder idsOfFromScenes = new StringBuilder("");
            for (Scene f : s.getScenesFrom()) {
                idsOfFromScenes.append(f.getId());
                idsOfFromScenes.append(",");
            }
            if (idsOfFromScenes.length() > 0) {
                idsOfFromScenes.deleteCharAt(idsOfFromScenes.length() - 1);
            }
            Element sceneNode = scenes.addElement("scene").
                addAttribute("id", s.getId()+"").
                addAttribute("isEnd",s.isEnd()+"").
                addAttribute("name", s.getName()).
                addAttribute("tags", s.getTags()).
                addAttribute("imageFilePath", s.getImageFile() == null ? "" : s.getImageFile().getAbsolutePath()).
                addAttribute("from", idsOfFromScenes.toString());
            
            if(s.getType().equals(SceneType.combat)) {
            	Element c = sceneNode.addElement("combat").addAttribute("type", s.getCombat().getType().toString());
            	for (Fighter f : s.getCombat().getEnemies()) {
					c.addElement("enemy").
					addAttribute("name", f.getName()).
					addAttribute("skill", f.getCombatSkillLevel()+"").
					addAttribute("stamina", f.getStamina()+"").
					addAttribute("damage", f.getDamage()+"");
				}
            } else if(s.getType().equals(SceneType.skillTest)) {
            	sceneNode.addElement("skill-test")
            		.addAttribute("name", s.getSkillToTest().getName());
            }
            
            Element e = sceneNode.addElement("text");
			e.addCDATA(s.getText());
			
			Element code = sceneNode.addElement("code");
			code.addCDATA(s.getCode());
            
            for (IPath p : s.getPaths()) {
                sceneNode.addElement("path").
                    addAttribute("toScene", p.getTo() == null ? "" : p.getTo().getId() + "").
                    addAttribute("order", p.getOrder()+"").
                    setText(p.getText());
            }
        }
	}

	private File save(Document doc)
	{
		try {
            if (!saveFile.getName().endsWith(".adv.xml")) {
                saveFile = new File(saveFile.getAbsolutePath() + ".adv.xml");
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("ISO-8859-1");
            format.setNewlines(true);
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLWriter writer = new XMLWriter(new FileWriter(saveFile), format);
            
            writer.write(doc);
            writer.close();

            return saveFile;
        } catch (Exception e) {
            throw new AdventureException("Error writing adventure", e);
        }
	}
}
