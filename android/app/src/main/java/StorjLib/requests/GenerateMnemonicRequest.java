package StorjLib.requests;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;

import StorjLib.response.SingleResponse;
import StorjLib.storjModelConvertibles.MnemonicWrapper;
import io.storj.libstorj.Storj;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public class GenerateMnemonicRequest extends StorjRequest {
    SingleResponse<MnemonicWrapper> mResponse;

    public GenerateMnemonicRequest(ReactContext context, ReadableMap inputParams, Promise promise) {
        super(context, inputParams, promise);
        mResponse = new SingleResponse<>();
    }

    @Override
    protected void doWork() {
        String mnemonic = Storj.generateMnemonic(256);
        if (mnemonic != null) {
            mResponse.success(new MnemonicWrapper(mnemonic));
        } else {
            mResponse.error("Unable to generate Mnemonic");
        }
        mPromise.resolve(mResponse);
    }
}
