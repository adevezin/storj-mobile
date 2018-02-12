package StorjLib;

import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import StorjLib.CallbackWrappers.CreateBucketCallbackWrapper;
import StorjLib.response.SingleResponse;
import StorjLib.storjModelConvertibles.BucketConvertible;
import StorjLib.utils.FileUtils;
import StorjLib.requests.DeleteBucketRequest;
import StorjLib.requests.GenerateMnemonicRequest;
import io.storj.libstorj.Bucket;
import io.storj.libstorj.DeleteFileCallback;
import io.storj.libstorj.DownloadFileCallback;
import io.storj.libstorj.File;
import io.storj.libstorj.GetBucketsCallback;
import io.storj.libstorj.Keys;
import io.storj.libstorj.KeysNotFoundException;
import io.storj.libstorj.ListFilesCallback;
import io.storj.libstorj.RegisterCallback;
import io.storj.libstorj.Storj;
import io.storj.libstorj.UploadFileCallback;
import io.storj.libstorj.android.StorjAndroid;

//TODO: 1. validate all input parameters (check in sources)
//TODO: split StorjLibModule into several modules to prevent God object creating

public class StorjLibModule extends ReactContextBaseJavaModule {

    private static final String E_VERIFY_KEYS = "STORJ_E_VERIFY_KEYS";
    private static final String E_IMPORT_KEYS = "E_IMPORT_KEYS";
    private static final String E_KEYS_EXISTS = "E_KEYS_EXISTS";
    private static final String E_GET_KEYS = "E_GET_KEYS";
    private static final String E_REGISTER = "E_REGISTER";
    private static final String E_GENERATE_MNEMONIC = "STORJ_E_GENERATE_MNEMONIC";
    private static final String E_CHECK_MNEMONIC = "E_CHECK_MNEMONIC";
    private static final String E_KEYS_NOT_FOUND = "E_KEYS_NOT_FOUND";
    private static final String E_GET_BUCKETS = "E_GET_BUCKETS";
    private static final String E_CREATE_BUCKET = "E_CREATE_BUCKET";
    private static final String E_LIST_FILES = "E_LIST_FILES";
    private static final String E_UPLOAD_FILE = "E_UPLOAD_FILE";
    private static final String MODULE_NAME = "StorjLibAndroid";

    public StorjLibModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @ReactMethod
    public void generateMnemonic(Promise promise) {
        new GenerateMnemonicRequest(getReactApplicationContext(), null, promise);
    }

    @ReactMethod
    public void checkMnemonic(String mnemonic, Promise promise) {
        try {
            promise.resolve(Storj.checkMnemonic(mnemonic));
        } catch (Exception e) {
            promise.reject(E_CHECK_MNEMONIC, e);
        }
    }

