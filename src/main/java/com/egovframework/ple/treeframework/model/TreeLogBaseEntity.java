package com.egovframework.ple.treeframework.model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class TreeLogBaseEntity extends TreeSearchEntity implements Serializable {

    //@Getter @Setter
    @Column(name="c_method")
    private String c_method;

    @Column(name="c_state")
    private String c_state;

    @Column(name="c_date")
    private Date c_date;

    /*
     * Extend Bean Field
     */
    private Boolean copyBooleanValue;

    @Transient
    public Boolean getCopyBooleanValue() {
        copyBooleanValue = false;
        if (this.getCopy() == 0) {
            copyBooleanValue = false;
        } else {
            copyBooleanValue = true;
        }
        return copyBooleanValue;
    }

    public void setCopyBooleanValue(Boolean copyBooleanValue) {
        this.copyBooleanValue = copyBooleanValue;
    }

    @Override
    public <T extends TreeSearchEntity> void setFieldFromNewInstance(T paramInstance) {

        if( paramInstance instanceof TreeBaseEntity){
            if(paramInstance.isCopied()) {
                this.setC_title("copy_" + this.getC_title());
            }
        }
    }

}
