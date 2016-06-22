# MeshIO
<h3>Licence</h3>
MIT Licence

<h3>Formats supported</h3>
Currently the only supported format is PLY, support for further formats will follow.


<h3>Read</h3>
To read an object you need a builder class that implements IMeshBuilder\<YourClass\> which has an empty constructor, and an input stream for the data. Then simply call

    YourClass newObject = MeshIO.read(builderClass, inputStream, MeshFormat.{Format});

A new builder will be created which will read data from the input stream. Success or failure will be reported to this builder. If successful, the builder will build the object which is returned to the caller. If unsuccessful, null will be returned.

<h3>Write</h3>
Writing an object is just as easy. Make sure you have an object which knows about the data to be saved and which implements ISaver, then simply call

    boolean success = MeshIO.write(saver, outputStream, MeshFormat.{Format});

<h5>NB.</h5>
Ideally the data format would be automatically detected, however some formats don't have a "magic number" specifying which format it is. This is why the data format is required to be specified manually.
