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

import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;

import org.openrdf.model.Value;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResult;

import com.clarkparsia.stardog.StardogException;
import com.clarkparsia.stardog.api.Query;

/**
 * @author Robert Butler
 * 
 */
public class PooledQuery extends AbstractedPooledObject<Query> implements Query
{
	
	public PooledQuery(PooledConnection pc, Query wrapped)
	{
		super(pc, wrapped);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#executeAsk()
	 */
	@Override
	public boolean executeAsk() throws StardogException
	{
		ValidateConnection();
		return wrapped.executeAsk();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#executeGraph()
	 */
	@Override
	public GraphQueryResult executeGraph() throws StardogException
	{
		ValidateConnection();
		return wrapped.executeGraph();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#executeSelect()
	 */
	@Override
	public TupleQueryResult executeSelect() throws StardogException
	{
		ValidateConnection();
		return wrapped.executeSelect();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#explain()
	 */
	@Override
	public String explain() throws StardogException
	{
		ValidateConnection();
		return wrapped.explain();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#isAsk()
	 */
	@Override
	public boolean isAsk()
	{
		ValidateConnectionWithIllegalStateException();
		return wrapped.isAsk();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#isGraph()
	 */
	@Override
	public boolean isGraph()
	{
		ValidateConnectionWithIllegalStateException();
		return wrapped.isGraph();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#isSelect()
	 */
	@Override
	public boolean isSelect()
	{
		ValidateConnectionWithIllegalStateException();
		return wrapped.isSelect();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#limit(long)
	 */
	@Override
	public Query limit(long limit)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.limit(limit);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#offset(long)
	 */
	@Override
	public Query offset(long offset)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.offset(offset);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String,
	 * org.openrdf.model.Value)
	 */
	@Override
	public Query parameter(String name, Value val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String, int)
	 */
	@Override
	public Query parameter(String name, int val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String, long)
	 */
	@Override
	public Query parameter(String name, long val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String, float)
	 */
	@Override
	public Query parameter(String name, float val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String,
	 * double)
	 */
	@Override
	public Query parameter(String name, double val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String, short)
	 */
	@Override
	public Query parameter(String name, short val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String,
	 * boolean)
	 */
	@Override
	public Query parameter(String name, boolean val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String, byte)
	 */
	@Override
	public Query parameter(String name, byte val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Query parameter(String name, String val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String,
	 * java.util.Date)
	 */
	@Override
	public Query parameter(String name, Date val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String,
	 * java.util.GregorianCalendar)
	 */
	@Override
	public Query parameter(String name, GregorianCalendar val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Query#parameter(java.lang.String,
	 * java.net.URI)
	 */
	@Override
	public Query parameter(String name, URI val)
	{
		ValidateConnectionWithIllegalStateException();
		wrapped.parameter(name, val);
		return this;
	}
	
}
