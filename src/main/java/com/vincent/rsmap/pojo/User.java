package com.vincent.rsmap.pojo;

import com.vincent.rsmap.annotaions.Column;

import java.util.Date;

public class User {
    private Long id;
    @Column
    private String username;
    @Column(column = "pwd")
    private String password;
    @Column(jdbcType = "int")
    private Integer age;

    private Date updateTime;
    private Date createTime;
}
