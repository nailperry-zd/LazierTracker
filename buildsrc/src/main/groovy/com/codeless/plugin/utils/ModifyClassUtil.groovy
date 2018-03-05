package com.codeless.plugin.utils

import com.codeless.plugin.MethodCell
import com.codeless.plugin.ReWriterConfig
import org.objectweb.asm.*

/**
 * Created by bryansharp(bsp0911932@163.com) on 2016/5/10.
 * Modified by nailperry on 2017/3/2.
 *
 */
public class ModifyClassUtil {

    public
    static byte[] modifyClasses(String className, byte[] srcByteCode) {
        byte[] classBytesCode = null;
        try {
            Log.info("====start modifying ${className}====");
            classBytesCode = modifyClass(srcByteCode);
            Log.info("====revisit modified ${className}====");
            onlyVisitClassMethod(classBytesCode);
            Log.info("====finish modifying ${className}====");
            return classBytesCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (classBytesCode == null) {
            classBytesCode = srcByteCode;
        }
        return classBytesCode;
    }


    private
    static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor methodFilterCV = new MethodFilterClassVisitor(classWriter);
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG);
        return classWriter.toByteArray();
    }

    private
    static void onlyVisitClassMethod(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodFilterClassVisitor methodFilterCV = new MethodFilterClassVisitor(classWriter);
        methodFilterCV.onlyVisit = true;
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG);
    }

    private static boolean instanceOfFragment(String superName) {
        return superName.equals('android/app/Fragment') || superName.equals('android/support/v4/app/Fragment')
    }

    /**
     *
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param start 方法参数起始索引（ 0：this，1+：普通参数 ）
     *
     * @param count 方法参数个数
     *
     * @param paramOpcodes 参数类型对应的ASM指令
     *
     */
    private
    static void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName, String methodDesc, int start, int count, List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[i - start], i);
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false);
    }

    static class MethodFilterClassVisitor extends ClassVisitor {
        public boolean onlyVisit = false;
        public HashSet<String> visitedFragMethods = new HashSet<>()// 无需判空
        private String superName
        private String[] interfaces
        private ClassVisitor classVisitor

        public MethodFilterClassVisitor(
                final ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
            this.classVisitor = cv
        }

        @Override
        void visitEnd() {
            Log.logEach('* visitEnd *');

            if (instanceOfFragment(superName)) {
                MethodVisitor mv;
                // 添加剩下的方法，确保super.onHiddenChanged(hidden);等先被调用
                Iterator<Map.Entry<String, MethodCell>> iterator = ReWriterConfig.sFragmentMethods.entrySet().iterator()
                while (iterator.hasNext()) {
                    Map.Entry<String, MethodCell> entry = iterator.next()
                    String key = entry.getKey()
                    MethodCell methodCell = entry.getValue()

                    if (visitedFragMethods.contains(key))
                        continue
                    mv = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, methodCell.name, methodCell.desc, null, null);
                    mv.visitCode();
                    // call super
                    visitMethodWithLoadedParams(mv, Opcodes.INVOKESPECIAL, superName, methodCell.name, methodCell.desc, methodCell.paramsStart, methodCell.paramsCount, methodCell.opcodes)
                    // call injected method
                    visitMethodWithLoadedParams(mv, Opcodes.INVOKESTATIC, ReWriterConfig.sAgentClassName, methodCell.agentName, methodCell.agentDesc, methodCell.paramsStart, methodCell.paramsCount, methodCell.opcodes)
                    mv.visitInsn(Opcodes.RETURN);
                    mv.visitMaxs(methodCell.paramsCount, methodCell.paramsCount);
                    mv.visitEnd();
                }
            }
            super.visitEnd()
        }

        @Override
        void visitAttribute(Attribute attribute) {
            Log.logEach('* visitAttribute *', attribute, attribute.type, attribute.metaClass, attribute.metaPropertyValues, attribute.properties);
            super.visitAttribute(attribute)
        }

        @Override
        AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            Log.logEach('* visitAnnotation *', desc, visible);
            return super.visitAnnotation(desc, visible)
        }

        @Override
        void visitInnerClass(String name, String outerName,
                             String innerName, int access) {
            Log.logEach('* visitInnerClass *', name, outerName, innerName, Log.accCode2String(access));
            super.visitInnerClass(name, outerName, innerName, access)
        }

        @Override
        void visitOuterClass(String owner, String name, String desc) {
            Log.logEach('* visitOuterClass *', owner, name, desc);
            super.visitOuterClass(owner, name, desc)
        }

        @Override
        void visitSource(String source, String debug) {
            Log.logEach('* visitSource *', source, debug);
            super.visitSource(source, debug)
        }

        @Override
        FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            Log.logEach('* visitField *', Log.accCode2String(access), name, desc, signature, value);
            return super.visitField(access, name, desc, signature, value)
        }

        @Override
        public void visit(int version, int access, String name,
                          String signature, String superName, String[] interfaces) {
            Log.logEach('* visit *', Log.accCode2String(access), name, signature, superName, interfaces);
            this.superName = superName
            this.interfaces = interfaces
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name,
                                         String desc, String signature, String[] exceptions) {
            MethodVisitor myMv = null;
            if (!onlyVisit) {
                Log.logEach("* visitMethod *", Log.accCode2String(access), name, desc, signature, exceptions);
            }
            if (interfaces != null && interfaces.length > 0) {
                MethodCell methodCell = ReWriterConfig.sInterfaceMethods.get(name + desc)
                if (methodCell != null && interfaces.contains(methodCell.parent)) {
                    if (onlyVisit) {
                        myMv = new MethodLogVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
                    } else {
                        try {
                            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                            myMv = new MethodLogVisitor(methodVisitor) {
                                @Override
                                void visitCode() {
                                    super.visitCode();
                                    visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, ReWriterConfig.sAgentClassName, methodCell.agentName, methodCell.agentDesc, methodCell.paramsStart, methodCell.paramsCount, methodCell.opcodes)
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            myMv = null
                        }
                    }
                }
            }

            if (instanceOfFragment(superName)) {
                MethodCell methodCell = ReWriterConfig.sFragmentMethods.get(name + desc)
                if (methodCell != null) {
                    // 记录该方法已存在
                    visitedFragMethods.add(name + desc)
                    if (onlyVisit) {
                        myMv = new MethodLogVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
                    } else {
                        try {
                            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                            myMv = new MethodLogVisitor(methodVisitor) {

                                @Override
                                void visitInsn(int opcode) {

                                    // 确保super.onHiddenChanged(hidden);等先被调用
                                    if (opcode == Opcodes.RETURN) { //在返回之前安插代码
                                        visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, ReWriterConfig.sAgentClassName, methodCell.agentName, methodCell.agentDesc, methodCell.paramsStart, methodCell.paramsCount, methodCell.opcodes)
                                    }
                                    super.visitInsn(opcode);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            myMv = null
                        }
                    }
                }
            }
            if (myMv != null) {
                if (onlyVisit) {
                    Log.logEach("* revisitMethod *", Log.accCode2String(access), name, desc, signature);
                }
                return myMv;
            } else {
                return cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }
}