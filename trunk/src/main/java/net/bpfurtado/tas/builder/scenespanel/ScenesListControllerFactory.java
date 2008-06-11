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

import javax.swing.JList;
import javax.swing.JPanel;

import net.bpfurtado.tas.builder.Builder;

public class ScenesListControllerFactory
{
    private static final ScenesListControllerFactory INSTANCE = new ScenesListControllerFactory();

    public static ScenesListControllerResult create(Builder builder, boolean showButtons)
    {
        ScenesList scenesList = new ScenesList(builder, showButtons);

        ScenesListController scenesListController = new ScenesListController();
        scenesListController.add(scenesList);
        ScenesFilter scenesFilter = new ScenesFilter(builder, showButtons);
        scenesListController.add(scenesFilter);

        return INSTANCE.new ScenesListControllerResult(scenesListController.getPanel(), scenesList, scenesFilter);
    }

    private ScenesListControllerFactory()
    {

    }

    public class ScenesListControllerResult
    {
        public JPanel panel;
        public ScenesList scenesList;
        public ScenesFilter scenesFilter;

        public ScenesListControllerResult(JPanel panel, ScenesList scenesList, ScenesFilter scenesFilter)
        {
            this.panel = panel;
            this.scenesList = scenesList;
            this.scenesFilter = scenesFilter;
        }

        public ScenesList getScenesList()
        {
            return scenesList;
        }

        public void setScenesList(ScenesList list)
        {
            this.scenesList = list;
        }

        public JPanel getPanel()
        {
            return panel;
        }

        public void setPanel(JPanel panel)
        {
            this.panel = panel;
        }

        public JList getFilterList()
        {
            return scenesFilter.getList();
        }

        public ScenesFilter getScenesFilter()
        {
            return scenesFilter;
        }

        public void setScenesFilter(ScenesFilter scenesFilter)
        {
            this.scenesFilter = scenesFilter;
        }
    }
}
