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

import java.io.File;
import java.io.InputStream;

import org.openrdf.model.Resource;
import org.openrdf.rio.RDFFormat;

import com.clarkparsia.stardog.StardogException;
import com.clarkparsia.stardog.api.IO;

/**
 * @author Robert Butler
 * 
 */
public class PooledIO<T> extends AbstractedPooledObject<IO<T>> implements IO<T>
{
	
	public PooledIO(PooledConnection pc, IO<T> wrapped)
	{
		super(pc, wrapped);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.IO#context(org.openrdf.model.Resource)
	 */
	@Override
	public IO<T> context(Resource context) throws StardogException
	{
		ValidateConnection();
		wrapped.context(context);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.IO#file(java.io.File)
	 */
	@Override
	public IO<T> file(File f) throws StardogException
	{
		ValidateConnection();
		wrapped.file(f);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.IO#format(org.openrdf.rio.RDFFormat)
	 */
	@Override
	public IO<T> format(RDFFormat format) throws StardogException
	{
		ValidateConnection();
		wrapped.format(format);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.stardog.api.IO#stream(java.io.InputStream)
	 */
	@Override
	public IO<T> stream(InputStream is) throws StardogException
	{
		ValidateConnection();
		wrapped.stream(is);
		return this;
	}
	
}
