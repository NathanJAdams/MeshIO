# MeshIO
<h3>Licence</h3>
MIT Licence

<h3>Formats supported</h3>
Currently the only supported format is PLY, support for further formats will follow.


<h3>Read</h3>
To read an object you need a builder object that implements IMeshBuilder\<YourMeshClass\>, and an input stream for the data. Then simply call the MeshIO.read() method passing in the appropriate MeshFormat.

    YourMeshClass newMeshObject = MeshIO.read(meshBuilder, inputStream, MeshFormat.{Format});

Data will be read from the input stream and sent to the builder, once all the data has been sent, the builder's build() method will be called with the result returned to the caller. If unsuccessful, null will be returned. Ideally the format would be automatically detected, however some formats don't have a "magic number" specifying which format is used which is why it has to be specified manually.

<h3>Write</h3>
Writing an object is just as easy. Make sure you have an object which knows about the mesh to be saved and which implements IMeshSaver, then simply call the MeshIO.write() method passing in an output stream and the desired MeshFormat.

    boolean success = MeshIO.write(meshSaver, outputStream, MeshFormat.{Format});
