# Conway-Picture
Conway's Game of Life that "draws" an image

java ConwayPicture filename

The images (in .jpg format) are added into a folder in the Input_Images, the two images should be named One.jpg and Two.jpg, the "Two" image acts as the world and the "One" image acts as the sample population that will be selected to populate the world.

Random pixels from the the "One" image are selected and put into the world. For the first 90 frames, the cells will not draw on the final image, each new cell will slightly mutate towards the current world tile color it is on when it is "born". After the first 90 frames, the cells will draw to the final image.
Toggles
q = draw || w = newGen || e = seeFinal || a = bg || s = cell

Exporting
space = export
