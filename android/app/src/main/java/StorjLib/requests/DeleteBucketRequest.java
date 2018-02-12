package StorjLib.requests;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;

import StorjLib.response.SingleResponse;
import StorjLib.storjModelConvertibles.BucketConvertible;
import io.storj.libstorj.DeleteBucketCallback;
import io.storj.libstorj.KeysNotFoundException;
import io.storj.libstorj.android.StorjAndroid;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public class DeleteBucketRequest extends StorjRequest implements DeleteBucketCallback {
    private SingleResponse<BucketConvertible> mResponse;

    public DeleteBucketRequest(ReactContext context, ReadableMap inputParams, Promise promise) {
        super(context, inputParams, promise);
        mResponse = new SingleResponse<>();
    }

    @Override
    public void onBucketDeleted() {
        mResponse.success(null);
        mPromise.resolve(mResponse);
    }

    @Override
    public void onError(int code, String message) {
        mResponse.error(message);
        mPromise.resolve(mResponse);
    }

    @Override
    protected void doWork() {
        try {
            StorjAndroid.getInstance(mContext)
                    .deleteBucket(mInputParams.getString(""), this);
        } catch (KeysNotFoundException e) {
            mPromise.reject("KeysNotFound", e);
        }
    }
}
