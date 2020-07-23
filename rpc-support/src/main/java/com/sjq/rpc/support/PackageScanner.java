package com.sjq.rpc.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PackageScanner {

    private static final ClassLoader cl = Thread.currentThread().getContextClassLoader();

    public static List<Class> scanInterfaceByPackagePathAndAnnotaion(String packagePath, Class[] annotations) {
        boolean annotationFilter = Objects.nonNull(annotations) && annotations.length > 0;
        List<Class> classes = scanClassByPackagePath(packagePath).stream().filter(cls -> {
            if (cls.isInterface() && !cls.isAnnotation()) {
                if (annotationFilter) {
                    for (Class annotationCls : annotations) {
                        if (Objects.isNull(cls.getAnnotation(annotationCls))) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        return classes;
    }

    public static List<Class> scanClassByPackagePathAndAnnotaion(String packagePath, Class[] annotations) {
        boolean annotationFilter = Objects.nonNull(annotations) && annotations.length > 0;
        List<Class> scanClasses = scanClassByPackagePath(packagePath).stream().filter(cls -> {
            if (!cls.isInterface() && !cls.isAnnotation()) {
                if (annotationFilter) {
                    for (Class annotationCls : annotations) {
                        if (Objects.isNull(cls.getAnnotation(annotationCls))) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return scanClasses;
    }

    public static List<Class> scanClassByPackagePath(String packagePath) {
        //File root = new File(PackageScanner.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("(?:file:)?/{1}", ""));

        List<Class> classes = new ArrayList<>();
        List<File> directories = new ArrayList();//需要扫描的目录
        List<String> rootPaths = new ArrayList<>();//项目的根路径
        Enumeration<URL> fileUrls = null;
        Enumeration<URL> rootUrls = null;
        try {
            fileUrls = Thread.currentThread().getContextClassLoader().getResources(packagePath.replaceAll("\\.", "/"));
            rootUrls = Thread.currentThread().getContextClassLoader().getResources("");
        } catch (IOException e) {
            e.printStackTrace();
            return classes;
        }
        while (fileUrls.hasMoreElements()) {
            URL url = fileUrls.nextElement();
            File file = new File(url.getFile().replaceFirst("(?:file:)?/{1}", ""));
            if (file.isDirectory()) {
                directories.add(file);
            }
        }
        while (rootUrls.hasMoreElements()) {
            URL url = rootUrls.nextElement();
            rootPaths.add(new File(url.getFile().replaceFirst("(?:file:)?/{1}", "")).getPath());
        }

        for (;;) {
            List<File> directoriesTemp = new ArrayList<>();
            for (File parent : directories) {
                for (File child : parent.listFiles()) {
                    if (child.isDirectory()) {
                        directoriesTemp.add(child);
                    } else if (child.getName().endsWith("class")) {
                        for (String rootPath : rootPaths) {
                            //剔除项目根路径
                            String path = child.getPath().replace(rootPath, "");
                            if (path.length() != child.getPath().length()) {
                                try {
                                    //转换成com.sjq.rpc这种格式,并加载class
                                    classes.add(cl.loadClass(path.substring(1, path.length() - 6).replace(File.separator, ".")));
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (directoriesTemp.size() > 0) {
                directories.clear();
                directories = directoriesTemp;
            } else {
                break;
            }
        }
        return classes;
    }

}
