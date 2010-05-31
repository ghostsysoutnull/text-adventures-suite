/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 03/07/2009 19:59:47
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

import static junit.framework.Assert.assertEquals;

import java.io.File;

import net.bpfurtado.tas.Workspace;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.Game;
import net.bpfurtado.tas.model.GameImpl;
import net.bpfurtado.tas.model.Player;
import net.bpfurtado.tas.model.PlayerEvent;
import net.bpfurtado.tas.model.PlayerEventListener;
import net.bpfurtado.tas.model.Scene;
import net.bpfurtado.tas.model.Skill;

import org.junit.Test;

public class SaveGameManagerTest
{
    @Test
    public void writeAndRead()
    {
        Adventure a = new Adventure();
        a.setName("SaveGameManagerTest");
        Game game = new GameImpl(a);
        game.setCurrentScene(new Scene(1337));

        Player p = game.getPlayer();
        p.setAttribute("gold", "30");
        p.setAttribute("scroll", "fireBallSpell");

        p.incIntValue("gold", 120);
        assertEquals(Integer.parseInt(p.getAttribute("gold")), 150);

        p.setCombatSkill(12);
        p.setDamage(3);

        p.addSkill("swim", 8);

        MockSaveGameListener mock = new MockSaveGameListener();
        SaveGameManager m = new SaveGameManager(game, mock);
        File file = m.save();
        System.out.println(file);

        SaveGame other = m.open(file, new MockPlayerEventListener());

        Player op = other.getPlayer();
        assertEquals(p.getAttribute("gold"), op.getAttribute("gold"));
        assertEquals(p.getAttribute("scroll"), op.getAttribute("scroll"));
        assertEquals(p.getCombatSkillLevel(), op.getCombatSkillLevel());
        assertEquals(p.getDamage(), op.getDamage());

        assertEquals(op.skill("Luck"), p.skill("Luck"));
        assertEquals(op.skill("swim"), p.skill("swim"));
        assertEquals(op.skill("Combat"), p.skill("Combat"));
        
        assertEquals(op.skill("XCombat"), Skill.NULL_OBJECT);
    }
}

class MockPlayerEventListener implements PlayerEventListener
{
    public void receive(PlayerEvent ev)
    {
    }
}

class MockSaveGameListener implements SaveGameListener
{
    @Override
    public void fireOpenSavedGameEvent(Workspace workspace)
    {
    }

    @Override
    public Game open(String workspaceId)
    {
        return null;
    }

    @Override
    public void log(String msg)
    {
    }

    @Override
    public void openScene(Scene to, boolean execActions)
    {
    }
}
