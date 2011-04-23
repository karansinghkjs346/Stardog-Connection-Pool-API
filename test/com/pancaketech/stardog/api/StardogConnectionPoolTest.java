/**
 * Copyright 2011, Pancake Technology, LLC
 * All Rights Reserved.
 */
package com.pancaketech.stardog.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.clarkparsia.stardog.StardogException;
import com.clarkparsia.stardog.api.Connection;
import com.clarkparsia.stardog.api.ConnectionConfiguration;
import com.clarkparsia.stardog.api.Query;

/**
 * @author Robert Butler
 * 
 */
public class StardogConnectionPoolTest
{
	
	/**
	 * Test method for
	 * {@link com.pancaketech.stardog.api.StardogConnectionPool#StardogConnectionPool()}
	 * .
	 */
	@Test
	public void testStardogConnectionPool()
	{
		StardogConnectionPool scp = new StardogConnectionPool();
		try
		{
			assertEquals(50, scp.getMaxPoolSize());
			assertEquals(10000L, scp.getPoolAggressiveness());
		}
		finally
		{
			scp.terminatePool();
		}
	}
	
	/**
	 * Test method for
	 * {@link com.pancaketech.stardog.api.StardogConnectionPool#connect()}.
	 * 
	 * @throws StardogException
	 * @throws InterruptedException
	 */
	@Test
	public void testConnect() throws StardogException, InterruptedException
	{
		pool = new StardogConnectionPool();
		try
		{
			pool.setConnConfig(ConnectionConfiguration.to("testDB"));
			for (int i = 0; i < 200; i++)
			{
				connectAskClose();
			}
			
			LinkedList<Connection> conns = new LinkedList<Connection>();
			for (int i = 0; i < 200; i++)
			{
				Connection c = pool.connect();
				conns.add(c);
				Query q = c.query("ASK { ?s ?p ?o }");
				assertTrue(q.executeAsk());
				if (conns.size() == 50)
				{
					conns.removeFirst().close();
				}
			}
			
			// multi-threaded test
			
			List<Thread> threads = new LinkedList<Thread>();
			for (int i = 0; i < 100; i++)
			{
				Thread t = new Thread(new Runnable()
				{
					
					@Override
					public void run()
					{
						for (int i = 0; i < 100; i++)
						{
							try
							{
								connectAskClose();
							}
							catch (StardogException e)
							{
								return;
							}
						}
					}
				});
				t.setDaemon(true);
				t.start();
				threads.add(t);
			}
			
			for (Thread t : threads)
			{
				t.join();
			}
			
			for (Throwable t : errors)
				t.printStackTrace();
			assertEquals(0, errors.size());
		}
		finally
		{
			pool.terminatePool();
		}
	}
	
	private StardogConnectionPool pool;
	private List<Throwable> errors = new LinkedList<Throwable>();
	
	private void connectAskClose() throws StardogException
	{
		try
		{
			Connection c = pool.connect();
			Query q = c.query("ASK { ?s ?p ?o }");
			assertTrue(q.executeAsk());
			c.close();
		}
		catch (StardogException e)
		{
			synchronized (errors)
			{
				errors.add(e);
			}
			throw e;
		}
	}
	
	/**
	 * Test method for
	 * {@link com.pancaketech.stardog.api.StardogConnectionPool#terminatePool()}
	 * .
	 * 
	 * @throws StardogException
	 */
	@Test
	public void testTerminatePool() throws StardogException
	{
		ConnectionConfiguration config = ConnectionConfiguration.to("testDB");
		StardogConnectionPool scp = new StardogConnectionPool();
		scp.setConnConfig(config);
		scp.terminatePool();
		assertEquals(0, scp.getPoolCount());
		assertNull(scp.connect());
	}
	
	/**
	 * Test method for
	 * {@link com.pancaketech.stardog.api.StardogConnectionPool#getMaxPoolSize()}
	 * .
	 * 
	 * @throws StardogException
	 * @throws InterruptedException
	 */
	@Test
	public void testSetMaxPoolSize() throws StardogException,
			InterruptedException
	{
		ConnectionConfiguration config = ConnectionConfiguration.to("testDB");
		pool = new StardogConnectionPool();
		try
		{
			pool.setConnConfig(config);
			
			IllegalArgumentException iae = null;
			try
			{
				pool.setMaxPoolSize(-1);
			}
			catch (IllegalArgumentException e)
			{
				iae = e;
			}
			assertNotNull(iae);
			
			pool.setMaxPoolSize(1);
			assertEquals(1, pool.getMaxPoolSize());
			assertEquals(0, pool.getPoolCount());
			Connection conn = pool.connect();
			assertEquals(1, pool.getMaxPoolSize());
			assertEquals(1, pool.getPoolCount());
			errors.clear();
			Thread t = new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					try
					{
						connectAskClose();
					}
					catch (StardogException e)
					{
						e.printStackTrace();
					}
				}
			});
			t.setDaemon(true);
			t.start();
			for (int i = 0; i < 11; i++)
			{
				assertEquals(1, pool.getPoolCount());
				Thread.sleep(10);
			}
			assertEquals(1, pool.getPoolCount());
			assertTrue(t.isAlive());
			conn.close();
			assertEquals(1, pool.getPoolCount());
			t.join();
			assertEquals(0, errors.size());
		}
		finally
		{
			pool.terminatePool();
			pool = null;
		}
	}
	
	/**
	 * Test method for
	 * {@link com.pancaketech.stardog.api.StardogConnectionPool#setConnConfig(com.clarkparsia.stardog.api.ConnectionConfiguration)}
	 * .
	 */
	@Test
	public void testSetConnConfig()
	{
		ConnectionConfiguration config = ConnectionConfiguration.to("testDB");
		StardogConnectionPool scp = new StardogConnectionPool();
		scp.setConnConfig(config);
		assertSame(config, scp.getConnConfig());
	}
	
	/**
	 * Test method for
	 * {@link com.pancaketech.stardog.api.StardogConnectionPool#setPoolAggressiveness(long)}
	 * .
	 * 
	 * @throws StardogException
	 * @throws InterruptedException
	 */
	@Test
	public void testSetPoolAggressiveness() throws StardogException,
			InterruptedException
	{
		StardogConnectionPool scp = new StardogConnectionPool();
		try
		{
			scp.setConnConfig(ConnectionConfiguration.to("testDB"));
			
			IllegalArgumentException iae = null;
			try
			{
				scp.setPoolAggressiveness(-1);
			}
			catch (IllegalArgumentException e)
			{
				iae = e;
			}
			assertNotNull(iae);
			
			iae = null;
			try
			{
				scp.setPoolAggressiveness(99);
			}
			catch (IllegalArgumentException e)
			{
				iae = e;
			}
			assertNotNull(iae);
			
			scp.setPoolAggressiveness(5000000);
			assertEquals(5000000, scp.getPoolAggressiveness());
			scp.setPoolAggressiveness(100);
			assertEquals(100, scp.getPoolAggressiveness());
			
			Connection c = scp.connect();
			assertEquals(1, scp.getPoolCount());
			c.close();
			assertEquals(1, scp.getPoolCount());
			Thread.sleep(110);
			assertEquals(0, scp.getPoolCount());
		}
		finally
		{
			scp.terminatePool();
		}
		
	}
	
}
