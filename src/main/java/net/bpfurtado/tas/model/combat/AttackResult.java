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

    boolean instantKill = false;

    private AttackResultType type;

    public AttackResult(int dice1, int dice2, Fighter f)
    {
        this.f = f;
        this.dice1 = dice1;
        this.dice2 = dice2;
        if (dice1 == dice2 && dice1 == 1) {
            instantKill = true;
        }
    }

    public int sum()
    {
        return dice1 + dice2 + f.getCombatSkillLevel();
    }

    public String toString()
    {
        int sum = dice1 + dice2 + f.getCombatSkillLevel();
        return "[" + dice1 + "] + [" + dice2 + "] + " + f.getCombatSkillLevel() + " = " + sum;
    }

    public String roundInfoToString(int round)
    {
        String roundStr = "[" + round + "] ";
        String nameAndDice = f.getName() + ": " + toString();
        String stamina = ", stamina = " + f.getStamina();

        return roundStr + nameAndDice + stamina;
    }

    public void defineType(AttackResult enemy)
    {
        if (sum() > enemy.sum() || isInstantKill()) {
            setType(AttackResultType.won);
            enemy.setType(AttackResultType.loose);
        } else if (sum() < enemy.sum()) {
            setType(AttackResultType.loose);
            enemy.setType(AttackResultType.won);
        } else {
            setType(AttackResultType.draw);
            enemy.setType(AttackResultType.draw);
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

    public boolean isInstantKill()
    {
        return instantKill;
    }
}
