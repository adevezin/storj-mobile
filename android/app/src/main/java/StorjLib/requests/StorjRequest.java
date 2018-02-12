package StorjLib.requests;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public abstract class StorjRequest {
    ReadableMap mInputParams;
    Promise mPromise;
    ReactContext mContext;

    public StorjRequest(ReactContext context, ReadableMap inputParams, Promise promise) {
        mInputParams = inputParams;
        mPromise = promise;
        mContext = context;
    }

    protected abstract void doWork();

    public void run() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doWork();
                }
            }).start();
        } catch (IllegalThreadStateException e) {
            mPromise.reject(e);
        }
    }

}
