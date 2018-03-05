package com.codeless.plugin

import com.android.annotations.NonNull
import com.android.annotations.Nullable
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created by bryansharp(bsp0911932@163.com) on 2016/5/7.
 * Modified by nailperry on 2017/3/2.
 */
public class InjectTransform extends Transform {
    static AppExtension android
    // our plugin will inject 'butterknife.internal.DebouncingOnClickListener' by default.
    static HashSet<String> targetPackages = ['butterknife.internal.DebouncingOnClickListener'];
    private static Project project;

    public InjectTransform(Project project) {
        InjectTransform.project = project
    }

    @Override
    String getName() {
        return "LazierTracker"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    public void transform(
            @NonNull Context context,
            @NonNull Collection<TransformInput> inputs,
            @NonNull Collection<TransformInput> referencedInputs,
            @Nullable TransformOutputProvider outputProvider,
            boolean isIncremental) throws IOException, TransformException, InterruptedException {
        com.codeless.plugin.utils.Log.info "==============${project.codelessConfig.pluginName + ' '}transform enter=============="
        android = project.extensions.getByType(AppExtension)
//        String flavorAndBuildType = context.name.split("For")[1]
//        Log.info("flavorAndBuildType ${flavorAndBuildType}")

        // our plugin will inject app package by default.
        String appPackageName = getAppPackageName()
        if (appPackageName != null) {
            targetPackages.add(appPackageName)
        }

        // 3rd party JAR packages that want our plugin to inject.
        HashSet<String> inputPackages = project.codelessConfig.targetPackages
        if (inputPackages != null) {
            targetPackages.addAll(inputPackages);
            com.codeless.plugin.utils.Log.info "==============@targetPackages = ${targetPackages}=============="
        }

        /**
         * 获取所有依赖的classPaths
         */
        def classPaths = []
        String buildTypes
        String productFlavors
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                classPaths.add(directoryInput.file.absolutePath)
                buildTypes = directoryInput.file.name
                productFlavors = directoryInput.file.parentFile.name
                com.codeless.plugin.utils.Log.info("项目包含的class文件夹：${directoryInput.file.absolutePath}");
            }
            com.codeless.plugin.utils.Log.info('===============================')
            input.jarInputs.each { JarInput jarInput ->
                classPaths.add(jarInput.file.absolutePath)
                com.codeless.plugin.utils.Log.info("项目包含的jar包：${jarInput.file.absolutePath}");
            }
        }

