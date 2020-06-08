package com.vincent.rsmap;

import com.vincent.rsmap.pojo.User;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeanToTable {

    private static final Map<Class,String> type = new HashMap<Class, String>();
    static {
        type.put(Long.class," bigint(8) ");
        type.put(String.class," VARCHAR(64) ");
        type.put(Date.class," timestamp ");
        type.put(Integer.class," int(4) ");
    }

    public static void main(String[] args) {
        Class clazz = User.class;
        StringBuilder sb = new StringBuilder("create table `").append(humpToLine2(clazz.getSimpleName())).append("`").append( "(\n");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            sb.append(humpToLine2(field.getName())).append(type.get(field.getType()));
            if ("id".equals(field.getName())) {
                sb.append("not null primary key ");
            } else {
                if (field.getType() == Date.class && field.getName().contains("create")) {
                    sb.append("not null default current_timestamp ");
                } else if (field.getType() == Date.class && field.getName().contains("update")) {
                    sb.append("null default current_timestamp on update current_timestamp ");
                } else if (field.getName().equals("status")) {
                    sb.append("default 1");
                } else{
                    sb.append("default null");
                }
            }
            sb.append(" comment '',\n");
        }
        sb.append("primary key(id),\n");
        sb.append(") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;  ");
        System.out.println(sb);

    }

    /** 驼峰转下划线,效率比上面高 */
    public static String humpToLine2(String str) {
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
