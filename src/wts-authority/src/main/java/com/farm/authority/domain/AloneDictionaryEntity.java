package com.farm.authority.domain;

import java.io.Serializable;
import lombok.Data;

/**
    * InnoDB free: 12288 kB
    */
@Data
public class AloneDictionaryEntity implements Serializable {
    private String id;

    private String ctime;

    private String utime;

    private String cuser;

    private String muser;

    private String state;

    private String name;

    private String entityindex;

    private String comments;

    private String type;

    private static final long serialVersionUID = 1L;
}