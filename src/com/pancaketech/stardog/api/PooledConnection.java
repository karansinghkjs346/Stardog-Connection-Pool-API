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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.clarkparsia.stardog.StardogException;
import com.clarkparsia.stardog.api.Adder;
import com.clarkparsia.stardog.api.Connection;
import com.clarkparsia.stardog.api.Getter;
import com.clarkparsia.stardog.api.Query;
import com.clarkparsia.stardog.api.Remover;

/**
 * @author Robert Butler
 * 
 */
public class PooledConnection implements Connection
{
	
	private static final Log Logger = LogFactory.getLog(PooledConnection.class);
	
	private StardogConnectionPool pool;
	private Connection wrapped;
	
	/**
	 * Creates a pooled connection to wrap the specified connection. The passed
	 * connection is allowed to be null, but will result in instantiating a
	 * terminated connection.
	 * 
	 * @param conn
	 *            The connection to wrap.
	 */
	public PooledConnection(StardogConnectionPool pool, Connection conn)
	{
		this.wrapped = conn;
		this.pool = pool;
	}
	
	/**
	 * Closes and cleans up the connection. All resources are released and
	 * closed as necessary. Any subsequent use of the connection will result in
	 * a StardogException being thrown indicating the connection is closed.
	 * 
	 * @author Robert Butler
	 * 
	 */
	protected void TerminateConnection()
	{
		try
		{
			this.wrapped.close();
			this.wrapped = null;
		}
		catch (StardogException e)
		{
			String msg = "Exception thrown during termination of pooled connection.";
			Logger.warn(msg, e);
		}
	}
	
	protected boolean isTerminated()
	{
		return this.wrapped == null;
	}
	
	private void ValidateConnection() throws StardogException
	{
		if (wrapped == null)
			throw new StardogException("Connection is closed.");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#add()
	 */
	@Override
	public Adder add() throws StardogException
	{
		ValidateConnection();
		return new PooledAdder(this, wrapped.add());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#begin()
	 */
	@Override
	public void begin() throws StardogException
	{
		ValidateConnection();
		wrapped.begin();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#close()
	 */
	@Override
	public void close() throws StardogException
	{
		if (wrapped == null)
			return;
		try
		{
			// If we close during open transaction, we want to roll back
			wrapped.rollback();
		}
		catch (StardogException e)
		{}
		try
		{
			wrapped.setAutoCommit(false);
		}
		catch (StardogException e)
		{}
		pool.returnToPool(wrapped);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#commit()
	 */
	@Override
	public void commit() throws StardogException
	{
		ValidateConnection();
		wrapped.commit();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#get()
	 */
	@Override
	public Getter get() throws StardogException
	{
		ValidateConnection();
		return new PooledGetter(this, wrapped.get());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#isAutoCommit()
	 */
	@Override
	public boolean isAutoCommit() throws StardogException
	{
		ValidateConnection();
		return wrapped.isAutoCommit();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#isOpen()
	 */
	@Override
	public boolean isOpen() throws StardogException
	{
		return wrapped != null && wrapped.isOpen();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#query(java.lang.String)
	 */
	@Override
	public Query query(String query) throws StardogException
	{
		ValidateConnection();
		return new PooledQuery(this, wrapped.query(query));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#query(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Query query(String query, String baseUri) throws StardogException
	{
		ValidateConnection();
		return new PooledQuery(this, wrapped.query(query, baseUri));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#remove()
	 */
	@Override
	public Remover remove() throws StardogException
	{
		ValidateConnection();
		return new PooledRemover(this, wrapped.remove());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#rollback()
	 */
	@Override
	public void rollback() throws StardogException
	{
		ValidateConnection();
		wrapped.rollback();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#setAutoCommit(boolean)
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws StardogException
	{
		ValidateConnection();
		wrapped.setAutoCommit(autoCommit);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Connection#size()
	 */
	@Override
	public long size() throws StardogException
	{
		ValidateConnection();
		return wrapped.size();
	}
	
}
