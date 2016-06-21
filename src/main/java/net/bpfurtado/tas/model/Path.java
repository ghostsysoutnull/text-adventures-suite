/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 01/10/2005 17:01:34                                                          
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * @author Bruno Patini Furtado
 */
public class Path extends TextObject implements IPath
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(Path.class);

    private Scene from;
    private Scene to;

    private boolean isVisible = true;

    private int order = -1;

    public Path(int id, String text, Scene from)
    {
        super(id);
        setText(text);
        setFrom(from);

        order = from.getPaths().size();
    }

    /**
     * TODO Accept newTo==null?
     */
    public void setTo(Scene newTo)
    {
        if (to != null) {
            to.removeDepthsOf(this);
        }

        to = newTo;

        if (to == null) {
            return;
        }

        createPathDepthsToDestinyScene(this, from, to);

        /*
         * FIXME os from'scrollPane de uma scene virão das entidades PathDepth, refatorar isto em breve!
         */
        to.addFrom(from);
    }

    @SuppressWarnings("unchecked")
    private static void createPathDepthsToDestinyScene(Path path, Scene from, Scene to)
    {
        Collection<PathDepth> pathDepthsToTheNewTo = new LinkedList<PathDepth>();

        for (PathDepth pathDepthFrom : from.createPathDepthsCopy()) {
            // Eh volta? Já existe algum path em meu destino que leve ateh mim?
            if (pathDepthFrom.getPath().getTo().equals(from) && pathDepthFrom.getPath().getFrom().equals(to)) {
                logger.debug("### JUMP " + pathDepthFrom);
                continue;
            }
            logger.debug("### KEEP WITH " + pathDepthFrom);

            // Aqui embaixo q a cena vai para o nivel 3
            PathDepth newPathDepth = pathDepthFrom.createChild(path); // TODO pensar em
                                                                      // encapsulamento aqui.
            new CreatePathDepthsToDestinyScene().create(newPathDepth, from, to);
            pathDepthsToTheNewTo.add(newPathDepth);
        }
        to.add(pathDepthsToTheNewTo);
    }

    public void goToNowhere()
    {
        if (to != null) {
            to.removeDepthsOf(this);
        }
        to = null;
    }

    public Scene getTo()
    {
        return this.to;
    }

    public Scene getFrom()
    {
        return this.from;
    }

    public void setFrom(Scene from)
    {
        this.from = from;
    }

    public void setVisible(boolean isVisible)
    {
        this.isVisible = isVisible;
    }

    public boolean isVisible()
    {
        return this.isVisible;
    }

    public String toStringShort()
    {
        return "[" + to + "]";
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(13, 33).append(getId()).append(from).toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Path other = (Path) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(getId(), other.getId()).append(from, other.from).isEquals();
    }

    @Override
    public String toString()
    {
        return "[Path: id=" + getId() + ", from=" + from.getName() + ", to=" + to + "]";
    }
}
