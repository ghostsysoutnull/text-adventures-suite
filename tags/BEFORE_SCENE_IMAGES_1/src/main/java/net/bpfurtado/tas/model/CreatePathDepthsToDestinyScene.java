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

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class CreatePathDepthsToDestinyScene
{
	private static Logger logger = Logger.getLogger(CreatePathDepthsToDestinyScene.class);
	
	private Collection<Scene> visited = new LinkedList<Scene>();

	void create(PathDepth newPathDepth, Scene from, Scene to)
	{
		if (visited.contains(to)) {
			return;
		}

		visited.add(to);

		for (IPath p : to.getPaths()) {
			logger.debug(p.getTo() + "=" + from);
			if (p.getTo() == null) {
				continue;
			}
			if (p.getTo().equals(from)) {
				logger.debug("Continue");
				continue;
			} else {
				logger.debug("KEEP GOING");
			}
			PathDepth child = newPathDepth.createChild(p);
			p.getTo().add(child); // TODO pensar em encapsulamento aqui.
			create(child, to, p.getTo());
		}
	}
}
