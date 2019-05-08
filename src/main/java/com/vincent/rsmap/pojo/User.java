package com.vincent.rsmap.pojo;

import com.vincent.rsmap.annotaions.Column;

public class User {
    @Column
    private String username;
    @Column(column = "pwd")
    private String password;
    @Column(jdbcType = "int")
    private Integer age;
}
