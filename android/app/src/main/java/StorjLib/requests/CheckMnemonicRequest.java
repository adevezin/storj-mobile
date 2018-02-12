package StorjLib.requests;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;

import StorjLib.response.SingleResponse;
import StorjLib.storjModelConvertibles.MnemonicConvertible;
import io.storj.libstorj.Storj;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public class CheckMnemonicRequest extends StorjRequest {
    SingleResponse<MnemonicConvertible> mResponse;

    public CheckMnemonicRequest(ReactContext context, ReadableMap inputParams, Promise promise) {
        super(context, inputParams, promise);
        mResponse = new SingleResponse<>();
    }

    @Override
    protected void doWork() {
        try {
            boolean isMnemonicValid = Storj.checkMnemonic("");
            if (isMnemonicValid) {
                mResponse.success(null);
            } else {
                mResponse.error("Wrong mnemonic");
            }
            mPromise.resolve(mResponse.toJsObject());
        } catch (Exception e) {
            mPromise.reject("WRONG mnemonic", e);
        }
    }

}