    @ReactMethod
    public void verifyKeys(final String email, final String password, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int result = StorjAndroid.getInstance(getReactApplicationContext()).verifyKeys(email, password);

                    promise.resolve(Storj.NO_ERROR == result);
                } catch (Exception e) {
                    promise.reject(E_VERIFY_KEYS, e);
                }
            }
        }).start();
    }

    @ReactMethod
    public void keysExists(Promise promise) {
        try {
            boolean result = StorjAndroid.getInstance(getReactApplicationContext()).keysExist();

            promise.resolve(result);
        } catch (Exception e) {
            promise.reject(E_KEYS_EXISTS, e);
        }
    }

    @ReactMethod
    public void importKeys(String email, String password, String mnemonic, String passcode, Promise promise) {
        try {
            boolean result = StorjAndroid.getInstance(getReactApplicationContext()).importKeys(new Keys(email, password, mnemonic), passcode);

            promise.resolve(result);
        } catch (Exception e) {
            promise.reject(E_IMPORT_KEYS, e);
        }
    }

    @ReactMethod
    public void getKeys(String passcode, Callback succesCallback, Callback errorCallback) {
        try {
            Keys keys = StorjAndroid.getInstance(getReactApplicationContext()).getKeys(passcode);

            WritableMap map = Arguments.createMap();

            map.putString("email", keys.getUser());
            map.putString("password", keys.getPass());
            map.putString("mnemonic", keys.getMnemonic());

            succesCallback.invoke(map);
        } catch (Exception e) {
            errorCallback.invoke(E_GET_KEYS);
        }
    }

    @ReactMethod
    public void register(final String login, final String password, final Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    StorjAndroid.getInstance(getReactApplicationContext()).register(login, password, new RegisterCallbackWrapper(promise));
                } catch (Exception e) {
                    promise.reject(E_REGISTER, e);
                }
            }
        }).start();
    }

    @ReactMethod
    public void createBucket(final String bucketName, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SingleResponse<BucketConvertible> response = new SingleResponse<>();

                try {
                    StorjAndroid.getInstance(getReactApplicationContext()).createBucket(bucketName, new CreateBucketCallbackWrapper(promise, response));
                } catch (Exception e) {
                    response.error(e.getMessage());
                    promise.resolve(response.toJsObject());
                }
            }
        }).start();
    }

    @ReactMethod
    void deleteBucket(final String bucketId, final Promise promise) {
        new DeleteBucketRequest(getReactApplicationContext(), null, promise).run();
    }

    @ReactMethod
    public void getBuckets(final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StorjAndroid.getInstance(getReactApplicationContext()).getBuckets(new GetBucketsCallbackWrapper(promise));
                } catch (KeysNotFoundException e) {
                    promise.reject(E_KEYS_NOT_FOUND, e);
                } catch (Exception e) {
                    promise.reject(E_GET_BUCKETS, e);
                }
            }
        }).start();
    }

    @ReactMethod
    public void uploadFile(final String bucketId,
                           final String uri,
                           final Callback progressCallback,
                           final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = FileUtils.getPath(getCurrentActivity().getApplicationContext(),
                            Uri.parse(uri));
                    if (path != null && FileUtils.isLocal(path)) {

                        StorjAndroid.getInstance(getReactApplicationContext()).uploadFile(bucketId,
                                path, new UploadFileCallback() {
                                    @Override
                                    public void onProgress(String filePath, double progress, long uploadedBytes, long totalBytes) {
                                        WritableMap progressMap = new WritableNativeMap();

                                        progressMap.putString("filePath", filePath);
                                        progressMap.putDouble("progress", progress);

                                        progressCallback.invoke(progressMap);
                                    }

                                    @Override
                                    public void onComplete(String filePath, File file) {
                                        WritableMap resultMap = new WritableNativeMap();
                                        resultMap.putBoolean("isSuccess", true);
                                        resultMap.putNull("errorMessage");

                                        promise.resolve(resultMap);
                                    }

                                    @Override
                                    public void onError(String filePath, int code, String message) {
                                        WritableMap resultMap = new WritableNativeMap();
                                        resultMap.putBoolean("isSuccess", false);
                                        resultMap.putString("errorMessage", code + ":" + message);

                                        promise.resolve(resultMap);
                                    }
                                });
                    }
                } catch (Exception e) {
                    promise.reject(E_UPLOAD_FILE, e);
                }

            }
        }).start();
    }

    @ReactMethod
    public void listFiles(final String bucketId, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StorjAndroid.getInstance(getReactApplicationContext()).listFiles(bucketId,
                            new ListFilesCallbackWrapper(promise));
                } catch (KeysNotFoundException e) {
                    promise.reject(E_KEYS_NOT_FOUND, e);
                } catch (Exception e) {
                    promise.reject(E_LIST_FILES, e);
                }
            }
        }).start();
    }

    public void downloadFile(final String bucketId,
                             final String fileId,
                             final Callback progressCallback,
                             final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bucket bucket = new Bucket(bucketId, null, null, false);
                File file = new File(fileId, bucketId, null, null, false, 0L,
                        null, null, null, null);
                StorjAndroid.getInstance(getReactApplicationContext()).downloadFile(bucket, file,
                        new DownloadFileCallback() {
                            @Override
                            public void onProgress(String fileId, double progress, long downloadedBytes, long totalBytes) {
                                WritableMap progressMap = new WritableNativeMap();

                                progressMap.putString("filePath", fileId);
                                progressMap.putDouble("progress", progress);

                                progressCallback.invoke(progressMap);
                            }

                            @Override
                            public void onComplete(String fileId, String localPath) {
                                WritableMap map = new WritableNativeMap();
                                map.putBoolean("isSuccess", true);

                                WritableMap fileMap = new WritableNativeMap();
                                fileMap.putString("fileId", fileId);
                                fileMap.putString("filePath", localPath);
                                map.putMap("result", fileMap);

                                promise.resolve(map);
                            }

                            @Override
                            public void onError(String fileId, int code, String message) {
                                WritableMap errorMap = new WritableNativeMap();
                                errorMap.putBoolean("isSuccess", false);
                                errorMap.putString("errorMessage", message);
                                errorMap.putNull("result");

                                promise.resolve(errorMap);
                            }
                        });
            }
        }).start();
    }

    @ReactMethod
    public void deleteFile(final String bucketId, final String fileId, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StorjAndroid.getInstance(getReactApplicationContext()).deleteFile(bucketId, fileId, new DeleteFileCallback() {
                        @Override
                        public void onFileDeleted() {
                            WritableMap resultMap = new WritableNativeMap();
                            resultMap.putBoolean("isSuccess", true);
                            resultMap.putNull("errorMessage");
                            promise.resolve(resultMap);
                        }

                        @Override
                        public void onError(int code, String message) {
                            WritableMap resultMap = new WritableNativeMap();
                            resultMap.putBoolean("isSuccess", false);
                            resultMap.putString("errorMessage", message);
                        }
                    });
                } catch (KeysNotFoundException e) {
                    promise.reject(E_KEYS_NOT_FOUND, e);
                } catch (Exception e) {
                    promise.reject(e);
                }

            }
        }).start();
    }

    private class ListFilesCallbackWrapper implements ListFilesCallback {

        private Promise mPromise;

        public ListFilesCallbackWrapper(Promise promise) {
            mPromise = promise;
        }

        @Override
        public void onFilesReceived(File[] files) {
            WritableMap result = new WritableNativeMap();
            result.putBoolean("isSuccess", true);
            result.putNull("errorMessage");
            WritableArray array = new WritableNativeArray();

            for (File file : files) {
                WritableMap map = new WritableNativeMap();
                map.putString("id", file.getId());
                map.putString("bucketId", file.getBucketId());
                map.putString("name", file.getName());
                map.putString("created", file.getCreated());
                map.putBoolean("decrypted", file.isDecrypted());
                map.putDouble("size", file.getSize());
                map.putString("mimeType", file.getMimeType());
                map.putString("erasure", file.getErasure());
                map.putString("index", file.getIndex());
                map.putString("hmac", file.getHMAC());

                array.pushMap(map);
            }
            result.putArray("result", array);
            mPromise.resolve(result);
        }

        @Override
        public void onError(int code, String message) {
            WritableMap map = new WritableNativeMap();
            map.putBoolean("isSuccess", false);
            map.putString("errorMessage", message);
            map.putNull("result");
            mPromise.resolve(map);
        }

    }

    private class RegisterCallbackWrapper implements RegisterCallback {

        private Promise _promise;
        private WritableMap _map;

        public RegisterCallbackWrapper(Promise promise) {
            _promise = promise;
            _map = Arguments.createMap();
        }

        @Override
        public void onConfirmationPending(final String email) {
            RegisterResponse response = new RegisterResponse(
                    true,
                    StorjAndroid.getInstance(getReactApplicationContext()).generateMnemonic(256),
                    null);

            _map.putBoolean("isSuccess", response.getResult());
            _map.putString("mnemonic", response.getMnemonic());
            _map.putString("errorMessage", response.getErrorMessage());

            _promise.resolve(_map);
        }

        @Override
        public void onError(int code, String message) {
            RegisterResponse response = new RegisterResponse(
                    false,
                    null,
                    message);

            _map.putBoolean("isSuccess", response.getResult());
            _map.putString("mnemonic", response.getMnemonic());
            _map.putString("errorMessage", response.getErrorMessage());

            _promise.resolve(_map);
        }
    }

    private class RegisterResponse {

        private Boolean _isSuccess = false;
        private String _mnemonic = null;
        private String _errorMessage = null;

        public RegisterResponse(Boolean isSuccess, String mnemonic, String errorMessage) {
            _isSuccess = isSuccess;
            _mnemonic = mnemonic;
            _errorMessage = errorMessage;
        }

        public Boolean getResult() {
            return _isSuccess;
        }

        public String getMnemonic() {
            return _mnemonic;
        }

        public String getErrorMessage() {
            return _errorMessage;
        }
    }

    private class GetBucketsCallbackWrapper implements GetBucketsCallback {

        private Promise _promise;

        public GetBucketsCallbackWrapper(Promise promise) {
            _promise = promise;
        }

        @Override
        public void onBucketsReceived(Bucket[] buckets) {
            WritableArray array = Arguments.createArray();

            for (Bucket buck : buckets) {
                WritableMap map = Arguments.createMap();

                map.putString("id", buck.getId());
                map.putString("name", buck.getName());
                map.putString("created", buck.getCreated());
                map.putInt("hash", buck.hashCode());
                map.putBoolean("isDecrypted", buck.isDecrypted());

                array.pushMap(map);
            }

            _promise.resolve(array);
        }

        @Override
        public void onError(int code, String message) {
            _promise.reject(E_GET_BUCKETS, message);
        }

    }
}
