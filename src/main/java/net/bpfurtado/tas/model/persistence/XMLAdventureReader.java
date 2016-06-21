/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 03/10/2005 17:36:44                                                          
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.SceneType;
import net.bpfurtado.tas.model.Skill;
import net.bpfurtado.tas.model.combat.Combat;
import net.bpfurtado.tas.model.combat.CombatType;
import net.bpfurtado.tas.model.combat.Fighter;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * @author Bruno Patini Furtado
 */
public class XMLAdventureReader
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(XMLAdventureReader.class);

    private Adventure adventure;
    private Document xmlDocument;

    public XMLAdventureReader()
    {
        adventure = new Adventure();
    }

    public Adventure read(String adventureFileName)
    {
        readXMLDocument(adventureFileName);
        init();
        return adventure;
    }

    public Adventure read(File adventureFile)
    {
        createXMLDocument(adventureFile);
        init();
        return adventure;
    }

    private void init()
    {
        adventure.setName(((Node) xmlDocument.selectNodes("/adventure/name").iterator().next()).getText());
        // adventure.setId(((Node) xmlDocument.selectNodes("/adventure/id").iterator().next()).getText());

        Node assertionsNode = xmlDocument.selectSingleNode("/adventure/assertions");
        // to be compatible with old project files
        if (assertionsNode != null) {
            adventure.setAssertions(assertionsNode.getText());
        }
        readScenes();
    }

    private void readXMLDocument(String adventureFileName)
    {
        File adventureFile = new File(adventureFileName);
        if (!adventureFile.exists()) {
            throw new AdventureReaderException("The file [" + adventureFileName + "] does not exist");
        }
        createXMLDocument(adventureFile);
    }

    private void createXMLDocument(File file)
    {
        SAXReader xmlReader = new SAXReader();
        try {
            xmlDocument = xmlReader.read(file);
        } catch (DocumentException e) {
            throw new AdventureReaderException("Error reading XML document", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void readScenes()
    {
        Node startNode = xmlDocument.selectSingleNode("//scene[@id='0']");
        Scene start = adventure.getStart();
        loadSceneAttributes(startNode, start.getId());

        Collection<Scene> allScenes = new LinkedList<Scene>();

        List<Node> scenesNodes = new LinkedList(xmlDocument.selectNodes("//scene"));
        for (Node node : scenesNodes) {
            allScenes.add(loadSceneAttributes(node, Integer.parseInt(node.valueOf("@id"))));
        }

        findAllTos(start, allScenes);

        // For the ones not visited, because can't be reached from the start scene.
        for (Scene s : allScenes) {
            findAllTos(s, null);
        }
    }

    private Scene loadSceneAttributes(Node n, int id)
    {
        Scene s = null;
        if (id == 0) {
            s = adventure.getStart();
        } else {
            s = adventure.createScene(id, Boolean.valueOf(n.valueOf("@isEnd")));
        }
        s.setImageId(n.valueOf("@imageId"));
        s.setName(n.valueOf("@name"));
        s.setTags(n.valueOf("@tags"));
        s.setText(n.selectSingleNode("./text").getText());

        try {
            s.setCode(n.selectSingleNode("./code").getText());
            if (s.getCode() == null) {
                s.setCode("");
            }
        } catch (NullPointerException npe) {
            logger.warn("No code node");
        }

        loadCombat(n, s);
        loadSkillTest(n, s);

        return s;
    }

    private void loadSkillTest(Node n, Scene s)
    {
        Node cn = n.selectSingleNode("skill-test");
        if (cn == null) {
            return;
        }
        String name = cn.valueOf("@name");
        s.setType(SceneType.skillTest);
        s.setSkillToTest(new Skill(name));
    }

    private void loadCombat(Node n, Scene s)
    {
        Node cn = n.selectSingleNode("combat");
        if (cn == null) {
            return;
        }

        Combat c = new Combat();
        String type = cn.valueOf("@type");
        c.setType(CombatType.fromPersistentRepr(type));

        List<Node> enemyNodes = cn.selectNodes("./enemy");
        for (Node en : enemyNodes) {
            Fighter fighter = new Fighter(en.valueOf("@name"), Integer.valueOf(en.valueOf("@skill")),
                    Integer.valueOf(en.valueOf("@stamina")));
            fighter.setDamage(Integer.valueOf(en.valueOf("@damage")));
            c.add(fighter);
        }

        s.setType(SceneType.combat);
        s.setCombat(c);
    }

    /**
     * @param scene
     * @param scenesNotScannedYet
     *            Just to keep the scenes not yet visited.
     */
    @SuppressWarnings("unchecked")
    private void findAllTos(Scene scene, Collection<Scene> scenesNotScannedYet)
    {
        if (scenesNotScannedYet != null) {
            scenesNotScannedYet.remove(scene);
        }

        Node sceneNode = xmlDocument.selectSingleNode("//scene[@id='" + scene.getId() + "']");
        List<Node> pathNodes = sceneNode.selectNodes("./path");
        int i = 0;
        logger.debug("Scene=" + scene + ", pathNodes.sz=" + pathNodes.size());
        for (Node pathNode : pathNodes) {
            logger.debug(i);
            IPath p = scene.createPath(pathNode.getText());
            String orderStr = pathNode.valueOf("@order");
            if (orderStr == null || orderStr.length() == 0) {
                p.setOrder(i++);
            }
            String idToStr = pathNode.valueOf("@toScene");
            if (!GenericValidator.isBlankOrNull(idToStr)) {
                Scene to = adventure.getScene(Integer.parseInt(idToStr));
                boolean hadScenesFrom = !to.getScenesFrom().isEmpty();
                p.setTo(to);

                logger.debug(p);

                if (!hadScenesFrom) {
                    logger.debug("BEFORE RECURSION: to=" + to);
                    logger.debug("BEFORE RECURSION: all=" + scenesNotScannedYet);
                    if (scenesNotScannedYet.contains(to)) {
                        findAllTos(to, scenesNotScannedYet);
                    }
                }
            } else {
                logger.debug(p);
            }
        }
    }

    @SuppressWarnings("unused")
    private String format(String text)
    {
        StringTokenizer stk = new StringTokenizer(text, "\n");
        StringBuilder buffer = new StringBuilder();
        while (stk.hasMoreTokens()) {
            String line = stk.nextToken().trim();
            buffer.append(line);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public static void main(String[] args) throws Exception
    {
        new XMLAdventureReader().read("conf/adventure.xml");
    }
}
