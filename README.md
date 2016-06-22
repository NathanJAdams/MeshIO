# MeshIO
<h3>License</h3>
MIT License

<h3>Formats supported</h3>
Currently the only supported format is PLY, support for further formats will follow.


<h3>Read</h3>
To read an object you need a builder class that implements I\{Format\}Builder\<YourClass\> and an input stream for the data. Then simply call

    YourClass newObject = {Format}Reader.read(builderClass, inputStream);

A new builder will be created which will read data from the input stream. Success or failure will be reported to this builder. If successful, the builder will build the object which is returned to the caller. If unsuccessful, null will be returned.

<h3>Write</h3>
Writing an object is just as easy. Make sure your class implements I\{Format\}Savable and that you have an output stream to save the data to. Then simply call

    boolean success = {Format}Writer.write(savable, outputStream);

<h3>Future updates</h3>
 - Responsibility for saving data moved from ISavable to ISaver\<T\> interfaces.
 - A single Reader class and a single Writer class, with a format parameter passed in to the read() and write() methods.
