package StorjLib.response;

import StorjLib.IConvertibleToJs;

/**
 * <b>Copyright (C) One Eleven studio - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited
 * Proprietary and confidential
 * Written by Bogdan Artemenko <artemenkobogdan@gmail.com> on  on 2/9/18.</b>
 */
public abstract class Response<T extends IConvertibleToJs> implements IConvertibleToJs {
    public static final String KEY_IS_SUCCESS = "isSuccess";
    public static final String KEY_ERROR_MESSAGE = "errorMessage";
    public static final String KEY_RESULT = "result";


    boolean _isSuccess = false;
    String _errorMessage = null;



    public abstract void error(String errorMessage);

}
