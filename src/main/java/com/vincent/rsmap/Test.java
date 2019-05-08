package com.vincent.rsmap;

import com.vincent.rsmap.generator.Generator;
import com.vincent.rsmap.utils.ClazzUtils;

import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        List<Class> classes = ClazzUtils.getClasses("com.vincent.rsmap.pojo");
        if (classes!=null) {
            for (Class clz:classes) {
                System.out.println(clz.getName());
            }
            Generator.generator(classes, "com.vincent.rsmap.mapper");
        }
    }
}
