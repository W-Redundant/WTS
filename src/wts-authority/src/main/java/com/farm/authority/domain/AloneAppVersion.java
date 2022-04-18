package com.farm.authority.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class AloneAppVersion implements Serializable {
    private String version;

    private String updateTime;

    private String updateUser;

    private static final long serialVersionUID = 1L;
}