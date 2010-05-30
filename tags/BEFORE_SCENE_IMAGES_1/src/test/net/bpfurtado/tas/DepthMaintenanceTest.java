package net.bpfurtado.tas;


import junit.framework.TestCase;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.PathDepth;
import net.bpfurtado.tas.model.Scene;

import org.apache.log4j.Logger;

/**
 * Created using Test Driven Development
 * Caso de teste 22-04-2006 (top of page)
 * @author Bruno Patini Furtado
 */
public class DepthMaintenanceTest extends TestCase
{
    @SuppressWarnings("unused")
    static final Logger logger = Logger.getLogger(DepthMaintenanceTest.class);

    public void testMain()
    {
        Adventure a = new Adventure();
        Scene start = a.getStart();

        assertEquals(1, a.getNumberOfDepths());
        assertEquals(1, a.getNumberOfScenesFromDepth(0));

        Scene startExpected = a.getScenesFromDepth(0).iterator().next();
        assertEquals(start, startExpected);

        // - S1
        IPath pStartTo1 = start.createPath("to 1");
        Scene s1 = a.createSceneFrom(pStartTo1);
        s1.setName("S1");

        assertEquals(2, a.getNumberOfDepths());
        assertEquals(1, a.getNumberOfScenesFromDepth(1));

        Scene s1Expected = a.getScenesFromDepth(1).iterator().next();
        assertEquals(s1, s1Expected);

        // - S2
        IPath pStartTo2 = start.createPath("to 2");
        Scene s2 = a.createSceneFrom(pStartTo2);
        s2.setName("S2");

        assertEquals(2, a.getNumberOfDepths());
        assertEquals(2, a.getNumberOfScenesFromDepth(1));

        assertTrue(a.getScenesFromDepth(1).contains(s1));
        assertTrue(a.getScenesFromDepth(1).contains(s2));

        // - S11
        IPath pS1toS11 = s1.createPath("to s11");
        Scene s11 = a.createSceneFrom(pS1toS11);
        s11.setName("S11");

        assertEquals(3, a.getNumberOfDepths());
        assertEquals(1, a.getNumberOfScenesFromDepth(2));

        // - S12
        IPath pS1toS12 = s1.createPath("to s12");
        Scene s12 = a.createSceneFrom(pS1toS12);
        s12.setName("S12");

        assertEquals(3, a.getNumberOfDepths());
        assertEquals(2, a.getNumberOfScenesFromDepth(2));

        // - Start to S12
        IPath pStartToS12 = start.createPath("Start to s12");
        pStartToS12.setTo(s12);

        int s12PathDepthsSize = 0;
        for (@SuppressWarnings("unused") PathDepth p : s12.getPathDepths()) s12PathDepthsSize++;
        assertEquals(2, s12PathDepthsSize);

        assertEquals(3, a.getNumberOfDepths());
        TestUtils.hasScenesInDepth(this, a, 1, s1, s2, s12);
        TestUtils.hasScenesInDepth(this, a, 2, s11, s12);

        // - S_1.2.1
        IPath pS12toS121 = s12.createPath("to S_1.2.1");
        Scene s121 = a.createSceneFrom(pS12toS121);
        s121.setName("S121");
        logger.debug("Created " + s121);

        assertEquals(4, a.getNumberOfDepths());
        TestUtils.hasScenesInDepth(this, a, 1, s1, s2, s12);
        TestUtils.hasScenesInDepth(this, a, 2, s11, s12, s121);
        TestUtils.hasScenesInDepth(this, a, 3, s121);
        
        TestUtils.hasNumberOfPathDepths(this, 1, s1, s2, s11);
        TestUtils.hasNumberOfPathDepths(this, 2, s12, s121);

        // - Remove path 'Start to S12'
        logger.debug("\nRemove path \'Start to S12\'");
        
        start.remove(pStartToS12); 
        TestUtils.hasScenesInDepth(this, a, 1, s1, s2);
        TestUtils.hasScenesInDepth(this, a, 2, s11, s12);
        TestUtils.hasScenesInDepth(this, a, 3, s121); 
        
        PathDepthPrinter printer = new PathDepthPrinter();
		printer.printPathDepthsTree(s1);
		printer.printPathDepthsTree(s11);
		printer.printPathDepthsTree(s12);
        printer.printPathDepthsTree(s2);
        printer.printPathDepthsTree(s121);
        
        TestUtils.hasNumberOfPathDepths(this, 1, s1, s2, s11, s12, s121);
        
        // - S_1.2.1.1
        IPath pS121toS1211 = s121.createPath("to S_1.2.1.1");
        
        Scene s1211 = a.createSceneFrom(pS121toS1211);
        s1211.setName("S1211");

        assertEquals(5, a.getNumberOfDepths());
        TestUtils.hasScenesInDepth(this, a, 1, s1, s2);
        TestUtils.hasScenesInDepth(this, a, 2, s11, s12);
        TestUtils.hasScenesInDepth(this, a, 3, s121);
        TestUtils.hasScenesInDepth(this, a, 4, s1211);

        for (int i = 0; i < a.getNumberOfDepths(); i++) {
            TestUtils.printScenesFromDepth(a, i);
        }
    }
}
