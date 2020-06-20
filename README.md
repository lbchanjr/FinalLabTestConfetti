# FinalLabTestConfetti
MADS4006 - Adv Android Dev - Final lab test

PROBLEM DESCRIPTION
Confetti are brightly coloured pieces of paper that are thrown in the air during times of
celebration
Example of Confetti
In this test, you will create an Android app that generates confetti.
2
The app consists of buttons and an area where confetti can be drawn and displayed. In the
diagram below, a single piece of confetti is represented as a colored square.
The app allows the user to perform the following functions:
1. Draw confetti on the screen
2. Erase all confetti from the screen
3. Prevent confetti from accidentally being drawn on the screen
4. Throw he confetti in the magnetic direction that the phone is pointing
5. Sweep all confetti into a pile for cleaning up
3
Update posted to whiteboard in Virtual Classroom:
For the purposes of this test, you may assume that #4 and #5 are mutually exclusive; and that
the user will restart the app before doing either #4 or #5.
This means:
● User will never be sweeping and throwing confetti at the same time
● If user “threw” the confetti, they will restart the app and draw new confetti before
“sweeping” (and vice/versa)
FEATURE REQUIREMENTS
1. DRAWING CONFETTI ON THE SCREEN
The user draws confetti by tapping on the screen.
A single piece of confetti is a square of width 50.
The confetti is of random color in the rgb spectrum 77-255. This means that:
● the minimum possible red, green, or blue value is is 77; and,
● the maximum possible value of red, green, or blue is 255.
The confetti should be centered at the exact (x,y) coordinate where the user tapped.
● This means that the middle of the square should be positioned at the (x,y) where the
user tapped..
2. ERASING CONFETTI
The user can erase the canvas by putting the phone in a dark location.
The instructor will test this feature in one of the following ways:
● Putting phone inside a desk and observing the results; or,
● Turning off the lights in a room at night; and observing the results; or,
● Covering the phone’s display with their hand and observing the results
4
3. PREVENTING NEW CONFETTI FROM BEING ACCIDENTALLY DRAWN
Because all confetti is drawn by tapping on the screen, it is possible that the user will
accidentally tap somewhere where they did not intend to draw a confetti.
To prevent this from occurring, the app includes a button called LOCK SCREEN.
When the user presses LOCK SCREEN, the drawing area no longer responds to taps.
The user can reactivate the drawing area by pressing the LOCK SCREEN button a second time.
You are not required to change the text on the button to be “LOCK SCREEN / UNLOCK
SCREEN”.
4. THROWING CONFETTI
When the THROW CONFETTI button is pressed, all confetti on the screen moves in the
direction the phone is facing. Each confetti should move at a random speed. The minimum
movement speed is 15 pixels per movement. The maximum speed is 30 pixels per
movement.
If the phone is facing towards magnetic north, all confetti on the screen moves towards
magnetic north.
If the phone is facing towards magnetic south, all confetti on the screen moves towards
magnetic south.
For the purposes of this test, you may assume that:
● movement towards magnetic north is the same as moving the confetti in the negative y
direction; and
● moving towards magnetic south is the same as moving the positive y direction.
Positive and negative y is defined per the Android coordinate system.
For this purposes of this test, you may assume that:
● Magnetic north is defined as any region between +270 and +360, and 0 and +90
degrees on a compass.
● Magnetic south is defined as any region between +90 and +270 degrees on a compass.
5
The instructor will test this feature by pointing a phone in the direction either magnetic north or
magnetic south per the definition above, and observing the results.
5. SWEEPING UP CONFETTI
If you have ever used confetti in real life, you will know that throwing confetti leaves behind a
huge mess!
The mess left behind by confetti
6
In the SWEEP feature, the app should automatically collect all the confetti into a single pile for
easy clean up.
Specifically, when the user presses the SWEEP button, all confetti on the screen, regardless of
location, should move to the center of the drawing area.
Please note that the center of the drawing area may vary, so your instructors are only checking
that your confetti moves to the approximate visual center.
● This means that as long as your confetti ends up in a position that looks roughly like the
center of the drawing area, you have fulfilled the requirements of this feature.
● Your instructors will not be checking to see if your final (x,y) position is ACTUALLY
the center.
Each confetti should move at a random speed per the speed limits listed in the previous feature
(15-30)
There is a standard mathematical formula for moving an object towards a specific (x,y)
destination. You may use the Internet to look for this function; but you must provide a reference
in your code to the exact website you used to find the formula. Please include this information
as a comment in the section of your code where you use the formula.
