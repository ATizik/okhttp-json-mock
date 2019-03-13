# okhttp-json-mock
[![](https://jitpack.io/v/mirrajabi/okhttp-json-mock.svg?style=flat-square)](https://jitpack.io/#mirrajabi/okhttp-json-mock)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Okhttp%20Json%20Mock-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5239)

This simple library helps you mock your data for using with okhttp+retrofit in json format in just a few moves.
it forwards the requests to local json files and returns the data stored in them.

### Usage
First add jitpack to your projects build.gradle file
```gradle
allprojects {
   	repositories {
   		...
   		maven { url "https://jitpack.io" }
   	}
}
```
Then add the dependency in modules build.gradle file
```gradle
dependencies {
    implementation'studio.icecreamhappens:okhttp-mock-suit:4.0-alpha01'
 }
```

1. Construct your custom [InputStreamProvider](https://github.com/mirrajabi/okhttp-json-mock/blob/master/okhttpjsonmock/src/main/java/ir/mirrajabi/okhttpjsonmock/providers/InputStreamProvider.java):

```java
InputStreamProvider inputStreamProvider = new InputStreamProvider() {
    @Override
    public InputStream provide(String path) {
        try {
            return getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
};
```

2. Use the `InputStreamProvider` to construct the `OkHttpMockInterceptor` and client:
```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new OkHttpMockInterceptor(getAndroidProvider(), 5))
    .build();
```

#### 3. Put your json models in assets folder like the [examples](https://github.com/mirrajabi/okhttp-json-mock/tree/master/app/src/main/assets)
```
\---api
    \---v1
        \---users
            |   1.json
            |   2.json
            |   3.json
            |   page=1.json
            |
            +---1
            |       phoneNumbers.json
            |
            +---2
            |       phoneNumbers.json
            |
            \---3
                    phoneNumbers.json
```


## 4. Implement listener, for example if you want to use AlertDialog to choose appropriate json reponse:
```
ResponsesQueue.getInstance().setListener { responseHandler ->
                launch(Dispatchers.Main) {
                    val list = responseHandler.files.toTypedArray()
                    AlertDialog.Builder(this@MainActivit)
                        .setTitle(responseHandler.methodName)
                        .setItems(list) { dialog, which ->
                            responseHandler.fileName = list[which]
                        }.apply {
                            setOnDismissListener {
                                responseHandler.dismissed()
                            }
                            show()
                        }
                }
            }
```

Original idea and code:
https://github.com/mirrajabi/okhttp-json-mock
