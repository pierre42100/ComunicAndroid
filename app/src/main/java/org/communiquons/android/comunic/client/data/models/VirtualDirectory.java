package org.communiquons.android.comunic.client.data.models;

import org.communiquons.android.comunic.client.data.enums.VirtualDirectoryType;

/**
 * Virtual directory information
 *
 * @author Pierre HUBERT
 */
public class VirtualDirectory {

    //Private fields
    private VirtualDirectoryType kind;
    private int id;

    public VirtualDirectoryType getKind() {
        return kind;
    }

    public void setKind(VirtualDirectoryType kind) {
        this.kind = kind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
