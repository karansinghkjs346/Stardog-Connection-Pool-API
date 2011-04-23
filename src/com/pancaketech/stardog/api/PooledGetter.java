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

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import com.clarkparsia.stardog.StardogException;
import com.clarkparsia.stardog.api.Getter;
import com.clarkparsia.stardog.util.Iteration;
import com.google.common.base.Function;

/**
 * @author Robert Butler
 * 
 */
public class PooledGetter extends AbstractedPooledObject<Getter> implements
		Getter
{
	
	public PooledGetter(PooledConnection pc, Getter wrapped)
	{
		super(pc, wrapped);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clarkparsia.stardog.api.Getter#context(org.openrdf.model.Resource)
	 */
	@Override
	public Getter context(Resource context) throws StardogException
	{
		ValidateConnection();
		wrapped.context(context);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Getter#graph()
	 */
	@Override
	public Graph graph() throws StardogException
	{
		ValidateConnection();
		return wrapped.graph();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Getter#iterator()
	 */
	@Override
	public Iteration<Statement, StardogException> iterator()
			throws StardogException
	{
		ValidateConnection();
		return new PooledIteration<Statement, StardogException>(source,
				wrapped.iterator());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clarkparsia.stardog.api.Getter#iterator(com.google.common.base.Function
	 * )
	 */
	@Override
	public <O> Iteration<O, StardogException> iterator(
			Function<Statement, O> func) throws StardogException
	{
		ValidateConnection();
		return new PooledIteration<O, StardogException>(source,
				wrapped.iterator(func));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clarkparsia.stardog.api.Getter#iterator(org.openrdf.model.Resource,
	 * org.openrdf.model.URI, org.openrdf.model.Value,
	 * org.openrdf.model.Resource)
	 */
	@Override
	public Iteration<Statement, StardogException> iterator(Resource subj,
			URI pred, Value obj, Resource ctxt) throws StardogException
	{
		ValidateConnection();
		return new PooledIteration<Statement, StardogException>(source,
				wrapped.iterator(subj, pred, obj, ctxt));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Getter#object(org.openrdf.model.Value)
	 */
	@Override
	public Getter object(Value obj) throws StardogException
	{
		ValidateConnection();
		wrapped.object(obj);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Getter#predicate(org.openrdf.model.URI)
	 */
	@Override
	public Getter predicate(URI pred) throws StardogException
	{
		ValidateConnection();
		wrapped.predicate(pred);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Getter#reset()
	 */
	@Override
	public void reset() throws StardogException
	{
		ValidateConnection();
		wrapped.reset();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.Getter#statement()
	 */
	@Override
	public Statement statement() throws StardogException
	{
		ValidateConnection();
		return wrapped.statement();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clarkparsia.stardog.api.Getter#subject(org.openrdf.model.Resource)
	 */
	@Override
	public Getter subject(Resource subj) throws StardogException
	{
		ValidateConnection();
		wrapped.subject(subj);
		return this;
	}
	
}
