package org.communiquons.android.comunic.client.ui.asynctasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * SafeAsyncTask manager
 *
 * Allows an easy management of AsyncTasks
 *
 * @author Pierre HUBERT
 */
public class SafeAsyncTasksManager {

    /**
     * The list of tasks
     */
    private ArrayList<SafeAsyncTask> mTasks = new ArrayList<>();


    /**
     * Add a task to the manager
     *
     * @param task The task to add
     */
    public void addTask(@NonNull SafeAsyncTask task){
        addTask(task, false);
    }

    /**
     * Add a task to the manager
     *
     * @param task The task to add
     * @param unsetSimilar Set to true to remove any similar class
     */
    public void addTask(@NonNull SafeAsyncTask task, boolean unsetSimilar){

        if(unsetSimilar)
            unsetSpecificTasks(task.getClass());

        mTasks.add(task);
    }

    /**
     * Get the first task that belongs to a specific class
     *
     * @param cls The kind of class to search for
     * @return The first task, or null if none found
     */
    @Nullable
    public SafeAsyncTask getTask(Class<?> cls){
        for(SafeAsyncTask task : mTasks){
            if(task.getClass().equals(cls))
                return task;
        }

        return null;
    }

    /**
     * Remove a task from the manager
     *
     * @param task The task to removes
     */
    public void removeTask(SafeAsyncTask task){
        mTasks.remove(task);
    }

    /**
     * Unset a task
     *
     * @param task The task to unset
     */
    public void unsetTask(@Nullable SafeAsyncTask task){
        if(task != null) {
            task.removeOnPostExecuteListener();
            removeTask(task);
        }
    }

    /**
     * Unset all running tasks
     */
    public synchronized void unsetAllTasks(){
        for(SafeAsyncTask task : mTasks){
            if(task != null)
                task.removeOnPostExecuteListener();

        }

        mTasks = new ArrayList<>();
    }

    /**
     * Unset only a specific kind of class
     *
     * @param cls The tasks to disable
     */
    public void unsetSpecificTasks(Class<?> cls){
        for (SafeAsyncTask task : mTasks){
            if(task.getClass().equals(cls))
                unsetTask(task);

        }
    }
}
