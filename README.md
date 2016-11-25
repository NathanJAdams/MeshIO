# MeshIO
<h3>Licence</h3>
MIT Licence

<h3>Formats supported</h3>
Currently the only supported formats are PLY and MBWF, support for further formats will follow.


<h3>Read</h3>
To read an object you need a builder object that implements IMeshBuilder\<YourMeshClass\> and an input stream for the data. Then call the MeshIO.read() method.

    try {
        YourMeshClass newMeshObject = MeshIO.read(MeshFormats.{Format}, meshBuilder, inputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

Data will be read from the input stream and sent to the builder, once all the data has been sent, the builder's build() method will be called and the result returned to the caller. If unsuccessful, a MeshIOException is thrown.

<h3>Write</h3>
Writing an object is done in a similar way. Make sure you have an object which knows about the mesh to be saved and which implements IMeshSaver, then call the MeshIO.write() method passing in an output stream. If unsuccessful, a MeshIOException is thrown.

    try {
        MeshIO.write(MeshFormats.{Format}, meshSaver, outputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

<h3>File extensions</h3>
For ease of use, the MeshFormats.getFormatFromFileExtension() method is provided which returns an IMeshFormat depending on a given file extension - although null may be returned if a format cannot be found. The extension a format uses can be retrieved using it's getFileExtension() method.
