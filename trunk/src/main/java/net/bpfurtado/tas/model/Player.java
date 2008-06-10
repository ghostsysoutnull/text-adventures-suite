/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
 * Created on 18/10/2005 23:14:14
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import net.bpfurtado.tas.AdventureException;
import net.bpfurtado.tas.model.combat.Fighter;

/**
 * @author Bruno Patini Furtado
 */
public class Player extends Fighter
{
    private static final Random rand = new Random();

    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<String, Skill> skills = new HashMap<String, Skill>();

    private Skill luck = new Skill("Luck", rand.nextInt(7) + 6);

    public Player(String name)
    {
        super(name, rand.nextInt(7) + 6, rand.nextInt(13) + 12);

        add(getCombatSkill());
        add(getLuck());
    }

    private void add(Skill s)
    {
        skills.put(s.getName(), s);
    }

    public Player(String name, int combatSkill, int stamina)
    {
        super(name, combatSkill, stamina);
    }

    public void addSkill(String name, int level)
    {
        add(new Skill(name, level));
    }

    public Skill skill(String skillName)
    {
        return skills.get(skillName);
    }

    public Skill getLuck()
    {
        return luck;
    }

    public void setLuck(int newLuckValue)
    {
        luck.setLevel(newLuckValue);
    }

    public void setAttribute(String name, String value)
    {
        String old = (String) attributes.get(name);
        attributes.put(name, value);
        fire(new PlayerEvent(name, name + " was " + old + ", now is " + value));
    }

    public void addAttribute(String name)
    {
        attributes.put(name, name);
        fire(new PlayerEvent(name, "Attribute " + name + " added"));
    }

    public void addAttribute(String name, int value)
    {
        attributes.put(name, value);
        fire(new PlayerEvent(name, "Attribute " + name + " added with [" + value + "]"));
    }

    public void addAttribute(String name, String value)
    {
        attributes.put(name, value);
        fire(new PlayerEvent(name, "Attribute " + name + " added with [" + value + "]"));
    }

    public void removeAttribute(String name)
    {
        attributes.remove(name);
        fire(new PlayerEvent(name, "Attribute " + name + " removed"));
    }

    public int getIntValue(String name)
    {
        return (Integer) attributes.get(name);
    }

    public void decIntValue(String name, int amount)
    {
        sumToIntValue(name, -1 * amount);
    }

    public void incIntValue(String name, int amount)
    {
        sumToIntValue(name, amount);
    }

    private void sumToIntValue(String name, int amount)
    {
        Integer increasedValue = null;
        Object value = recoverValue(name);
        try {
            increasedValue = Integer.parseInt((String) value) + amount;
        } catch (ClassCastException cce) {
            increasedValue = ((Integer) value) + amount;
        } catch (Exception e) {
            throw new AdventureException(e);
        }
        attributes.put(name, increasedValue);

        fire(new PlayerEvent(name + " has now the value of " + increasedValue));
    }

    private Object recoverValue(String name)
    {
        Object value = attributes.get(name);
        if (value == null) {
            attributes.put(name, 0);
        }
        return attributes.get(name);
    }

    public boolean has(String attribute)
    {
        return attributes.containsKey(attribute);
    }

    // START: dec & inc the three main attributes

    public void incStamina(int v)
    {
        setStamina(getStamina() + v);
    }

    public void decStamina(int v)
    {
        setStamina(getStamina() - v);
    }

    public void incLuck(int v)
    {
        luck.inc(v);
    }

    public void decLuck(int v)
    {
        luck.dec(v);
    }

    public void incCombatSkill(int value)
    {
        getCombatSkill().inc(value);
    }

    public void decCombatSkill(int value)
    {
        getCombatSkill().dec(value);
    }

    public void incDamage(int v)
    {
        setDamage(getDamage() + v);
    }

    public void decDamage(int v)
    {
        setDamage(getDamage() - v);
    }

    // END: dec & inc the three main attributes

    public String getAttribute(String name)
    {
        return (String) attributes.get(name);
    }

    public Set<Entry<String, Object>> getAttributesEntrySet()
    {
        return attributes.entrySet();
    }

    public Iterable<Skill> getSkills()
    {
        return skills.values();
    }

    public String getName()
    {
        return "Player";
    }

    @Override
    public String toString()
    {
        return "[Player: " + attributes + "]";
    }

    public static void main(String[] args)
    {
        Player p = new Player("H");
        p.incIntValue("coins", 30);
    }
}
