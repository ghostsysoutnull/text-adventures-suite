/**
 * Created on 03/10/2005 21:54:21
 */
package net.bpfurtado.tas.model.persistence;

import net.bpfurtado.tas.model.Adventure;
import junit.framework.TestCase;

/**
 * @author Bruno Patini Furtado
 */
public class XMLAdventureReaderTest extends TestCase
{
    public void testMain()
    {
        Adventure a = new XMLAdventureReader().read("conf/adventure.xml");
        assertTrue(a.getStart().getText().startsWith("Voce acorda de manhã"));
    }
}
