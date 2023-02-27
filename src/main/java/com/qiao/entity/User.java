package com.qiao.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String phone;
    private String sex;
    private String id_number;
    private String avatar;
    private Integer status;
    private String password;


}
