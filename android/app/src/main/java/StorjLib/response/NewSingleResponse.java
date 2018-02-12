package StorjLib.response;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import StorjLib.IConvertibleToJs;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public class NewSingleResponse <T extends IConvertibleToJs>extends Response<T>{
    private T _result = null;

    public void success(T result) {
        _isSuccess = false;
        _errorMessage = null;
        _result = result;
    }

    @Override
    public void error(String errorMessage) {
        _isSuccess = false;
        _errorMessage = errorMessage;
        _result = null;
    }

    @Override
    public WritableMap toJsObject() {
        WritableMap responseJs = Arguments.createMap();

        responseJs.putBoolean(KEY_IS_SUCCESS, _isSuccess);
        responseJs.putString(KEY_ERROR_MESSAGE, _errorMessage);

        if(_result != null) {
            responseJs.putMap(KEY_RESULT, _result.toJsObject());
        } else {
            responseJs.putMap(KEY_RESULT, null);
        }

        return responseJs;
    }
}
