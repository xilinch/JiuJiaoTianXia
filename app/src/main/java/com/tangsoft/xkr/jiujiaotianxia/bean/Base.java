package com.tangsoft.xkr.jiujiaotianxia.bean;

import java.io.Serializable;

public abstract class Base implements Serializable {
    private Long id=0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
