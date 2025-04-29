Simulates the classical (and limited quantum) interactions of electrons, neutrons, and protons in a vacuum

-Supports electromagnetic force between protons and electrons
-Supports strong nuclear force between protons and neutrons
-Supports rendering of electron orbitals


How to use:
- to simulate electron orbitals and avoid getting an error, 1. change the constructor of the WaveFunction in the emagSim.java file as well as the q_n, q_l, and q_m to whatever numbers you want (they have to be the same) 2. run the WaveFunction.java file 3. Run the          Runner.java file
- to add protons, neutrons, and electrons, uncomment lines in the panel constructor in emagSim or create your own with either specified coordinates or with points[n] from the fibonacci sphere method which evenly spaces points along a sphere
- to change the flow of time, modify the timeStep variable in the emagSim.java panel constructor
- to change the zoom, modify the mpp (meters per pixel) value in the emagSim.java panel constructor
- to enable/disable the electromagnetic/strong nuclear forces, uncheck the checkbox in the menu
- to evaluate the forces acting on a specific point, hover your cursor over the point. The magnitude of the force, direction (in degrees), and appropriate arrow vector will be at the top of the screen

Clarifications:
- The fibonacci sphere method is included to easily space masses along the surface of a sphere so unstable nuclei are easier to avoid
- The strong nuclear force is calculated with the negative derivative with respect to x of the reid potential (F = -dU/dx)
- An alternative form of coloring (Hue, Saturation, Lightness) is used for the coloring of field arrows and converted to the more widely used Red Green Blue (RGB) coloring system. The formula to convert from HSL to RGB is given in the hslToRGB method, where s and l are doubles in the range [0, 1] and h is in the range [0, 360] for degrees
- The greek letter theta is used for the polar (between the x and y axes) angle and the greek letter phi is used for the azimuthal (between the z axis and the plane created with the x and y axes) angle. This is the spherical coordinate system.
- hslToRGB is called with arbitrary modifications of the arguments (magnitude and angle of the vector). They are scaled by taking the fourth root of the magnitude over the max magnitude and by having a base 0.2 lightness and 0.4 saturation, but this is arbitrary and can be modified.
- Protons are represented with red circles, neutrons as white circles, and electrons as yellow circles.
  

![19-17-02 (online-video-cutter com)(1)](https://github.com/user-attachments/assets/6e66551d-1095-457f-89ca-b6e80b85a3e2)

Extremely idealized example of the simulation being used with a Uranium-238 nucleus

![image](https://github.com/user-attachments/assets/fbe00678-0dc7-4aec-a7c3-3cbb22c20079)
Electron orbitals for n = 4, l = 2, m = 2 electron wave function
