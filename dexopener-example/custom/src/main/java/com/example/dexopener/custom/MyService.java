/*
 * Copyright 2016 Tsuyoshi Murakami
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dexopener.custom;

// The final classes you can mock on instrumented unit tests are only those under the package
// indicated by the `applicationId` in the `android.defaultConfig` section of the build.gradle. In
// this example, it is `com.example.dexopener.custom` as specified in the build.gradle.
// The package name of this class is `com.example.dexopener.custom` as above and therefore this
// class can be mocked on instrumented unit tests.
final class MyService {
    void doIt() {
    }
}