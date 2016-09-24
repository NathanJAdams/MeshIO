# MeshIO
<h3>Licence</h3>
MIT Licence

<h3>Formats supported</h3>
Currently the only supported formats are PLY and MBWF, support for further formats will follow.


<h3>Read</h3>
To read an object you need a builder object that implements IMeshBuilder\<YourMeshClass\>, and an input stream for the data. Then simply call the MeshIO.{MeshFormat}.read() method.

    try {
        YourMeshClass newMeshObject = MeshIO.{MeshFormat}.read(meshBuilder, inputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

Data will be read from the input stream and sent to the builder, once all the data has been sent, the builder's build() method will be called with the result returned to the caller. If unsuccessful, a MeshIOException is thrown. It is recommended that a new builder and input stream are created for each new mesh object built, as the state of the builder and input stream cannot be guaranteed, especially if an exception is thrown.

<h3>Write</h3>
Writing an object is just as easy. Make sure you have an object which knows about the mesh to be saved and which implements IMeshSaver, then simply call the MeshIO.{MeshFormat}.write() method passing in an output stream. If unsuccessful, a MeshIOException is thrown.

    try {
        MeshIO.{MeshFormat}.write(meshSaver, outputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

<h3>File extensions</h3>
For ease of use, the MeshIO.getFormatFromFileExtension() method is provided which returns a MeshFormat depending on a given file extension - although null may be returned if a format cannot be found. The extension a format uses can be retrieved using it's getFileExtension() method.
