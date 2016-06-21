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

public enum SceneType {
    regular {
        public String toString()
        {
            return "Regular";
        }

        public int exactPathsNumberPermited()
        {
            return Integer.MAX_VALUE;
        }

        public boolean hasPathsNumberRestrictions()
        {
            return false;
        }
    },
    skillTest {
        public String toString()
        {
            return "Test some skill";
        }

        public int exactPathsNumberPermited()
        {
            return 2;
        }

        public boolean hasPathsNumberRestrictions()
        {
            return true;
        }
    },
    combat {
        public String toString()
        {
            return "Combat";
        }

        public int exactPathsNumberPermited()
        {
            return 1;
        }

        public boolean hasPathsNumberRestrictions()
        {
            return true;
        }
    },
    end {
        public String toString()
        {
            return "End";
        }

        public int exactPathsNumberPermited()
        {
            return 0;
        }

        public boolean hasPathsNumberRestrictions()
        {
            return true;
        }
    };

    public int exactPathsNumberPermited()
    {
        return 0;
    }

    public boolean hasPathsNumberRestrictions()
    {
        return false;
    }
}