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
package net.bpfurtado.tas.builder.scenespanel;

import javax.swing.JPanel;

import net.bpfurtado.tas.builder.scenespanel.ScenesListController.SortBy;

public interface ScenesListControllerListener
{
    void searchFieldUpdated(String filterExp);
    JPanel getPanel();
    boolean isPanelVisible();
    void sort(SortBy by);

    void focusOnList();

    void set(FilterScenesFieldsHolder holder);
}
