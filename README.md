# MeshIO
<h3>Licence</h3>
MIT Licence

<h3>Formats supported</h3>
Currently the only supported format is PLY, support for further formats will follow.


<h3>Read</h3>
To read an object you need a builder object that implements IMeshBuilder\<YourMeshClass\>, and an input stream for the data. Then simply call the MeshIO.{MeshFormat}.read() method.

    try {
        YourMeshClass newMeshObject = MeshIO.{MeshFormat}.read(meshBuilder, inputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

Data will be read from the input stream and sent to the builder, once all the data has been sent, the builder's build() method will be called with the result returned to the caller. If unsuccessful, null will be returned.

Ideally the format would be automatically detected, however some formats don't have a "magic number" specifying itself which is why it has to be specified manually. For ease of use, the MeshIO.fromExtension() method is provided which returns a default MeshFormat depending on a given file extension - although null may be returned if a format cannot be found.

<h3>Write</h3>
Writing an object is just as easy. Make sure you have an object which knows about the mesh to be saved and which implements IMeshSaver, then simply call the MeshIO.{MeshFormat}.write() method passing in an output stream.

    try {
        boolean success = MeshIO.{MeshFormat}.write(meshSaver, outputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }
