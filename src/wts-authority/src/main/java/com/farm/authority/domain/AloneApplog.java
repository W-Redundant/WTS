package com.farm.authority.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class AloneApplog implements Serializable {
    private String id;

    private String ctime;

    private String describes;

    private String appuser;

    private String levels;

    private String method;

    private String classname;

    private String ip;

    private static final long serialVersionUID = 1L;
}