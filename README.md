This library was made from existing [PageStepIndicator](https://raw.githubusercontent.com/devmike01/PageStepIndicator) 
library but with lots of improvement. 

- support multiline text
- add fonts
- place text on top or bottom of step indicator
- make titles clickable or static
- hide/show secondary texts

[![](https://jitpack.io/v/tatocaster/StepView.svg)](https://jitpack.io/#tatocaster/StepView)

### Preview
<img src="https://raw.githubusercontent.com/tatocaster/StepView/master/preview_01.gif" width="240" height="400" />

### How To Use 

- Add `StepView` to your app.

 Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

 Add the dependency

```groovy
dependencies {
	        implementation 'com.github.tatocaster:StepView:1.0.0'
	}
```

- Add `StepView` to your layout. E.g:

```xml
    <me.tatocaster.stepview.StepView
            app:svStepCount="3"
            app:svClickableTitle="true"
            app:svTextBottom="false"
            app:svTitleTextSize="15sp"
            app:svTitles="@array/stepview_titles"
            app:svStrokeAlpha="255"
            app:svRadius="15dp"
            app:svCurrentStepColor="@android:color/holo_red_dark"
            app:svTextColor="@android:color/white"
            app:svStepColor="@android:color/holo_purple"
            android:id="@+id/stepView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>
```

- Setup a pager adapter by extending `FragmentStatePagerAdapter` or `FragmentPagerAdapter`.

- Add the adapter to your viewpager and pass it to `StepView` by calling a handy method `setupWithViewPager(ViewPager)`.

That's all. You can customize it the way you want.

### Customization

- `svStepColor` Color of the step indicator
- `svCurrentStepColor` Color of the current step
- `svBackgroundColor` Background color of the step indicator
- `svTextColor` Background color of the step indicator
- `svSecondaryTextColor` Background color of the step indicator
- `svRadius` Radius of the step indicator
- `svStrokeWidth` Stroke Width of a current step
- `svStepCount` Size of step (With out ViewPager)
- `svTitles` Titles of pages
- `svActiveTitleColor` Current color of page's title
- `svInActiveTitleColor` Color of your previous or future page's title
- `svTitleTextSize` Size of your page's title
- `svLineHeight` Height of indicator line
- `svStrokeAlpha` Opacity of current stroke(255 means the color is solid)
- `svTextTypeFace` Set a font typeface
- `svTextBottom` Places text on top or below of step indicator
- `svClickableTitle` Make titles clickable
- `svSecondaryTextEnabled` Make secondary text in step indicator visible



License
-------

    MIT License
    
    Copyright (c) 2019 Merab Tato Kutalia
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.