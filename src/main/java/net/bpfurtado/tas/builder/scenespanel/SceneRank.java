/**
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]
 * Created on 01/05/2008 17:29:06
 * 
 * This file is part of LJColligo.
 * 
 * LJColligo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LJColligo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LJColligo.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Project page: http://sourceforge.net/projects/ljcolligo/
 */

package net.bpfurtado.tas.builder.scenespanel;

import net.bpfurtado.tas.model.Scene;

public class SceneRank implements Comparable<SceneRank>
{
    private Integer rank;
    private Scene scene;

    SceneRank(Scene s, int rank)
    {
        this.setRank(rank);
        this.setScene(s);
    }

    public SceneRank()
    {
    }

    @Override
    public String toString()
    {
        return getScene().getName() + " (" + getRank() + ")";
    }

    void setRank(Integer rank)
    {
        this.rank = rank;
    }

    Integer getRank()
    {
        return rank;
    }

    void setScene(Scene scene)
    {
        this.scene = scene;
    }

    Scene getScene()
    {
        return scene;
    }
    
    public int compareTo(SceneRank o)
    {
        return Integer.valueOf(o.getRank()).compareTo(getRank());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SceneRank) {
            SceneRank other = (SceneRank) obj;
            return rank.equals(other.rank);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return rank.hashCode() * 84;
    }
}