package net.bpfurtado.tas;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;

import org.apache.log4j.Logger;

public class TestUtils 
{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(TestUtils.class);

	@SuppressWarnings("static-access")
	static void hasNumberOfPathDepths(TestCase c, int expectedNumberOfPathDepths, Scene... scenes)
	{
		DepthMaintenanceTest.logger.debug("Expected: " + expectedNumberOfPathDepths);
		for (Scene s : scenes) {
			DepthMaintenanceTest.logger.debug(s + ": " + s.getPathDepths());
			c.assertEquals(expectedNumberOfPathDepths, s.getPathDepthsSize());
		}
	}

	@SuppressWarnings("static-access")
	static void hasScenesInDepth(TestCase testCase, Adventure a, int depthNumber, Scene... scenes)
	{
	    LinkedList<Scene> scenesThisDepthMustHave = new LinkedList<Scene>(Arrays.asList(scenes));
	    logger.debug("must have: " + scenesThisDepthMustHave);
	    
	    Collection<Scene> scenesFromDepth = new LinkedList<Scene>(a.getScenesFromDepth(depthNumber));
	    logger.debug("scenesFromDepth[" + depthNumber + "]: " + scenesFromDepth);
	    
	    testCase.assertEquals(scenesThisDepthMustHave.size(), scenesFromDepth.size());
	    
	    for (Scene mustHaveScene : scenesThisDepthMustHave) {
			logger.debug("mustHaveScene: " + mustHaveScene);
			testCase.assertTrue(scenesFromDepth.remove(mustHaveScene));
		}
	    testCase.assertTrue(scenesFromDepth.isEmpty());
	}

	static void printScenesFromDepth(Adventure a, int depthNumber)
	{
	    logger.debug("Depth number: " + depthNumber);
	    for (Scene s : a.getScenesFromDepth(depthNumber)) {
	        logger.debug("\t" + s.getName());
	    }
	}

	@SuppressWarnings("unused")
	static Scene createNewScene(Adventure a, Scene origin, String name)
	{
		IPath path = origin.createPath("to " + name);
		Scene scene = a.createSceneFrom(path);
		scene.setName(name);
		return scene;
	}
}
