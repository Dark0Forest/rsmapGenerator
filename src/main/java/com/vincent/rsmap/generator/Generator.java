package com.vincent.rsmap.generator;

import com.vincent.rsmap.annotaions.Column;
import com.vincent.rsmap.utils.StringUtils;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

public class Generator {
    private static final Map<Class<?>, String> TYPE_ALIAS = new HashMap<Class<?>, String>();

    static {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("byte[]", Byte[].class);
        registerAlias("long[]", Long[].class);
        registerAlias("short[]", Short[].class);
        registerAlias("int[]", Integer[].class);
        registerAlias("integer[]", Integer[].class);
        registerAlias("double[]", Double[].class);
        registerAlias("float[]", Float[].class);
        registerAlias("boolean[]", Boolean[].class);

        registerAlias("_byte", byte.class);
        registerAlias("_long", long.class);
        registerAlias("_short", short.class);
        registerAlias("_int", int.class);
        registerAlias("_integer", int.class);
        registerAlias("_double", double.class);
        registerAlias("_float", float.class);
        registerAlias("_boolean", boolean.class);

        registerAlias("_byte[]", byte[].class);
        registerAlias("_long[]", long[].class);
        registerAlias("_short[]", short[].class);
        registerAlias("_int[]", int[].class);
        registerAlias("_integer[]", int[].class);
        registerAlias("_double[]", double[].class);
        registerAlias("_float[]", float[].class);
        registerAlias("_boolean[]", boolean[].class);

        registerAlias("date", Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigdecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);

        registerAlias("date[]", Date[].class);
        registerAlias("decimal[]", BigDecimal[].class);
        registerAlias("bigdecimal[]", BigDecimal[].class);
        registerAlias("biginteger[]", BigInteger[].class);
        registerAlias("object[]", Object[].class);

        registerAlias("map", Map.class);
        registerAlias("hashmap", HashMap.class);
        registerAlias("list", List.class);
        registerAlias("arraylist", ArrayList.class);
        registerAlias("collection", Collection.class);
        registerAlias("iterator", Iterator.class);

        registerAlias("ResultSet", ResultSet.class);
    }

    public static void registerAlias(String alias, Class<?> clazz) {
        TYPE_ALIAS.put(clazz, alias);
    }

    public static void generator(List<Class> classes, String packageName, String toFile, String mapperName) throws Exception {
        System.out.println(Generator.class.getResource("/").getPath());
        toFile = System.getProperty("user.dir") + "/" + toFile;
        File path = new File(toFile);
        if (!path.exists()) {
            path.mkdirs();
            System.out.println("create file:" + path.getPath());
        }

        Document document = null;

        Element root = new Element("mapper");
        root.setAttribute("namespace", packageName + "." + mapperName);

        document = new Document(root);
        DocType docType = new DocType("mapper", "-//mybatis.org/DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        document.setDocType(docType);
        for (Class clz : classes) {
            processClass(root, clz, packageName);
        }

        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        outputter.setFormat(outputter.getFormat().setEncoding("UTF-8"));

        outputter.output(document, new FileOutputStream(toFile + File.separator + mapperName + ".xml"));
    }

    public static void processClass(Element root, Class clzz, String packageName) throws Exception {
        String id = clzz.getSimpleName();
        String type = clzz.getName();
        Field[] fields = clzz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            Element resultMap = new Element("resultMap");
            resultMap.setAttribute("id", id);
            resultMap.setAttribute("type", type);
            for (Field field : fields) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    String javaType = TYPE_ALIAS.get(field.getType());
                    if (StringUtils.isEmpty(javaType)) {
                        javaType = field.getType().getName();
                    }
                    String fieldName = field.getName();
                    String columnName = null;
                    if (StringUtils.isEmpty(annotation.column())) {
                        columnName = StringUtils.humpToLine2(fieldName);
                    } else {
                        columnName = annotation.column().replaceAll(" ", "");
                    }
                    Element child = new Element("result");
                    child.setAttribute("property", fieldName);
                    child.setAttribute("javaType", javaType);
                    child.setAttribute("column", columnName);
                    if (!StringUtils.isEmpty(annotation.jdbcType())) {
                        child.setAttribute("jdbcType", annotation.jdbcType().replaceAll(" ", "").toUpperCase());
                    }
                    resultMap.addContent(child);
                } else {
                    continue;
                }
            }
            root.addContent(resultMap);
        }
    }
}