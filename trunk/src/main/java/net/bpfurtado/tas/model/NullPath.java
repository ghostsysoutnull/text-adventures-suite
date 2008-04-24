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

import org.apache.log4j.Logger;

public class NullPath implements IPath
{
	private static Logger logger = Logger.getLogger(NullPath.class);
	
	private Scene to;
	private Scene from;

	public NullPath(Scene to)
	{
		this.to = to;
		this.from = new Scene(-999);
	}

	public Scene getFrom()
	{
		return from;
	}

	public void goToNowhere()
	{
		
	}

	public boolean isVisible()
	{
		return false;
	}

	public void setFrom(Scene from)
	{
		
	}

	public void setVisible(boolean isVisible)
	{
		
	}

	public String getText()
	{
		return null;
	}

	public void setText(String text)
	{
	}

	public int getId()
	{
		return 0;
	}

	public void setId(int id)
	{
	}

	public Scene getTo()
	{
		return to;
	}

	public void setTo(Scene to)
	{
		this.to = to;
	}

	public String toStringShort()
	{
		return "[NullPath]";
	}

	@Override
	public String toString()
	{
		return toStringShort();
	}

	public int getOrder()
	{
		return 0;
	}

	public void setOrder(int i)
	{
		logger.warn("Should I be called?");
	}
}
