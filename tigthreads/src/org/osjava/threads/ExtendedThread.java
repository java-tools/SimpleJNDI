/* 
 * org.osjava.threads.ExtendedThread
 * 
 * $Revision: 1.1 $
 * 
 * Created on Aug 01, 2002
 * 
 * Copyright (c) 2002-2003, Robert M. Zigweid
 * All rights reserved. 
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
 * + Neither the name of the TigThreads nor the names of its contributors may 
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

package org.osjava.threads;

/**
 * A thread implementation that adds additional accessor API for easier 
 * management.
 * <p>
 * Specifically this implmentation of {@link Thread} adds accessability to the 
 * Runnable which standard Java {@link Thread}s do not.  
 * <p>
 * This class is designed to only be instantiated by the {@link ThreadManager}.
 *
 * @author Robert M. Zigweid
 * @version $Revision: 1.1 $ $Date: 2003/09/08 16:40:26 $
 */
public class ExtendedThread extends Thread implements ExtendedRunnable {

    /* TODO: Add a clone() override to prevent the ExtendedThread from being 
     * cloned*/

    /** 
     * Field containing the {@link ExtendedRunnable} object 
     */
    private ExtendedRunnable runnable;

    /** 
     * Field which determines whether or not the {@link Thread} has been 
     * started.
     */
    private boolean started = false;

    /** 
     * Create an ExtendedThread that is part of a specified 
     * {@link ExtendedThreadGroup} and with a specified name.  The 
     * {@link ExtendedRunnable} is assigned to be the newly created 
     * ExtendedThread. This method is designed to be only called by the
     * {@link ThreadManager} thread creation methods.
     * 
     * @param threadGroup The {@link ExtendedThreadGroup} to which the thread  
     *                    is to belong
     * @param name        The name of the thread.
     */
    protected ExtendedThread(ExtendedThreadGroup threadGroup, String name) {
        super(threadGroup, name);
        setRunnable(this);
    }

    /**
     * Create an ExtendedThread that is part of a specified
     * {@link ExtendedRunnable} associated with it, an
     * {@link ExtendedThreadGroup} and with a specified name.
     * This method is designed to be only called by the
     * {@link ThreadManager} thread creation methods.
     * 
     * @param threadGroup The ExtendedThreadGroup to which the thread
     *                    is to belong.
     * @param target      The ExtendedRunnable target for the thread.
     * @param name        The name of the thread.
     */
    protected ExtendedThread(
        ExtendedThreadGroup threadGroup,
        ExtendedRunnable target,
        String name) {
        super(threadGroup, target, name);
        setRunnable(target);
    }

    /**
     * Private constructor which does nothing, just to make sure we don't 
     * 'accidently' get created.
     */
    private ExtendedThread() {}

    /** 
     * Sets the runnable object for the thread.  An 
     * {@link ExtendedRunnable} parameter is accepted..  
     * 
     * @param runnable The ExtendedRunnable object which is to be 
     *                 utilized ExtendedThread.
     */
    public void setRunnable(ExtendedRunnable runnable) {
        /* TODO: A check needs to be added so that the runnable cannot be 
         * set after the thread is started. */
        this.runnable = runnable;
    }

    /** 
     * Gets the {@link ExtendedRunnable} object for this thread.
     * 
     * @return The ExtendedRunnable object that this ExtendedThread
     * is utilizing.
     */
    public ExtendedRunnable getRunnable() {
        return runnable;
    }

    /**
     * Indicates whether or not the thread has at any time been
     * tarted.  Returning true indicates that the ExtendedThread has at some 
     * point {@link ExtendedThread#start()} has been called.
     * 
     * @return A boolean value 
     */
    public boolean isStarted() {
        return started;
    }

    /** 
     * Starts the thread's execution.  This is a wrapper around the 
     * {@link java.lang.Thread#start()} method, but also sets the value used
     * by {@link #isStarted()} so that it will return true.
     */
    public void start() {
        started = true;
        super.start();
    }

    /**
     * Instructs the threads runnable object to eithr abort or cancel the 
     * abort sequence.  if <code>abort</code> is true, the abort sequence is
     * started in the {@link ExtendedRunnable} object found by <code>
     * getRunnable()</code>.  If <code>abort</code> is false, the aborting 
     * squence is stopped, if it had been started.  There is no guarantee that 
     * the aborting can be stopped once started.
     * 
     * @param abort boolean value determining whether or not the thread is to 
     *              be aborted, or can be set to halt a previously declared 
     *              abort
     */
    public void setAbort(boolean abort) {
        getRunnable().setAbort(abort);
    }

    /**
     * Returns a boolean value indicating  whether or not the
     * ExtendedThread and its associated {@link ExtendedRunnable} are in the 
     * process of aborting, or already have aborted.  Returning true indicates
     * that the thread has ceased execution, or begun the process of aborting.
     * 
     * @return A boolean value indicating whether the thread has aborted or 
     *         not. 
     */
    public boolean getAbort() {
        return getRunnable().getAbort();
    }
}
