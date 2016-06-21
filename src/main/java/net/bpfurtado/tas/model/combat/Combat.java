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
package net.bpfurtado.tas.model.combat;

import java.util.LinkedList;
import java.util.List;

public class Combat
{
    private List<Fighter> fighters = new LinkedList<Fighter>();
    private CombatType type = CombatType.oneAtATime;

    /**
     * to easy the loading from the xml file we just use the id here. To prevent us from loading all scenes
     * before building the combat objects.
     */
    private int sucessDestinyId;

    public void add(Fighter fighter)
    {
        fighters.add(fighter);
    }

    public List<Fighter> getEnemies()
    {
        return fighters;
    }

    public int getSucessDestinyId()
    {
        return sucessDestinyId;
    }

    public void setSucessDestinyId(int sucessDestinyId)
    {
        this.sucessDestinyId = sucessDestinyId;
    }

    public CombatType getType()
    {
        return type;
    }

    public void setType(CombatType t)
    {
        this.type = t;
    }

    public void remove(Fighter enemy)
    {
        fighters.remove(enemy);
    }
}
