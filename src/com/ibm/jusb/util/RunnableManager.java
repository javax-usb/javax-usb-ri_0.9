package com.ibm.jusb.util;

/* 
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

/**
 * Class to execute (and manage a Queue of) Runnables.
 * <p>
 * Runnables may be added to this at any time.  They will not be run until
 * this is {@link #start() started}.  When this is {@link #stop() stopped}
 * Any Runnables previously added but not run will still be executed;
 * the Thread that executes them (and the queue of the) are no longer associated
 * with this; the RunnableManager's state is exactly as it was when initially created.
 * <p>
 * Note that this class is not (externally) Thread-safe; there is internal synchronization
 * with the slave Thread, but no external synchronization.  This should not be a problem
 * if the {@link #setMaxSize(long) max size} is set high enough to never be reached.  However,
 * the default size is 1.  To get an externally synchronized RunnableManager use the inner class
 * {@link com.ibm.jutil.RunnableManager.SynchronizedRunnableManager SynchronizedRunnableManager}.
 * @author Dan Streetman
 */
public class RunnableManager
{
	/**
	 * Constructor.
	 * <p>
	 * This will create a started RunnableManager.
	 */
	public RunnableManager() { this( true ); }

	/**
	 * Constructor.
	 * <p>
	 * If <i>start</i> is false, this will create but <strong>not</strong> {@link #start() start}
	 * a RunnableManager.  If <i>start</i> is true, this is identical
	 * to the {@link #RunnableManager() no-argument constructor}.
	 * @param start if the new RunnableManager should be automatically started.
	 */
	public RunnableManager( boolean start )
	{
		if (start) start();
	}

	//*************************************************************************
	// Public methods

	/**
	 * Add a Runnable.
	 * @param runnable the Runnable to add.
	 */
	public void add( Runnable newRunnable )
	{
		synchronized (runnable.lock) {
			if (isRunning() && (getSize() >= getMaxSize())) {
				stop();
				start();
			}
		}

		synchronized (runnable.lock) {
			runnable.list.add( newRunnable );
			runnable.lock.notifyAll();
		}
	}

	/**
	 * Start.
	 * <p>
	 * This must be started before any Runnables will be run.
	 * If {@link #isRunning() currently running} this will throw
	 * a IllegalThreadStateException.
	 * @throws IllegalThreadStateException If this RunnableManager is already running.
	 */
	public void start()
	{
		synchronized (runnable.lock) {
			if (runnable.running)
				throw new IllegalThreadStateException( "RunnableManager already running" );
			else
				runnable.running = true;

			Thread thread = new Thread( runnable );
			thread.setDaemon( true );
			thread.setName( "RunnableManager " + managerCount + " Thread " + (++threadCount) );
			thread.start();
		}
	}

	/**
	 * Stop.
	 * <p>
	 * This stops the currently running Thread.  If not yet started
	 * this effectively abandons any Runnables previously added without
	 * executing them.  If already started any Runnables added but not
	 * run will still be run; the Thread will exit after all have been run.
	 * <p>
	 * After calling stop the RunnableManager's state is effectively identical
	 * to when it was initially created, i.e. Runnables may be added and
	 * the RunnableManager must be started.
	 */
	public void stop()
	{
		synchronized (runnable.lock) {
			runnable.running = false;
			runnable.lock.notifyAll();
			runnable = new ManagerRunnable();
		}
	}

	/**
	 * If this is running.
	 * @return if this is running.
	 */
	public boolean isRunning()
	{
		return runnable.running;
	}

	/**
	 * Get the maximum number of Runnables to queue.
	 * <p>
	 * If more than this many Runnables are queued, a new RunnableManager is
	 * created to handle further Runnables (and the old RunnableManager is
	 * abandoned, left to finish its Runnables, end its Thread, and eventually
	 * get garbage collected).  The queue size is obviously reset, but the
	 * max is retained.
	 * <p>
	 * The default number is 1, meaning <i>a new Thread is created for
	 * every Runnable added while another is in progress</i>.  This
	 * default allows complete isolation, i.e. no Runnable will be
	 * dependent on any other Runnable and no Runnable can starve
	 * any other.  As long as Runnables execute reasonably quickly,
	 * most of the time the RunnableManager will be idle again by
	 * the time the next Runnable is added.
	 * <p>
	 * In any case, if this default proves to be too conservative for you,
	 * you can change it using
	 * {@link #setMaxSize(long) setMaxSize()}.
	 * @return The maximum number of Runnables to queue.
	 */
	public long getMaxSize() { return maxSize; }

	/**
	 * Set the maximum number of Runnables to queue.
	 * <p>
	 * This sets the maximum number of Runnables that will be queued.  See
	 * {@link #getMaxSize() getMaxSize()} for details.
	 * <p>
	 * Note that this is only effective for subsequently added Runnables;
	 * any Runnables already queued are <i>not</i> handled by a
	 * seperate RunnableManager.  Also, this is only honored if
	 * the RunnableManager is {@link #start() started}.  Any
	 * Runnables added while the RunnableManager is stopped
	 * are executed by the same RunnableManager (once it is started).
	 * @param size The maximum number of Runnables to queue.
	 * @throws IllegalArgumentException If the size is less than 1.
	 */
	public void setMaxSize(long size)
	{
		if (1 > size)
			throw new IllegalArgumentException("Max size cannot be less than 1");

		maxSize = size;
	}

	/**
	 * Get the number of Runnables currently queued.
	 * <p>
	 * This returns the number of Runnables currently queued, including
	 * the one being processed.
	 * @return The number of Runnables queued.
	 */
	public long getSize() { return runnable.getSize(); }

	//*************************************************************************
	// Instance variables

	private ManagerRunnable runnable = new ManagerRunnable();
	private long managerCount = ++count;
	private long threadCount = 0;

	private long maxSize = 1;

	//*************************************************************************
	// Class variables

	private static long count = 0;

	//*************************************************************************
	// Inner classes

	/**
	 * An externally synchronized RunnableManager.
	 */
	public class SynchronizedRunnableManager extends RunnableManager
	{
		public synchronized void add(Runnable newRunnable) { super.add(newRunnable); }
		public synchronized void start() { super.start(); }
		public synchronized void stop() { super.stop(); }
	}

	/**
	 * Main Thread's Runnable.
	 * <p>
	 * This runs all queued Runnables.
	 */
	protected class ManagerRunnable implements Runnable
	{
		public void run()
		{
			while (running || !list.isEmpty()) {
				while (!list.isEmpty()) {
					/* For a large list (size > ~50000), this is amazingly faster
					 * than pulling one at a time.
					 */
					synchronized (lock) {
						array = list.toArray();
						list.clear();
						size = array.length;
					}

					for (int i=0; i<array.length; i++,size--)
						((Runnable)array[i]).run();
				}

				synchronized (lock) {
					while (running && list.isEmpty()) {
						try { lock.wait(); }
						catch ( InterruptedException iE ) { }
					}
				}
			}
		}

		public long getSize()
		{
			synchronized (lock) {
				return list.size() + size;
			}
		}

		public Object[] array = null;
		public Object lock = new Object();
		public boolean running = false;
		public List list = new ArrayList();

		private long size = 0;
	}

}