        def paths = [android.bootClasspath.get(0).absolutePath/*, injectClassPath*/]
        paths.addAll(classPaths)
        /**遍历输入文件*/
        inputs.each { TransformInput input ->
            /**
             * 遍历jar
             * JarInput和DirectoryInput两个接口都继承自QualifiedContent这个接口
             * 他们的scope属性（枚举，类型为QualifiedContent.Scope）表明这个Input所属的类型可见源码注释
             * @see QualifiedContent.Scope
             */
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.file.name;
                /** 重名名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8);
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4);
                }
                /** 获得输出文件*/
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR);
                def modifiedJar = null;
                if (isJarNeedModify(jarInput.file)) {
                    modifiedJar = modifyJarFile(jarInput.file, context.getTemporaryDir());
                }
                if (modifiedJar == null) {
                    modifiedJar = jarInput.file;
                } else {
                    saveModifiedJarForCheck(modifiedJar);
                }
                FileUtils.copyFile(modifiedJar, dest);
            }
            /**
             * 遍历目录
             */
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
//                Log.info("dest dir  ${dest.absolutePath}")
                File dir = directoryInput.file
                if (dir) {
                    HashMap<String, File> modifyMap = new HashMap<>();
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                        File classFile ->
                            File modified = modifyClassFile(dir, classFile, context.getTemporaryDir());
                            if (modified != null) {
                                //key为相对路径
                                modifyMap.put(classFile.absolutePath.replace(dir.absolutePath, ""), modified);
                            }
                    }
                    FileUtils.copyDirectory(directoryInput.file, dest);
                    modifyMap.entrySet().each {
                        Map.Entry<String, File> en ->
                            File target = new File(dest.absolutePath + en.getKey());
                            com.codeless.plugin.utils.Log.info(target.getAbsolutePath());
                            if (target.exists()) {
                                target.delete();
                            }
                            FileUtils.copyFile(en.getValue(), target);
                            saveModifiedJarForCheck(en.getValue());
                            en.getValue().delete();
                    }
                }
            }
        }
    }

    private static void saveModifiedJarForCheck(File optJar) {
        File dir = com.codeless.plugin.utils.DataHelper.ext.pluginTmpDir;
        File checkJarFile = new File(dir, optJar.getName());
        if (checkJarFile.exists()) {
            checkJarFile.delete();
        }
        FileUtils.copyFile(optJar, checkJarFile);
    }

    /**
     * 只扫描特定包下的类
     * @param className 形如 android.app.Fragment 的类名
     * @return
     */
    static boolean shouldModifyClass(String className) {
        if (project.codelessConfig.enableModify && targetPackages != null) {
            Iterator<String> iterator = targetPackages.iterator()
            // 注意，闭包里的return语句相当于continue，不会跳出遍历，故用while或for
            while (iterator.hasNext()) {
                String packagename = iterator.next()
                if (className.contains(packagename)) {
                    return (!className.contains("R\$") && !className.endsWith("R") && !className.endsWith("BuildConfig"))
                }
            }
        }
        return false
    }

    /**
     * 植入代码
     * @param buildDir 是项目的build class目录,就是我们需要注入的class所在地
     * @param lib 这个是hackdex的目录,就是AntilazyLoad类的class文件所在地
     */
    public static File modifyJarFile(File jarFile, File tempDir) {
        if (jarFile) {
            /** 设置输出到的jar */
            def hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8);
            def optJar = new File(tempDir, hexName + jarFile.name)
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));
            /**
             * 读取原jar
             */
            def file = new JarFile(jarFile);
            Enumeration enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                InputStream inputStream = file.getInputStream(jarEntry);

                String entryName = jarEntry.getName();
                String className

                ZipEntry zipEntry = new ZipEntry(entryName);

                jarOutputStream.putNextEntry(zipEntry);

                byte[] modifiedClassBytes = null;
                byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
                if (entryName.endsWith(".class")) {
                    className = path2Classname(entryName)
                    if (shouldModifyClass(className)) {
                        modifiedClassBytes = com.codeless.plugin.utils.ModifyClassUtil.modifyClasses(className, sourceClassBytes);
                    }
                }
                if (modifiedClassBytes == null) {
                    jarOutputStream.write(sourceClassBytes);
                } else {
                    jarOutputStream.write(modifiedClassBytes);
                }
                jarOutputStream.closeEntry();
            }
            com.codeless.plugin.utils.Log.info("${hexName} is modified");
            jarOutputStream.close();
            file.close();
            return optJar;
        }
        return null;
    }

    private static String path2Classname(String entryName) {
        entryName.replace(File.separator, ".").replace(".class", "")
    }

    public static File modifyClassFile(File dir, File classFile, File tempDir) {
        File modified;
        try {
            String className = path2Classname(classFile.absolutePath.replace(dir.absolutePath + File.separator, ""));
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile));
            if (shouldModifyClass(className)) {
                byte[] modifiedClassBytes = com.codeless.plugin.utils.ModifyClassUtil.modifyClasses(className, sourceClassBytes);
                if (modifiedClassBytes) {
                    modified = new File(tempDir, className.replace('.', '') + '.class')
                    if (modified.exists()) {
                        modified.delete();
                    }
                    modified.createNewFile()
                    new FileOutputStream(modified).write(modifiedClassBytes)
                }
            }
        } catch (Exception e) {
        }
        return modified;

    }
    /**
     * 该jar文件是否包含需要修改的类
     * @param jarFile
     * @return
     */
    public static boolean isJarNeedModify(File jarFile) {
        boolean modified = false;
        if (targetPackages != null && targetPackages.size() > 0) {
            if (jarFile) {
                /**
                 * 读取原jar
                 */
                def file = new JarFile(jarFile);
                Enumeration enumeration = file.entries();
                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                    String entryName = jarEntry.getName();
                    String className
                    if (entryName.endsWith(".class")) {
                        className = entryName.replace("/", ".").replace(".class", "")
                        if (shouldModifyClass(className)) {
                            modified = true;
                            break;
                        }
                    }
                }
                file.close();
            }
        }
        return modified;
    }

    private static void writeStreamWithBuffer(InputStream inputStream, OutputStream out) {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * 获取应用程序包名
     * @return
     */
    private static String getAppPackageName() {
        String packageName
        try {
            def manifestFile = android.sourceSets.main.manifest.srcFile
            com.codeless.plugin.utils.Log.info("XmlParser manifestFile: " + manifestFile)
            packageName = new XmlParser().parse(manifestFile).attribute('package')
            com.codeless.plugin.utils.Log.info("XmlParser packageName: " + packageName)
        } catch (Exception e) {
            com.codeless.plugin.utils.Log.info("XmlParser Exception: " + e.getMessage())
        }
        return packageName
    }
}
