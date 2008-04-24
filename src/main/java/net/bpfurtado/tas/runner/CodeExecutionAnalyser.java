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
package net.bpfurtado.tas.runner;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.bpfurtado.tas.model.Game;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

public class CodeExecutionAnalyser
{
	private static Logger logger = Logger.getLogger(CodeExecutionAnalyser.class);

	private static Random rnd = new Random();

	public static void main(String[] args)
	{
		System.out.println(rnd.nextInt(10));
	}

	@SuppressWarnings("unchecked")
	public List<PostCodeExecutionAction> analyseCode(Game game, String code, String currentSceneText)
	{
		List<PostCodeExecutionAction> actions = new LinkedList<PostCodeExecutionAction>();
		Interpreter interpreter = new Interpreter();
		try {
			interpreter.set("text", currentSceneText);
			
			interpreter.set("rnd", rnd);
			interpreter.set("originalText", game.getCurrentScene().getOriginalText());
			interpreter.set("origText", game.getCurrentScene().getOriginalText());

			LinkedList<Integer> pathsToHide = new LinkedList<Integer>();
			interpreter.set("pathsToHide", pathsToHide);

			interpreter.set("player", game.getPlayer());

			interpreter.eval(code);

			logger.debug(game.getPlayer());

			Integer go = (Integer) interpreter.get("go");
			if (go != null) {
				logger.debug("will go to " + go);
				actions.add(new SwitchSceneAction(go));
			}

			pathsToHide = (LinkedList<Integer>) interpreter.get("pathsToHide");
			logger.debug(pathsToHide);

			if(!pathsToHide.isEmpty()) {
				actions.add(new HidePaths(pathsToHide));
			}

			String text = (String) interpreter.get("text");
			if (text != null && text.trim().length() > 0) {
				game.getCurrentScene().setText(text);
			}

		} catch (EvalError e1) {
			throw new BadSceneCodeException("Bad Scene code, call the scene author :)", e1);
		}
		return actions;
	}
}
