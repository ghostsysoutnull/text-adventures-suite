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

import net.bpfurtado.tas.model.Player;

import org.apache.log4j.Logger;

public class AttackResult
{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(AttackResult.class);

	private int dice1;
	private int dice2;
	private Fighter f;

	private AttackResultType type;

	public AttackResult(int dice1, int dice2, Fighter f)
	{
		this.f = f;
		this.dice1 = dice1;
		this.dice2 = dice2;
	}
	
	/*public boolean relatesTo(Fighter other)
	{
		logger.debug("me: "+f+", other="+other+" = "+(f == other));
		return f == other;
	}*/

	public int sum()
	{
		if (dice1 == dice2) {
			if (dice1 == 1) {
				//use rule of instant kill?
			}
		}
		return dice1 + dice2 + f.getCombatSkillLevel();
	}

	public String toString()
	{
		int sum = dice1 + dice2 + f.getCombatSkillLevel();
		String string = "[" + dice1 + "] + [" + dice2 + "] + " + f.getCombatSkillLevel() + " = " + sum;
		//logger.debug(string);
		return string;
	}
	
	public String roundInfoToString(int round)
	{
		String roundStr = "[" + round + "] ";
		String nameAndDice = f.getName() + ": " + toString();
		String stamina = ", stamina = " + f.getStamina();
		
		return roundStr + nameAndDice + stamina;
	}

	public void defineType(AttackResult e)
	{
		if (sum() > e.sum()) {
			setType(AttackResultType.won);
			e.setType(AttackResultType.loose);
		} else if (sum() < e.sum()) {
			setType(AttackResultType.loose);
			e.setType(AttackResultType.won);
		} else {
			setType(AttackResultType.draw);
			e.setType(AttackResultType.draw);
		}
	}

	public void setType(AttackResultType type)
	{
		this.type = type;
	}

	public AttackResultType getType()
	{
		return type;
	}

	public boolean isPlayer()
	{
		return f instanceof Player;
	}
}

