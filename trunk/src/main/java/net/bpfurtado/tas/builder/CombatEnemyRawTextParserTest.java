/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 01/07/2009 17:28:37
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

package net.bpfurtado.tas.builder;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.model.combat.Fighter;

import org.junit.Test;

public class CombatEnemyRawTextParserTest
{
    @Test
    public void test()
    {
        String fighterRawText = "Skeleton Warrior III          \n   SKIll   11  sTAmina  \n \n 9 \n";
        Fighter fighter = new CombatEnemyRawTextParser().parse(fighterRawText);

        assertEquals("Skeleton Warrior III", fighter.getName());
        assertEquals(11, fighter.getCombatSkillLevel());
        assertEquals(9, fighter.getStamina());
    }
}

class CombatEnemyRawTextParser
{
    Pattern skillRegex = Pattern.compile("skill +(\\d+)");
    Pattern staminaRegex = Pattern.compile("stamina +(\\d+)");

    public Fighter parse(String text)
    {
        text = text.replaceAll("\n", "");
        int skillIdx = text.toLowerCase().indexOf("skill");
        if (skillIdx == -1) {
            throw new AdventureException("Could not find Enemy(NAME) on the selected text");
        }

        String name = text.substring(0, skillIdx).trim();

        text = text.toLowerCase();
        int skill = 0, stamina = 0;

        Matcher m = skillRegex.matcher(text);
        if (m.find()) {
            skill = Integer.parseInt(m.group(1));
        } else {
            throw new AdventureException("Could not find Enemy(SKILL) on the selected text");
        }

        m = staminaRegex.matcher(text);
        if (m.find()) {
            stamina = Integer.parseInt(m.group(1));
        } else {
            throw new AdventureException("Could not find Enemy(STAMINA) on the selected text");
        }

        return new Fighter(name, skill, stamina);
    }
}