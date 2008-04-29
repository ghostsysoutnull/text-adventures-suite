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
package net.bpfurtado.tas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Conf
{
	private static final Conf builder = new Conf("builder");
	private static final Conf runner = new Conf("runner");

	public static Conf builder()
	{
		return builder;
	}
	
	public static Conf runner()
	{
		return runner;
	}

    private Properties p = null;
    private String confFileName = null;
    private File confFile = null;

	private Conf(String type)
	{
		p = new Properties();
		confFileName = System.getProperty("user.home") + File.separator + ".adventure-tools" + File.separator + type + File.separator + "config.xml";
		confFile = new File(confFileName);
		try {
            if(confFile.exists()) {
                FileInputStream in = new FileInputStream(confFile);
                p.loadFromXML(in);
            }
		} catch (FileNotFoundException fnfe) {
			throw new AdventureException("Could not save the last used dir", fnfe);
		} catch (IOException ioe) {
			throw new AdventureException("Could not save the last used dir", ioe);
		}
	}

	public String get(String key)
	{
		String value = p.getProperty(key);
		if (value == null) {
			throw new AdventureException("Configuration item [" + key + "] does not exist!");
		}
		return value;
	}

	public String get(String key, String defaultValue)
	{
		String value = p.getProperty(key);
		return value == null ? defaultValue : value;
	}

	public boolean is(String key)
	{
		String value = p.getProperty(key);
		if (value == null) {
			throw new ConfigurationItemNotFoundException("Configuration item [" + key + "] does not exist!");
		}
		return new Boolean(value);
	}
	
	public boolean is(String key, boolean defaultValue)
	{
		try {
			return is(key);
		} catch (AdventureException e) {
			return defaultValue;
		}
	}
	
	public void set(String key, int i)
	{
		p.setProperty(key, new Integer(i).toString());
	}

	public void save()
	{
		try {
			FileOutputStream output = new FileOutputStream(confFile);
			p.storeToXML(output, "no comments");
		} catch (FileNotFoundException fnfe) {
			throw new AdventureException("Could not save the last used dir", fnfe);
		} catch (IOException ioe) {
			throw new AdventureException("Could not save the last used dir", ioe);
		}
	}

	public int getInt(String key, int defaultValue)
	{
		String value = p.getProperty(key);
		if (value == null) {
			value = defaultValue + "";
		}
		return new Integer(value);
	}

	public void set(String key, String value)
	{
		p.setProperty(key, value);
	}
	
	public void set(String key, Boolean value)
	{
		p.setProperty(key, value.toString());
	}
}