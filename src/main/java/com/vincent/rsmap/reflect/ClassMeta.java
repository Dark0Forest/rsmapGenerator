package com.vincent.rsmap.reflect;

import java.lang.reflect.Field;

public class ClassMeta {
    private Class clzz;
    private Field []fields;

    public ClassMeta(Class clzz) {
        this.clzz = clzz;
        fields = clzz.getDeclaredFields();
    }
}
