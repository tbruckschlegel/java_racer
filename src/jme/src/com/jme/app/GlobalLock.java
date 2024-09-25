package com.jme.app;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 */

/**
 * @author tommy
 *
 */
public final class GlobalLock
{
    public static final ReentrantReadWriteLock rRWLock = new ReentrantReadWriteLock();
    
    public static final Lock writeLock = rRWLock.writeLock();
    public static final Lock readLock = rRWLock.readLock();

}
