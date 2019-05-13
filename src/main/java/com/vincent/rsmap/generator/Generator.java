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
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

public class Generator {

    public static void generator(List<Class> classes, String packageName,String toFile) throws Exception {
        System.out.println(Generator.class.getResource("/").getPath());
        toFile = System.getProperty("user.dir") + "/" + toFile;
        File path = new File(toFile);
        if (!path.exists()) {
            path.mkdirs();
            System.out.println("create file:" + path.getPath());
        }

        Document document = null;

        try {
            Element root = new Element("mapper");
            root.setAttribute("namespace", packageName + "." + "CommonResultMapper");

            document = new Document(root);
            DocType docType = new DocType("mapper", "-//mybatis.org/DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
            document.setDocType(docType);
            for (Class clz : classes) {
                processClass(root, clz,packageName);
            }

            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.setFormat(outputter.getFormat().setEncoding("UTF-8"));

            String systemPath = Generator.class.getResource("/").getPath();
            outputter.output(document,new FileOutputStream(toFile + File.separator  + "CommonResultMapper.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processClass(Element root, Class clzz,String packageName) throws Exception {
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
                    String javaType = field.getType().getSimpleName().toLowerCase();
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
                        child.setAttribute("jdbcType", annotation.jdbcType().replaceAll(" ", ""));
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