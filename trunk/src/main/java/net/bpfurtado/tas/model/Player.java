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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
	private static final Random r = new Random();

	private Map<String, Object> attributes = new HashMap<String, Object>();
	private Collection<Skill> skills = new LinkedList<Skill>();

	private int luck;

	public Player(String name)
	{
		super(name, r.nextInt(7) + 6, r.nextInt(13) + 12);
		
		skills.add(getCombatSkill());
		
		setLuck(r.nextInt(7) + 6);
	}

	public Player(String name, int combatSkill, int stamina)
	{
		super(name, combatSkill, stamina);
	}
	
	public void addSkill(String name, int level) 
	{
		skills.add(new Skill(name, level));
	}

	public int getLuck()
	{
		return luck;
	}

	public void setLuck(int luck)
	{
		this.luck = luck;
	}

    public void setAttribute(String name, String value)
    {
        attributes.put(name, value);
    }

    public void addAttribute(String name)
	{
		attributes.put(name, name);
	}

    public void addAttribute(String name, int value)
    {
        attributes.put(name, value);
    }

    public void addAttribute(String name, String value)
    {
        attributes.put(name, value);
    }

    public void removeAttribute(String name)
    {
        attributes.remove(name);
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

    public void decStamina(int v)
	{
		setStamina(getStamina() - v);
	}

    public void decSkill(int v)
	{
		setCombatSkill(getCombatSkillLevel() - v);
	}

    public void decDamage(int v)
	{
		setDamage(getDamage() - v);
	}

    public void decLuck(int v)
	{
		setLuck(getLuck() - v);
	}

    public void incStamina(int v)
	{
		setStamina(getStamina() + v);
	}

    public void incSkill(int v)
	{
		setCombatSkill(getCombatSkillLevel() + v);
	}

    public void incDamage(int v)
	{
		setDamage(getDamage() + v);
	}

    public void incLuck(int v)
	{
		setLuck(getLuck() + v);
	}

    // END: dec & inc the three main attributes

    public String getValue(String name)
    {
        return (String) attributes.get(name);
    }

    public Set<Entry<String, Object>> getAttributesEntrySet()
    {
        return attributes.entrySet();
    }
    
	public Iterable<Skill> getSkills()
	{
		return skills;
	}

	public String getName()
	{
		return "Player";
	}

	@Override
	public String toString()
	{
		return "[Player: "+attributes+"]";
	}
	
	public static void main(String[] args)
	{
		Player p = new Player("H");
		p.incIntValue("coins", 30);
	}
}
