package com.ripplargames.meshio;

public interface IMeshBuilder<T extends IMesh> {
    T build(MeshRawData meshRawData);
}
