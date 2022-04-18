package com.farm.authority.domain;

import java.io.Serializable;
import lombok.Data;

/**
    * InnoDB free: 12288 kB alone_dictionary_type
    */
@Data
public class AloneDictionaryType implements Serializable {
    private String id;

    private String ctime;

    private String utime;

    private String cuser;

    private String muser;

    private String state;

    private String name;

    private String comments;

    private String entitytype;

    private String entity;

    private Integer sort;

    private String parentid;

    private String treecode;

    private static final long serialVersionUID = 1L;
}