package StorjLib.storjModelConvertibles;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import StorjLib.IConvertibleToJs;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public class MnemonicConvertible implements IConvertibleToJs {
    public static final String MNEMONIC = "mnemonic";
    String mMnemonic;

    public MnemonicConvertible(String mnemonic) {
        mMnemonic = mnemonic;
    }

    @Override
    public WritableMap toJsObject() {
        WritableMap result = new WritableNativeMap();
        result.putString(MNEMONIC, mMnemonic);
        return result;
    }
}
