/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com] - 2005
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.bpfurtado.tas.model.combat.Fighter;

public class RandomNPCGenerator
{
    private Random random = new Random();

    private List<String> npcsSuggestions = new LinkedList<String>();
    private List<String> races = new LinkedList<String>();

    public RandomNPCGenerator()
    {
        races.addAll(Arrays.asList("Human", "Dwarf", "Elf", "Orc"));

        npcsSuggestions = new LinkedList<String>();
        npcsSuggestions.add("Trool");
        npcsSuggestions.add("Elf");
        npcsSuggestions.add("Drow");
        npcsSuggestions.add("Dwarf");
        npcsSuggestions.add("Mage");
        npcsSuggestions.add("Human Warrior");
        npcsSuggestions.add("Human Thief");
        npcsSuggestions.add("Centaur");
        npcsSuggestions.add("Fish Soldier");
        npcsSuggestions.add("Snake warrior");
        npcsSuggestions.add("Necromancer");
        npcsSuggestions.add("Sorcerer");
        npcsSuggestions.add("Prince");
        npcsSuggestions.add("King");
        npcsSuggestions.add("Monk");
        npcsSuggestions.add("Druid");
        npcsSuggestions.add("Minotaur");
        npcsSuggestions.add("Demon");
        npcsSuggestions.add("Gargoile");
        npcsSuggestions.add("Mummy");
        npcsSuggestions.add("Goblin");
        npcsSuggestions.add("Orc");
        npcsSuggestions.add("Giant Bat");
        npcsSuggestions.add("Skeleton");
        npcsSuggestions.add("Ghoul");
    }

    public String generateName()
    {
        return npcsSuggestions.get(random.nextInt(npcsSuggestions.size()));
    }

    public Fighter generateFighter()
    {
        String name = generateName();
        if (name == "King" || name == "Prince") {
            name = races.get(random.nextInt(races.size())) + " " + name;
        }
        Fighter fighter = new Fighter(name, 4 + random.nextInt(9), 4 + random.nextInt(13));

        int i = random.nextInt(10);
        if (i == 0) {
            fighter.setDamage(1);
            return fighter;
        }

        int d = 2;
        if (i > 5) {
            d++;
        }
        if (i == 8) {
            d++;
        }
        fighter.setDamage(d);
        return fighter;
    }
}
