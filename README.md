ImageEditor
============

How to use lib
--------------

```groovy
dependencies {
  compile 'com.github.lauhwong:image-editor:1.0'
}
```
and then apply it in your application:
```kotlin
val setup = EditorSetup(source, mOriginalPath, getEditorSavePath())
val intent = ImageEditorActivity.intent(this, setup)
startActivityForResult(intent, ACTION_REQUEST_EDITOR)
```
more details usage can be explore in example module

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
 Reference
 ----------
 1.<https://github.com/chrisbanes/PhotoView>  
 
 2.<https://github.com/Curzibn/Luban>
