package org.cswteams.ms3.entity.soft_delete;

import lombok.Getter;
import org.hibernate.annotations.*;

import javax.persistence.*;

@Getter
@MappedSuperclass
@FilterDef(
        name = "softDeleteFilter",
        parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(name = "softDeleteFilter", condition = "is_deleted = :isDeleted")
public abstract class SoftDeletableEntity {

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }
}
