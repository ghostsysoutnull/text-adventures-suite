package net.bpfurtado.tas;

import static net.bpfurtado.tas.TestUtils.createNewScene;
import static net.bpfurtado.tas.TestUtils.hasScenesInDepth;
import junit.framework.TestCase;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;

import org.apache.log4j.Logger;

public class PathLoopsTest extends TestCase
{
    private static Logger logger = Logger.getLogger(PathLoopsTest.class);

    public void testMain()
    {
        Adventure a = new Adventure();
        Scene start = a.getStart();

        IPath fromStartToHallway = start.createPath("to hallway");
        Scene hallway = a.createSceneFrom(fromStartToHallway);
        hallway.setName("Hallway");

        IPath fromStartToSteelDoor = start.createPath("to Steel Door");
        Scene steelDoor = a.createSceneFrom(fromStartToSteelDoor);
        steelDoor.setName("Steel Door");

        IPath sdToSword = steelDoor.createPath("take Sword");
        Scene sword = a.createSceneFrom(sdToSword);
        sword.setName("Sword");
        IPath sdToShield = steelDoor.createPath("take Shield");
        Scene shield = a.createSceneFrom(sdToShield);
        shield.setName("Shield");

        assertEquals(3, a.getNumberOfDepths());

        IPath fromHallwayToSD = hallway.createPath("to SD");
        fromHallwayToSD.setTo(steelDoor);

        assertEquals(4, a.getNumberOfDepths());

        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, steelDoor, sword, shield);
        hasScenesInDepth(this, a, 3, sword, shield);

        IPath fromSDtoH = steelDoor.createPath("to Hallway");
        fromSDtoH.setTo(hallway);

        assertEquals(4, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield);
        hasScenesInDepth(this, a, 3, sword, shield);

        IPath fromHalltoOut = hallway.createPath("to out");
        Scene outside = a.createSceneFrom(fromHalltoOut);
        outside.setName("outside");

        assertEquals(4, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield, outside);
        hasScenesInDepth(this, a, 3, sword, shield, outside);

        IPath fromOutToFlorest = outside.createPath("to florest");
        Scene florest = a.createSceneFrom(fromOutToFlorest);
        florest.setName("florest");

        assertEquals(5, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield, outside);
        hasScenesInDepth(this, a, 3, sword, shield, outside, florest);
        hasScenesInDepth(this, a, 4, florest);

        IPath fromFtoShi = florest.createPath("FtoSH");
        fromFtoShi.setTo(shield);

        assertEquals(6, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield, outside);
        hasScenesInDepth(this, a, 3, sword, shield, outside, florest);
        hasScenesInDepth(this, a, 4, florest, shield);
        hasScenesInDepth(this, a, 5, shield);

        IPath fromHallToShi = hallway.createPath("Hallway to Shield");
        fromHallToShi.setTo(shield);

        assertEquals(6, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield, outside, shield);
        hasScenesInDepth(this, a, 3, sword, shield, outside, florest, shield);
        hasScenesInDepth(this, a, 4, florest, shield);
        hasScenesInDepth(this, a, 5, shield);

        IPath fromSWtoF = sword.createPath("from SW to F");
        fromSWtoF.setTo(florest);

        assertEquals(6, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield, outside, shield);
        hasScenesInDepth(this, a, 3, sword, shield, outside, florest, shield, florest);
        hasScenesInDepth(this, a, 4, florest, florest, shield, shield);
        hasScenesInDepth(this, a, 5, shield, shield);

        Scene read = createNewScene(a, shield, "read");
        assertEquals(7, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield, outside, shield);
        hasScenesInDepth(this, a, 3, sword, shield, outside, florest, shield, florest, read, read);
        hasScenesInDepth(this, a, 4, florest, florest, shield, shield, read, read);
        hasScenesInDepth(this, a, 5, shield, shield, read, read);
        hasScenesInDepth(this, a, 6, read, read);

        fromHalltoOut.goToNowhere();

        assertEquals(7, a.getNumberOfDepths());
        hasScenesInDepth(this, a, 0, start);
        hasScenesInDepth(this, a, 1, hallway, steelDoor);
        hasScenesInDepth(this, a, 2, hallway, steelDoor, sword, shield, shield);
        hasScenesInDepth(this, a, 3, sword, shield, shield, florest, read, read);
        hasScenesInDepth(this, a, 4, florest, shield, read, read);
        hasScenesInDepth(this, a, 5, shield, read);
        hasScenesInDepth(this, a, 6, read);
    }

    @SuppressWarnings("unused")
    private void printDepths(Adventure a)
    {
        for (int i = 0; i < a.getNumberOfDepths(); i++) {
            logger.debug(i + " : " + a.getScenesFromDepth(i));
        }
    }
}
