Fotag Mobile
======
- Created by Dongwoo Son
- Created at March 26, 2016
- Email: d3son@uwaterloo.ca

Settings
-------------------
- Android Studio 1.5, Android 6.0 "Marshmallow" (API Level 23)
- Minimum SDK: Android 5.0 "Lollipop" (API Level 21)
- Tested on:
    - Nexus 6 Android 5.1 "Lollipop" API 22 x86 AVD
    - Note Edge Android 5.1.1 Real Device

How to use it
-------------
### Toolbar:
- Rating Filter:
    The images with rating greater than or eqaul to the filter will be shown.
    The filter can be cleared by Clear Filter
- Delete All:
    All the images added will be deleted permanently.
    
### Floating Action Toolbar:
- Upload: 10 images saved in APK will be loaded to the image grid.
- Link: any images from the web URI (http and https with image extension) can be loaded from the internet.
- Search: load top 10 Google image search result to the image grid (Yet to be implemented)

### Image:
- Rating: Touch on stars to rate each image. Touch on Clear to clear the rating.
- Thumbnail:
    Touch on the thumbnail image to get enlarged image on full sized screen.
    Touch on the full sized image to dismiss and go back to the image grid.

More description
----------------
- No third party library was used.
- Would perform if I could use Android-Universal-Image-Loader