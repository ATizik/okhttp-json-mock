# okhttp-mock-suit
 [ ![Download](null/packages/atizik/maven/okhttp-mock-suit/images/download.svg?version=4.0-alpha03) ](https://bintray.com/atizik/maven/okhttp-mock-suit/4.0-alpha03/link)

Simple interceptor for OkHttp that replaces remote data with any data that you provide - put mock reponses in local storage or assets folder and replace away

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
    implementation'studio.icecreamhappens:okhttp-mock-suit:4.0-alpha03'
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
