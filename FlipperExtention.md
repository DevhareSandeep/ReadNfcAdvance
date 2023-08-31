# How to enable the flipper on network layer

`if (BuildConfig.DEBUG) {
    // interceptor for logging
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    builder.addNetworkInterceptor(logging)
    builder.addNetworkInterceptor(StethoInterceptor())
    FlipperInitializr.getInstance(this)
    .getNetworkInspector()
    ?.let {
    builder.addNetworkInterceptor(it)
    }
}
`
# How to enable the Flipper in Application class

```  
@Override
    public void onCreate() {
       
        super.onCreate();
        if (BuildConfig.DEBUG) {
           Stetho.initializeWithDefaults(this);
           FlipperInitializr.Companion.getInstance(getApplicationContext());
        }
    }
  ```