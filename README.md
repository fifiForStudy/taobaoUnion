# taobaoTicketUnion
to receives the ticket of taobao
## frame

    图片加载框架：Glide
    网络请求框架：Retrofit、okhttp
    UI注入框架：ButterKnife
    二维码框架：Zxing、RxTool
    刷新/加载更多UI框架：tkrefreshlayout
    导航和指示器：MaterialDesign

## dependencies
```
implementation 'androidx.appcompat:appcompat:1.2.0'
implementation 'com.google.android.material:material:1.2.1'
implementation 'androidx.constraintlayout:constraintlayout:2.0.1'
testImplementation 'junit:junit:4.+'
androidTestImplementation 'androidx.test.ext:junit:1.1.2'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
implementation fileTree(dir: 'libs', include: ['*.jar'])

implementation 'com.squareup.retrofit2:retrofit:2.6.3'
implementation 'com.squareup.retrofit2:converter-gson:2.7.0'
implementation 'com.github.bumptech.glide:glide:4.12.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.10.0'

implementation 'androidx.recyclerview:recyclerview:1.1.0'
implementation 'com.jakewharton:butterknife:10.2.1'
annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.1'
//基础工具库
implementation "com.github.tamsiree.RxTool:RxKit:v2.4.1"
//UI库
implementation "com.github.tamsiree.RxTool:RxUI:v2.4.1"
//(依赖RxUI库时，需要额外依赖 cardview 库)
//noinspection GradleCompatible
implementation 'com.android.support:cardview-v7:27.1.1'
//功能库（Zxing扫描与生成二维码条形码 支付宝 微信）
implementation "com.github.tamsiree.RxTool:RxFeature:v2.4.1"
implementation 'com.google.zxing:android-core:3.3.0'
implementation 'com.google.zxing:core:3.3.2'
implementation project(path: ':refreshlibrary')
```
