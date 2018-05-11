/*
 * org.osjava.sj.memory.MemoryContext
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Dec 30, 2004
 *
 * Copyright (c) 2004, Robert M. Zigweid All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * + Neither the name of the Simple-JNDI nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without 
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package org.osjava.sj.memory;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingException;

import org.osjava.sj.jndi.AbstractContext;

/**
 * A generic context that requires no DataSource backend.   It is designed to
 * live exclusively in memory and not have its state saved.
 * 
 * @author Robert M. Zigweid
 * @since Simple-JNDI 0.11
 * @version $Rev$ $Date$
 */
public class MemoryContext extends AbstractContext {

    /**
     * 
     */
    public MemoryContext() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param env
     */
    public MemoryContext(Hashtable env) {
        super(env);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param env
     * @param systemOverride
     */
    public MemoryContext(Hashtable env, boolean systemOverride) {
        super(env, systemOverride);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param env
     * @param parser
     */
    public MemoryContext(Hashtable env, NameParser parser) {
        super(env, parser);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param systemOverride
     */
    public MemoryContext(boolean systemOverride) {
        super(systemOverride);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param systemOverride
     * @param parser
     */
    public MemoryContext(boolean systemOverride, NameParser parser) {
        super(systemOverride, parser);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param parser
     */
    public MemoryContext(NameParser parser) {
        super(parser);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param env
     * @param systemOverride
     * @param parser
     */
    public MemoryContext(Hashtable env, boolean systemOverride, NameParser parser) {
        super(env, systemOverride, parser);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param that
     */
    public MemoryContext(AbstractContext that) {
        super(that);
        // TODO Auto-generated constructor stub
    }

    /**
     * @see javax.naming.Context#createSubcontext(javax.naming.Name)
     */
    public Context createSubcontext(Name name) throws NamingException {
        Context newContext;
        /* Get the subcontexts of /this/ subcontext. */
        Hashtable subContexts = getSubContexts();

        if(name.size() > 1) {
            if(subContexts.containsKey(name.getPrefix(1))) {
                Context subContext = (Context)subContexts.get(name.getPrefix(1));
                newContext = subContext.createSubcontext(name.getSuffix(1));
                return newContext;
            } 
            throw new NameNotFoundException("The subcontext " + name.getPrefix(1) + " was not found.");
        }
        
        if(lookup(name) != null) {
            throw new NameAlreadyBoundException();
        }

        Name contextName = getNameParser((Name)null).parse(getNameInNamespace());
        contextName.addAll(name);
        newContext = new MemoryContext(this);
        ((AbstractContext)newContext).setNameInNamespace(contextName);
        bind(name, newContext);
        return newContext;
    }
}
