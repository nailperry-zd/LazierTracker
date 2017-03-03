package com.bryansharp.gradle.hibeaver.utils;

import org.objectweb.asm.*


/**
 * Created by zhangdan on 17/3/3.
 */

public class ReWriterAgent {


    public static List<Map<String, Object>> getClickReWriter() {
        List<Map<String, Object>> clickMatchMaps = [
                ['methodName': 'onClick', 'methodDesc': '(Landroid/view/View;)V', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/netease/demo/dabeaver/MainActivity", "hookXM", "(Landroid/view/View;)V");
                            }
                        }
                        return adapter;
                }]
        ];
        return clickMatchMaps
    }

    public static List<Map<String, Object>> getFragmentReWriter(String fragmntFullClassName) {

        List<Map<String, Object>> fragmentMatchMaps = [
                ['methodName': 'onResume', 'methodDesc': '()V', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/netease/demo/dabeaver/PluginAgent", "onFragmentResume", "(L" + fragmntFullClassName + ";)V");
                            }
                        }
                        return adapter;
                }],
                ['methodName': 'onPause', 'methodDesc': '()V', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/netease/demo/dabeaver/PluginAgent", "onFragmentPause", "(L" + fragmntFullClassName + ";)V");
                            }
                        }
                        return adapter;
                }],
                ['methodName': 'setUserVisibleHint', 'methodDesc': '(Z)V', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/netease/demo/dabeaver/PluginAgent", "setFragmentUserVisibleHint", "(Ljava/lang/Object;Z)V");
                            }
                        }
                        return adapter;
                }],
                ['methodName': 'onHiddenChanged', 'methodDesc': '(Z)V', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/netease/demo/dabeaver/PluginAgent", "onFragmentHiddenChanged", "(Ljava/lang/Object;Z)V");
                            }
                        }
                        return adapter;
                }]
        ];
        return fragmentMatchMaps;
    }

}
