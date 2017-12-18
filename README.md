# MeshIO
<h3>Licence</h3>

[MIT Licence](LICENSE)


<h3>CircleCI Status</h3>

[![CircleCI](https://circleci.com/gh/NathanJAdams/MeshIO/tree/master.svg?style=svg)](https://circleci.com/gh/NathanJAdams/MeshIO/tree/master)


<h3>Usage</h3>

The purpose of this library is to allow easy saving and loading of polygon meshes with any format and with any type of mesh object.
The main way of doing this is to instantiate an object of the class [MeshIO](src/com/ripplargames/mesh_io/MeshIO.java).
Meshes can then be loaded and saved to and from file via it's read() and write() methods.


<h3>Read</h3>

To read an object create a builder object that implements [IMeshBuilder&lt;YourMeshClass&gt;](src/com/ripplargames/mesh_io/IMeshBuilder.java).
Then call the meshIO.read() method passing in the builder and a file path to read from.
If unsuccessful, a [MeshIOException](src/com/ripplargames/mesh_io/MeshIOException.java) is thrown.

    MeshIO meshIO = new MeshIO();
    try {
        YourMeshClass newMeshObject = meshIO.read(meshBuilder, filePath);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

The method attempts to read the format from the file extension.
If the format is recognised and the file is valid, data will be sent from the file to the builder.
Once all the data has been sent, the builder's build() method will be called and the result returned to the caller.


<h3>Write</h3>

Writing an object is done in a similar way.
Create a saver object that implements [IMeshSaver](src/com/ripplargames/mesh_io/IMeshSaver.java) and has access to the mesh data to be saved.
Then call the meshIO.write() method passing in the saver and a file path to save to.
If unsuccessful, a [MeshIOException](src/com/ripplargames/mesh_io/MeshIOException.java) is thrown.

    MeshIO meshIO = new MeshIO();
    try {
        meshIO.write(meshSaver, filePath);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

This method also attempts to read the format from the file extension.
If the format is recognised and the file is valid, data will be requested from the saver object and written to the file.


<h3>Using Formats directly</h3>

Format objects can be instantiated and used directly.
The format read() and write() methods are similar to the above methods but use an input or output stream instead of a file path.


<h4>Reading:</h4>

    PlyFormatAscii_1_0 format = new PlyFormatAscii_1_0();
    try {
        YourMeshClass newMeshObject = format.read(meshBuilder, inputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

<h4>Writing:</h4>

    PlyFormatAscii_1_0 format = new PlyFormatAscii_1_0();
    try {
        format.write(meshSaver, outputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

Currently the only supported formats are PLY and MBMSH, [support for further formats will follow](TODO.md).
Additional formats can be created and used by either implementing the [IMeshFormat](src/com/ripplargames/mesh_io/IMeshFormat.java) interface or extending the [MeshFormatBase](src/com/ripplargames/mesh_io/MeshFormatBase.java) abstract class.
The new format will then need to be registered via the MeshIO.registerMeshFormat() method.