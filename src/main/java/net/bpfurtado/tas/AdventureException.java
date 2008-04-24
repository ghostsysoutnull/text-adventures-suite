/**                                                                           
 * Created by Bruno Patini Furtado [http://bpfurtado.livejournal.com]         
 * Created on 06/10/2005 17:06:55                                                          
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

package net.bpfurtado.tas;

/**
 * @author Bruno Patini Furtado
 */
public class AdventureException extends RuntimeException
{
	private static final long serialVersionUID = -8485696600334757699L;

	public AdventureException()
    {
        super();
    }

    public AdventureException(String message)
    {
        super(message);
    }

    public AdventureException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AdventureException(Throwable cause)
    {
        super(cause);
    }
}
