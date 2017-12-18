package com.ripplargames.mesh_io;

public class MeshIOException extends Exception {
    private static final long serialVersionUID = 1L;

    public MeshIOException() {
        super();
    }

    public MeshIOException(String message) {
        super(message);
    }

    public MeshIOException(Throwable cause) {
        super(cause);
    }

    public MeshIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
