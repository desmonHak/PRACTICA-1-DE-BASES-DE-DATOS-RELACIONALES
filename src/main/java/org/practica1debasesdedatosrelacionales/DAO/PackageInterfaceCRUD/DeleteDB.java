package org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD;

@FunctionalInterface
public interface RemoveDB<ClassConnection> {
    void invokeRemove(ClassConnection connection, Object condition);
}
