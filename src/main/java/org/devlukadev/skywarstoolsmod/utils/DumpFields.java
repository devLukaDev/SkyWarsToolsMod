package org.devlukadev.skywarstoolsmod.utils;

import java.lang.reflect.Field;

public class DumpFields {
    public static void dumpFields(Object obj) {
        if (obj == null) {
            System.out.println("null object");
            return;
        }
        Class<?> clazz = obj.getClass();
        System.out.println("=== Dumping fields for " + clazz.getName() + " ===");
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    System.out.println(field.getName() + " = " + value);
                } catch (IllegalAccessException e) {
                    System.out.println(field.getName() + " = <inaccessible: " + e.getMessage() + ">");
                }
            }
            clazz = clazz.getSuperclass();
        }
        System.out.println("=== End dump ===");
    }
}
