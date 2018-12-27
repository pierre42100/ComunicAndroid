package org.communiquons.android.comunic.client.data.models;

import org.communiquons.android.comunic.client.data.enums.KindSearchResult;

/**
 * Single search result
 *
 * @author Pierre HUBERT
 */
public class SearchResult {

    //Private fields
    private int id;
    private KindSearchResult kind;

    /**
     * Base constructor
     */
    public SearchResult(){}

    /**
     * Copy the values of the current object to a new object
     *
     * @param dst Destination object
     */
    public void copyTo(SearchResult dst){
        dst.setId(getId());
        dst.setKind(getKind());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public KindSearchResult getKind() {
        return kind;
    }

    public void setKind(KindSearchResult kind) {
        this.kind = kind;
    }
}
