package com.farm.authority.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class AloneParameterLocal implements Serializable {
    private String id;

    private String parameterid;

    private String euser;

    private String pvalue;

    private static final long serialVersionUID = 1L;
}