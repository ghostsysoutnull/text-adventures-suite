package net.bpfurtado.tas;

import static net.bpfurtado.tas.TestUtils.createNewScene;
import static org.junit.Assert.assertEquals;
import net.bpfurtado.tas.model.Adventure;
import net.bpfurtado.tas.model.DepthManager;
import net.bpfurtado.tas.model.IPath;
import net.bpfurtado.tas.model.Scene;

import org.junit.Test;

public class FindFirstDepthOfScene
{
    @Test
    public void find()
    {
        Adventure a = new Adventure();
        Scene start = a.getStart();
        assertEquals(0, DepthManager.getInstance().getFirstDepthOfScene(start));

        Scene s1 = createNewScene(a, start, "1");
        assertEquals(1, DepthManager.getInstance().getFirstDepthOfScene(s1));

        Scene s2 = createNewScene(a, start, "2");
        assertEquals(1, DepthManager.getInstance().getFirstDepthOfScene(s2));

        Scene s11 = createNewScene(a, s2, "s11");
        assertEquals(2, DepthManager.getInstance().getFirstDepthOfScene(s11));

        IPath p = start.createPath("from S to s11");
        p.setTo(s11);
        assertEquals(1, DepthManager.getInstance().getFirstDepthOfScene(s11));
    }
}
