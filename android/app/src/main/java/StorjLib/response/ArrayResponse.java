package StorjLib.response;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;

import StorjLib.IConvertibleToJs;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public class ArrayResponse<T extends IConvertibleToJs> extends Response<T> {
    private T[] _result = null;

    public ArrayResponse() {

    }

    public void success(T[] result) {
        _isSuccess = true;
        _errorMessage = null;
        _result = result;
    }

    public void error(String errorMessage) {
        _isSuccess = false;
        _errorMessage = errorMessage;
        _result = null;
    }

    public WritableMap toJsObject() {
        WritableMap responseJs = Arguments.createMap();

        responseJs.putBoolean("isSuccess", _isSuccess);
        responseJs.putString("errorMessage", _errorMessage);

        WritableArray resultArray = new WritableNativeArray();
        for (T resultObject : _result) {
            if (resultObject != null) {
                resultArray.pushMap(resultObject.toJsObject());
            }
        }

        responseJs.putArray("result", resultArray);
        return responseJs;
    }
}
