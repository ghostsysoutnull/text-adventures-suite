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
package net.bpfurtado.tas.model;

import java.io.Serializable;

public class Skill implements Serializable
{
    private static final long serialVersionUID = 6619915916576611215L;

    public static Skill NULL_OBJECT = new Skill(null, "NULL", 0);

    private Player player;

    private String name;
    private int level;

    /**
     * To Deserialize
     */
    public Skill()
    {
    }

    /**
     * to read from the XML TODO verifi how that instance is used later.
     */
    public Skill(String name)
    {
        setName(name);
    }

    public Skill(Player p, String name)
    {
        setName(name);
        this.player = p;
    }

    public Skill(Player p, String name, int level)
    {
        this(p, name);
        setLevel(level);
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public void dec(int v)
    {
        inc(-v);
    }

    public void inc(int v)
    {
        int old = level;
        level += v;
        player.fireEvent(this, old);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Skill) {
            Skill other = (Skill) obj;
            return other.name.equals(name) && other.level == level;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "[Skill: name=" + name + ", level=" + level + "]";
    }
}
