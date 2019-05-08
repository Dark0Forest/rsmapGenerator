package com.vincent.rsmap.generator;

import com.vincent.rsmap.annotaions.Column;
import com.vincent.rsmap.utils.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

public class Generator {

    public static void generator(List<Class> classes,String packageName) throws Exception{
        System.out.println(Generator.class.getResource("/").getPath());
        String toFile = Generator.class.getResource("/").getPath() + File.pathSeparator + packageName.replaceAll(".", File.pathSeparator);
        File path = new File(toFile);
        if (!path.exists()) {
            path.mkdirs();
            System.out.println("create file:"+path.getPath());
        }
        System.out.println(path.getPath());
        File file = new File(toFile+"/CommonResultMapper.xml");
        FileWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\"> \n");
            String nameSpace = toFile.replaceAll("/", ".")+"CommonResultMapper";

            writer.write("<mapper namespace=\""+ nameSpace +"\">\n");
            for (Class clz : classes) {
                processClass(writer,clz);
            }
            writer.write("</mapper>");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (writer != null) {
            writer.close();
        }
    }

    public static void processClass(FileWriter writer,Class clzz) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        String id = clzz.getSimpleName();
        String type = clzz.getName();
        Field []fields = clzz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            stringBuilder.append("<resultMap id=\"").append(id).append("\" type=\"").append(type).append("\">");
            for (Field field : fields) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    String javaType = field.getType().getSimpleName().toLowerCase();
                    String fieldName = field.getName();
                    String columnName = null;
                    if (StringUtils.isEmpty(annotation.column())) {
                        columnName = StringUtils.humpToLine2(fieldName);
                    } else {
                        columnName = annotation.column().replaceAll(" ", "");
                    }
                    stringBuilder.append("<result ").append("property=\"").append(fieldName).append("\" ")
                            .append("javaType=\"").append(javaType).append("\" ")
                            .append("column=\"").append(columnName).append("\" ");
                    if (!StringUtils.isEmpty(annotation.jdbcType())) {
                        stringBuilder.append("jdbcType=\"").append(annotation.jdbcType().replaceAll(" ", "").toUpperCase()).append("\"");
                    }
                    stringBuilder.append(" />");
                } else {
                    continue;
                }
            }
            stringBuilder.append("</resultMap>");
            writer.write(stringBuilder.toString());
            writer.write("\n");
        }
    }
}