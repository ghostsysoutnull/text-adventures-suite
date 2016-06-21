/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 17/10/2005 23:06:44                                                          
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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Bruno Patini Furtado
 */
public class RandomPathNameGenerator
{
    private Random random = new Random();

    private RandomNPCGenerator npcGen = new RandomNPCGenerator();

    private List<String> pathsSuggestions = new LinkedList<String>();

    public RandomPathNameGenerator()
    {
        pathsSuggestions.add("to the florest");
        pathsSuggestions.add("to the hill");
        pathsSuggestions.add("to the mountain");
        pathsSuggestions.add("to the swamp");
        pathsSuggestions.add("to the river");
        pathsSuggestions.add("to the cave");
        pathsSuggestions.add("to the castle");
        pathsSuggestions.add("to the dark passage");
        pathsSuggestions.add("to the room");
        pathsSuggestions.add("to the corridor");
        pathsSuggestions.add("to the hallway");
        pathsSuggestions.add("to the gallery");

        pathsSuggestions.add("take the sword");
        pathsSuggestions.add("take the knife");
        pathsSuggestions.add("take the gem");
        pathsSuggestions.add("take the haste");

        for (int i = 0; i < 20; i++) {
            pathsSuggestions.add("fight with the " + npcGen.generateName());
        }
    }

    public String getSuggestion()
    {
        return pathsSuggestions.get(random.nextInt(pathsSuggestions.size()));
    }
}
