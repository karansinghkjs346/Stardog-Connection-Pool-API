/*
Copyright 2011 Pancake Technology, LLC. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY PANCAKE TECHNOLOGY, LLC ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL PANCAKE TECHNOLOGY, LLC OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Pancake Technology, LLC.
 */
package com.pancaketech.stardog.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.clarkparsia.stardog.StardogException;
import com.clarkparsia.stardog.api.Connection;
import com.clarkparsia.stardog.api.ConnectionConfiguration;

/**
 * This is a basic connection pool for the Stardog api. To create a connection
 * pool, create an instance and call <tt>setConnConfig</tt> with the connection
 * configuration you want to use to create connections.
 * 
 * <p>
 * The maxPoolSize property will set the maximum number of open connections that
 * are maintained. Requests beyond that will block until a connection becomes
 * available. The default max is 50 connections.
 * </p>
 * <p>
 * The poolAggressiveness property determines how aggressively unused
 * connections are closed. It specifies the number of milli-seconds allowed for
 * the number of connections requested to reach the current pool size. The
 * default is 10,000. Note: This does not require them to be requested
 * simultaneously. If a pool size of 10 is connected to at least once per
 * second, no connections will be released even if the connections were not
 * simultaneous. It is recommended that you tune the number to your specific
 * application.
 * 
 * </p>
 * 
 * @author Robert Butler
 * 
 */
public class StardogConnectionPool
{
	private static final Log Logger = LogFactory
		.getLog(StardogConnectionPool.class);
	private static final int DefaulMaxPoolSize = 50;
	
	private ConnectionConfiguration connConfig;
	
	private int poolCount = 0;
	private List<Connection> available = new LinkedList<Connection>();
	private Set<Connection> inUse = new HashSet<Connection>();
	private int requests = 0;
	private int maxPoolSize = DefaulMaxPoolSize;
	private long poolAggressiveness = 10000;
	private boolean shutdown = false;
	private Thread watcherThread;
	
	public StardogConnectionPool()
	{
		watcherThread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				watch();
			}
		});
		watcherThread.setName("StardogConnectionPool");
		watcherThread.setDaemon(true);
		watcherThread.start();
	}
	
	private void watch()
	{
		for (;;)
		{
			try
			{
				Thread.sleep(getPoolAggressiveness());
			}
			catch (InterruptedException e)
			{}
			
			if (shutdown)
			{
				break;
			}
			
			synchronized (available)
			{
				if (requests < available.size())
				{
					poolCount--;
					Connection c = available.remove(0);
					available.notifyAll();
					terminateConnection(c);
				}
				requests = 0;
			}
		}
	}
	
	private void terminateConnection(Connection conn)
	{
		try
		{
			conn.close();
		}
		catch (Throwable t)
		{
			Logger.warn("Connection did not shutdown cleanly.", t);
		}
	}
	
	/*
	 * Retrieves an available connection or instantiates a new one if the total
	 * count is less than the
	 */
	private Connection grabAvailable() throws StardogException
	{
		if (shutdown)
			return null;
		requests++;
		if (available.size() > 0)
			return available.remove(0);
		else if (poolCount < maxPoolSize)
		{
			Connection conn = getConnConfig().connect();
			poolCount++;
			return conn;
		}
		else
		{
			return null;
		}
	}
	
	public Connection connect() throws StardogException
	{
		if (shutdown)
			return null;
		Connection conn;
		synchronized (available)
		{
			conn = grabAvailable();
			while (conn == null && !shutdown)
			{
				try
				{
					available.wait();
				}
				catch (InterruptedException e)
				{
					if (!shutdown)
						Logger
							.warn("Thread interrupted while waiting for connection",
									e);
					return null;
				}
				conn = grabAvailable();
			}
		}
		if (shutdown)
			return null;
		synchronized (inUse)
		{
			inUse.add(conn);
		}
		return new PooledConnection(this, conn);
	}
	
	protected void returnToPool(Connection conn) throws StardogException
	{
		synchronized (inUse)
		{
			if (inUse.contains(conn))
				inUse.remove(conn);
			else
			{
				// This was not managed, so just close it.
				conn.close();
				return;
			}
		}
		if (shutdown)
		{
			conn.close();
			return;
		}
		synchronized (available)
		{
			if (!conn.isOpen())
			{
				poolCount--;
			}
			else
			{
				available.add(conn);
			}
			available.notifyAll();
		}
	}
	
	public void terminatePool()
	{
		shutdown = true;
		watcherThread.interrupt();
		List<Connection> list = available;
		synchronized (list)
		{
			available = Collections.emptyList();
			list.notifyAll();
		}
		for (Connection conn : list)
		{
			terminateConnection(conn);
		}
		list = null;
		Set<Connection> set = inUse;
		synchronized (set)
		{
			inUse = Collections.emptySet();
		}
		for (Connection conn : set)
		{
			terminateConnection(conn);
		}
	}
	
	public void setMaxPoolSize(int maxPoolSize)
	{
		if (maxPoolSize < 1)
			throw new IllegalArgumentException("Max pool size must be >= 1");
		this.maxPoolSize = maxPoolSize;
	}
	
	public int getMaxPoolSize()
	{
		return maxPoolSize;
	}
	
	public void setConnConfig(ConnectionConfiguration connConfig)
	{
		this.connConfig = connConfig;
	}
	
	public ConnectionConfiguration getConnConfig()
	{
		return connConfig;
	}
	
	public void setPoolAggressiveness(long poolAggressiveness)
	{
		if (poolAggressiveness < 100)
			throw new IllegalArgumentException(
					"Pool aggressiveness must be at least 100");
		this.poolAggressiveness = poolAggressiveness;
		this.watcherThread.interrupt();
	}
	
	public long getPoolAggressiveness()
	{
		return poolAggressiveness;
	}
	
	public int getPoolCount()
	{
		return poolCount;
	}
}
