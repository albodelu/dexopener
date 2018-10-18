# DexOpener

[![CircleCI](https://circleci.com/gh/tmurakami/dexopener.svg?style=shield)](https://circleci.com/gh/tmurakami/dexopener)
[![Release](https://jitpack.io/v/tmurakami/dexopener.svg)](https://jitpack.io/#tmurakami/dexopener)
[![Javadoc](https://img.shields.io/badge/Javadoc-1.0.5-brightgreen.svg)](https://jitpack.io/com/github/tmurakami/dexopener/1.0.5/javadoc/)<br>
![Android](https://img.shields.io/badge/Android-4.1%2B-blue.svg)

A library that provides the ability to mock
[your final classes](#limitations) on Android.

## Example

See the [dexopener-example](dexopener-example) directory.

## Usage

Add an AndroidJUnitRunner subclass into your app's **androidTest**
directory.

```java
public class YourAndroidJUnitRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        DexOpener.install(this); // Call me first!
        return super.newApplication(cl, className, context);
    }
}
```

Then specify your AndroidJUnitRunner as the default test instrumentation
runner in your app's build.gradle.

```groovy
android {
    defaultConfig {
        minSdkVersion 16 // 16 or higher
        testInstrumentationRunner 'your.app.YourAndroidJUnitRunner'
    }
}
```

## Tips

### Replacing the Application instance for testing

To instantiate your custom `android.app.Application` object other than
default application, pass a string literal of that class name as the
second argument to `super.newApplication()` in your test instrumentation
runner.

```java
@Override
public Application newApplication(ClassLoader cl, String className, Context context)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException {

    ...

    return super.newApplication(cl, "your.app.YourTestApplication", context);
}
```

**Do not call `Class#getName()` to get your Application class name**.
The following code may cause a class inconsistency error.

```java
// This code may cause a class inconsistency error.
return super.newApplication(cl, YourTestApplication.class.getName(), context);
````

## Limitations

The final classes you can mock on instrumented unit tests are only those
under the package indicated by the `applicationId` in the
`android.defaultConfig` section of your build.gradle. For example, if it
is `foo.bar`, you can mock only the final classes belonging in
`foo.bar.**`, such as `foo.bar.Baz` and `foo.bar.qux.Quux`. Therefore,
you cannot mock the final classes of both Android system classes and
third-party libraries, and cannot mock the final classes not belonging
in that package, even if they are yours.

## Installation

First, add the [JitPack](https://jitpack.io/) repository to your
build.gradle.

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

And then, add this library as `androidTestImplementation` dependency.

```groovy
dependencies {
    androidTestImplementation 'com.github.tmurakami:dexopener:1.0.5'
}
```

> **Note:** If you are using
[Multidex](https://developer.android.com/studio/build/multidex.html?hl=en),
you need to specify your BuildConfig class
[in the primary DEX file](https://developer.android.com/studio/build/multidex.html?hl=en#keep),
otherwise, you will get a NoClassDefFoundError.

## Notice

This library contains the classes of the following libraries:

- [ClassInjector](https://github.com/tmurakami/classinjector)
- [dexlib2 (part of smali/baksmali)](https://github.com/JesusFreke/smali)
- [Guava (on which dexlib2 relies)](https://github.com/google/guava)

They have been minified with
[ProGuard](https://www.guardsquare.com/en/proguard) and repackaged with
[Jar Jar Links](https://github.com/pantsbuild/jarjar). In addition,
dexlib2 has been backported to Java 7 with
[Retrolambda](https://github.com/orfjackal/retrolambda).

## License

```
Copyright 2016 Tsuyoshi Murakami

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
