# RGBD Motion Segmentation Benchmark

## General structure:
+ 5 records with the Kinect for Xbox One
+ 5 records with the Asus Xtion Pro
+ 3 dummy Tests

The scenes consist of up to 300-600 frames at 30 fps RGB and depth frame pairs, we will use them possibly @ 10 fps

## Open Questions
+ **File format** to be specified. Same format as Stueckler behnke?
+ **@ Kinect**: use color or Infrared? (Infrared is perfectly aligned
+ Evaluation metrics to be used

## Scenes:
+ **(m)** denotes a scene where the camera is moved **(s)** denotes a static camera
+ **(k)** denotes kinect, **(a)** Asus

### Trivial scenes **(k)** **(a)**
Recorded twice, once with asus, once with kinect
+ Translation dominant **(m)** 
+ Rotation dominant **(s)** 

Use something different than chairs here?

### Room Scenes **(k)**
+ 2-3 people playing ball **(m)** 
+ Chair interaction. Move around on chair. **(m)**
+ A person bringing/moving multiple chairs

### Desk Scenes **(a)**
+ Phone handling, possibly move around other objects
+ Assemble something or stack/unstack objects.
+ Slip and slide **(m)**

### Dummy Tests **(a)** **(k)**
+ No motion, only cam movement
+ Simplest possible translation
+ Simplest possible rotation (use rotating thingy from shihao)

## Further Ideas
+ Cars
+ Interacting with Cupboards.
+ Crash a pile of something, reassemble
+ Chair interaction, can recycle my chair scene, that was a good one.
