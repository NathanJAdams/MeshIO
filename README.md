# MeshIO
<h3>Licence</h3>
MIT Licence

<h3>Formats supported</h3>
Currently the only supported format is PLY, support for further formats will follow.


<h3>Read</h3>
To read an object you need a builder class that implements I\{Format\}Builder\<YourClass\> which has an empty constructor, and an input stream for the data. Then simply call

    YourClass newObject = {Format}Reader.read(builderClass, inputStream);

A new builder will be created which will read data from the input stream. Success or failure will be reported to this builder. If successful, the builder will build the object which is returned to the caller. If unsuccessful, null will be returned.

<h3>Write</h3>
Writing an object is just as easy. Make sure your class implements I\{Format\}Savable and that you have an output stream to save the data to. Then simply call

    boolean success = {Format}Writer.write(savable, outputStream);

<h3>Future updates</h3>
 - Responsibility for saving data moved from I{Format}Savable to I{Format}Saver interfaces.
 - I{Format}Builder and I{Format}Saver to be changed to IBuilder and ISaver respectively, all formats will be able to be built/saved using a single interface.

<h5>NB.</h5>
Ideally the data format would be automatically detected, however some formats don't have a "magic number" specifying which format it is. This is why the data format is required to be specified manually.
