package cz.cvut.fit.wikimetric.pocclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;

public class Tag {
    public String name;
    public Long id;
    public boolean assignable;
    public Long parentId;
    public Collection<Long> childrenIds;
    public Collection<Long> elementIds;

    public Tag(@JsonProperty("name") String name,
               @JsonProperty("id") Long id,
               @JsonProperty("assignable") boolean assignable,
               @JsonProperty("parentId") Long parentId,
               @JsonProperty("childrenIds") Collection<Long> childrenIds,
               @JsonProperty("elementIds") Collection<Long> elementIds) {
        this.name = name;
        this.id = id;
        this.assignable = assignable;
        this.parentId = parentId;
        this.childrenIds = childrenIds;
        this.elementIds = elementIds;
    }

    public Tag(String name) {
        this(name, 0L, true, null, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", assignable=" + assignable +
                ", parentId=" + parentId +
                ", childrenIds=" + childrenIds +
                ", elementIds=" + elementIds +
                '}';
    }
}
