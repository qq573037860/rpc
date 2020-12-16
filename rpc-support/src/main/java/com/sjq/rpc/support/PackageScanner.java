package com.sjq.rpc.support;

import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PackageScanner {

    private static final ClassLoader cl = Thread.currentThread().getContextClassLoader();

    public static List<Class> scanInterfaceByPackagePathAndAnnotaion(String packagePath, Class[] annotations) {
        final boolean annotationFilter = Objects.nonNull(annotations) && annotations.length > 0;
        return scanClassByPackagePath(packagePath).stream().filter(cls -> {
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
    }

    public static List<Class> scanClassByPackagePathAndAnnotaion(String packagePath, Class[] annotations) {
        final boolean annotationFilter = Objects.nonNull(annotations) && annotations.length > 0;
        return scanClassByPackagePath(packagePath).stream().filter(cls -> {
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
    }

    public static List<Class> scanClassByPackagePath(String packagePath) {
        //File root = new File(PackageScanner.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("(?:file:)?/{1}", ""));

        List<Class> classes = new ArrayList<>();
        List<File> directories = new ArrayList<>();//需要扫描的目录
        List<String> rootPaths = new ArrayList<>();//项目的根路径
        Enumeration<URL> fileUrls;
        Enumeration<URL> rootUrls;
        try {
            fileUrls = Thread.currentThread().getContextClassLoader().getResources(packagePath.replaceAll("\\.", "/"));
            rootUrls = Thread.currentThread().getContextClassLoader().getResources("");
        } catch (IOException e) {
            e.printStackTrace();
            return classes;
        }
        final String regex = "(?:file:)?/{1}";
        while (fileUrls.hasMoreElements()) {
            URL url = fileUrls.nextElement();
            File file = new File(url.getFile().replaceFirst(regex, ""));
            if (file.isDirectory()) {
                directories.add(file);
            }
        }
        while (rootUrls.hasMoreElements()) {
            URL url = rootUrls.nextElement();
            rootPaths.add(new File(url.getFile().replaceFirst(regex, "")).getPath());
        }

        for (;;) {
            List<File> directoriesTemp = new ArrayList<>();
            for (File parent : directories) {
                if (Objects.isNull(parent.listFiles()) || parent.listFiles().length == 0) {
                    continue;
                }
                for (File child : parent.listFiles()) {
                    if (child.isDirectory()) {
                        directoriesTemp.add(child);
                        continue;
                    }
                    if (child.getName().endsWith("class")) {
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
            if (CollectionUtils.isEmpty(directoriesTemp)) {
                break;
            }
            directories.clear();
            directories = directoriesTemp;
        }
        return classes;
    }

}
