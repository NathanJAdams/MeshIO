# MeshIO

### Licence</h3>
[MIT Licence](LICENSE)


### CircleCI Status
[![CircleCI](https://circleci.com/gh/NathanJAdams/MeshIO/tree/master.svg?style=svg)](https://circleci.com/gh/NathanJAdams/MeshIO/tree/master)


### Usage
View this project on [mvnrepository.com](https://mvnrepository.com/artifact/com.ripplargames/meshio)
or [maven.org](https://search.maven.org/#artifactdetails%7Ccom.ripplargames%7Cmeshio%7C1.0.0%7Cjar)

To use as a dependency in a Maven project, add the following into your pom file dependencies:

    <dependency>
      <groupId>com.ripplargames</groupId>
      <artifactId>meshio</artifactId>
      <version>2.1.1</version>
    </dependency>

For other build tools, view it on [mvnrepository.com](https://mvnrepository.com/artifact/com.ripplargames/meshio/1.0.0)
or [maven.org](https://search.maven.org/#artifactdetails%7Ccom.ripplargames%7Cmeshio%7C1.0.0%7Cjar).

The purpose of this library is two-fold.
<ul>
<li>To easily save a polygon mesh to file or an output stream in a format.</li>
<li>To load a polygon mesh from file or an input stream into ByteBuffers with any user defined vertex/indice formats.
</ul>
The main way of doing this is to instantiate an object of the class [MeshIO](src/com/ripplargames/meshio/MeshIO.java).
Meshes can then be loaded and saved via it's read() and write() methods.

##### Read with MeshIO
To read a mesh call the meshIO.read() method passing in the file path to read from.
If unsuccessful, a [MeshIOException](src/com/ripplargames/meshio/MeshIOException.java) is thrown.

    MeshIO meshIO = new MeshIO();
    try {
        Mesh mesh = meshIO.read(filePath);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

The method attempts to read the format from the file extension.
If the format is recognised and the file is valid, the mesh will be created.


##### Write with MeshIO
Writing an object is done in a similar way, call the meshIO.write() method passing in the mesh and the file path to save to.
If unsuccessful, a [MeshIOException](src/com/ripplargames/meshio/MeshIOException.java) is thrown.

    MeshIO meshIO = new MeshIO();
    try {
        meshIO.write(mesh, filePath);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

This method also attempts to read the format from the file extension.
If the format is recognised and the file is valid, the mesh will be saved to file.


### Using Formats directly
Mesh formats can be instantiated and used directly by implementing the [IMeshFormat](src/com/ripplargames/meshio/IMeshFormat.java) interface.
The format read() and write() methods are similar to the above methods but use an input or output stream instead of a file path.
Helper methods that use formats directly are in MeshIO.


##### Read with format
    PlyFormatAscii_1_0 format = new PlyFormatAscii_1_0();
    try {
        Mesh mesh = format.read(inputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }

##### Write with format
    PlyFormatAscii_1_0 format = new PlyFormatAscii_1_0();
    try {
        format.write(mesh, outputStream);
    } catch (MeshIOException e) {
        e.printStackTrace();
    }



### Retrieving data
Mesh vertices and indices can be retrieved in the form of [ByteBuffers](https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html).


##### Vertices
Vertices require a [VertexFormat](src/com/ripplargames/meshio/vertices/VertexFormat.java) to be created.
These are created from at least one [VertexFormatPart](src/com/ripplargames/meshio/vertices/VertexFormatPart.java).
An example part would be one with a [VertexType](src/com/ripplargames/meshio/vertices/VertexType.java) of Position_X
and a [VertexDataType](src/com/ripplargames/meshio/vertices/VertexDataType.java) type of float. This will produce a ByteBuffer
filled with all the x positions of the vertices in 32 bit floats.


##### Indices
Indices require a [MeshType](src/com/ripplargames/meshio/indices/IMeshType.java) - [Lines](src/com/ripplargames/meshio/indices/LinesMeshType.java) or [Triangles](src/com/ripplargames/meshio/indices/TrianglesMeshType.java);
and an [IndicesDataType](src/com/ripplargames/meshio/indices/IndicesDataType.java) - [Byte](src/com/ripplargames/meshio/indices/ByteIndicesDataType.java),
[Short](src/com/ripplargames/meshio/indices/ShortIndicesDataType.java) or [Int](src/com/ripplargames/meshio/indices/IntIndicesDataType.java).


### Formats
Currently the only supported formats are PLY, OBJ and MBMSH, [support for further formats will follow](TODO.md).
Additional formats can be created and used by either implementing the [IMeshFormat](src/com/ripplargames/meshio/IMeshFormat.java)
interface or extending the [AMeshFormat](src/com/ripplargames/meshio/meshformats/AMeshFormat.java) abstract class.
The new format will then need to be registered via the MeshIO.registerMeshFormat() method.