package org.communiquons.android.comunic.client.ui.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Safe async task (can be used statically)
 *
 * @param <Params> Params object type
 * @param <Progress> Progress object type
 * @param <Result> Result object type
 */
public abstract class SafeAsyncTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    private OnPostExecuteListener<Result> onPostExecuteListener = null;

    /**
     * Application context
     */
    @SuppressLint("StaticFieldLeak")
    private Context context;

    /**
     * Public constructor of the object
     *
     * @param context The context of the activity / fragment / application
     */
    public SafeAsyncTask(Context context){
        super();

        //Save application context
        this.context = context.getApplicationContext();
    }

    /**
     * @return Application context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set (replace) onPostExecuteListener
     *
     * @param onPostExecuteListener New listener
     */
    public void setOnPostExecuteListener(OnPostExecuteListener<Result> onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
    }

    /**
     * Remove current onPostExecuteListener
     */
    public void removeOnPostExecuteListener(){
        this.onPostExecuteListener = null;
    }

    /**
     * Check whether the a OnPostExecuteListener has been set or not
     *
     * @return TRUE if it has been set / FALSE else
     */
    public boolean hasOnPostExecuteListener(){
        return this.onPostExecuteListener != null;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if(onPostExecuteListener != null)
            onPostExecuteListener.OnPostExecute(result);
    }

    /**
     * This interface is used to update what is done once the task is finished while the task
     * is running
     *
     * @param <Result> The kind of reslt
     */
    public interface OnPostExecuteListener<Result> {

        /**
         * Executed once the task is finished
         *
         * @param result The result of the operation
         */
        void OnPostExecute(Result result);

    }
}
