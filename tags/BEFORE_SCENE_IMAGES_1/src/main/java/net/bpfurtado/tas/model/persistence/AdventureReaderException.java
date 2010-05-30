/**
 * Adventure Suite
 * Copyright (C) 2005 Bruno Patini Furtado
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received adventure copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package net.bpfurtado.tas.model.persistence;

import net.bpfurtado.tas.AdventureException;

public class AdventureReaderException extends AdventureException
{
	private static final long serialVersionUID = 7655648827034131817L;

	public AdventureReaderException()
    {
        super();
    }

    public AdventureReaderException(String message)
    {
        super(message);
    }

    public AdventureReaderException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AdventureReaderException(Throwable cause)
    {
        super(cause);
    }
}
