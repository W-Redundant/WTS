package com.farm.authority.domain;

import java.io.Serializable;
import lombok.Data;

/**
    * InnoDB free: 12288 kB; InnoDB free: 12288 kB alone_parameter
    */
@Data
public class AloneParameter implements Serializable {
    private String id;

    private String ctime;

    private String utime;

    private String cuser;

    private String muser;

    private String name;

    private String state;

    private String pkey;

    private String pvalue;

    private String rules;

    private String domain;

    private String comments;

    /**
    *  文本：1 枚举：2
    */
    private String vtype;

    /**
    * 0否，1是
    */
    private String userable;

    private static final long serialVersionUID = 1L;
}