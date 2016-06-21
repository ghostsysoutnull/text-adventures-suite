package net.bpfurtado.tas;

import net.bpfurtado.tas.model.PathDepth;
import net.bpfurtado.tas.model.Scene;

import org.apache.log4j.Logger;

public class PathDepthPrinter
{
    private static Logger logger = Logger.getLogger(PathDepthPrinter.class);

    private String tabs = "";

    public void printPathDepthsTree(Scene start)
    {
        tabs = "";
        for (PathDepth pd : start.getPathDepths()) {
            printChildren(pd);
        }
    }

    private void printChildren(PathDepth parent)
    {
        logger.debug("Parent: " + parent);
        tabs += "\t";
        for (PathDepth pd : parent.children()) {
            logger.debug(tabs + pd);
        }
        tabs = tabs.substring(tabs.length() - 1);
    }
}
