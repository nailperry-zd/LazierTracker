package com.netease.mobidroid.plugin

import com.netease.mobidroid.plugin.utils.MethodLogVisitor;
import org.objectweb.asm.*


/**
 * Created by zhangdan on 17/3/3.
 */

public class ReWriterAgent {

    public static String sAgentClassName = 'com/netease/mobidroid/PluginAgent'

    public static HashMap<MethodCell, MethodCell> getFragmentAddMethods() {
        HashMap<MethodCell, MethodCell> fragmentAddMethods = new HashMap<>()
        fragmentAddMethods.put(new MethodCell("onResume", "()V"), new MethodCell("onFragmentResume", "(Ljava/lang/Object;)V"));
        fragmentAddMethods.put(new MethodCell("onPause", "()V"), new MethodCell("onFragmentPause", "(Ljava/lang/Object;)V"));
        fragmentAddMethods.put(new MethodCell("setUserVisibleHint", "(Z)V"), new MethodCell("setFragmentUserVisibleHint", "(Ljava/lang/Object;Z)V"));
        fragmentAddMethods.put(new MethodCell("onHiddenChanged", "(Z)V"), new MethodCell("onFragmentHiddenChanged", "(Ljava/lang/Object;Z)V"));
        return fragmentAddMethods
    }

    private static Map<String, Object> sClickMatchMap =
            ['methodName': 'onClick', 'methodDesc': '(Landroid/view/View;)V', 'methodVisitor': {
                ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                    MethodVisitor methodLogVisitor = new MethodLogVisitor(methodVisitor) {

                        @Override
                        void visitCode() {
                            super.visitCode();
                            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, sAgentClassName, "onClick", "(Landroid/view/View;)V", false);
                        }
                    }
                    return methodLogVisitor;
            }];

    private static Map<String, Object> sDialogClickMatchMap =
            ['methodName': 'onClick', 'methodDesc': '(Landroid/content/DialogInterface;I)V', 'methodVisitor': {
                ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                    MethodVisitor methodLogVisitor = new MethodLogVisitor(methodVisitor) {

                        @Override
                        void visitCode() {
                            super.visitCode();
                            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                            methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
                            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, sAgentClassName, "onClick", "(Ljava/lang/Object;Landroid/content/DialogInterface;I)V", false);
                        }
                    }
                    return methodLogVisitor;
            }];

    private static List<Map<String, Object>> sFragmentMatchMaps = new ArrayList<>()

    static {
        HashMap<MethodCell, MethodCell> fragmentAddMethods = getFragmentAddMethods()
        fragmentAddMethods.each {
            MethodCell key = it.getKey()
            MethodCell value = it.getValue()
            Map<String, Object> map = new HashMap<>()
            map.put('methodName', key.name)
            map.put('methodDesc', key.desc)
            map.put('methodVisitor', {
                ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                    MethodVisitor methodLogVisitor = new MethodLogVisitor(methodVisitor) {

                        @Override
                        void visitInsn(int opcode) {

                            // 确保super.onHiddenChanged(hidden);等先被调用
                            if (opcode == Opcodes.RETURN) { //在返回之前安插代码
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                                if (value.desc.contains('Z')) {
                                    // (this,bool)
                                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                                }
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, sAgentClassName, value.name, value.desc, false);
                            }
                            super.visitInsn(opcode);
                        }
                    }
                    return methodLogVisitor;
            })
            sFragmentMatchMaps.add(map)
        }
    }

    public static Map<String, Object> getClickReWriter() {
        return sClickMatchMap
    }

    public static Map<String, Object> getDialogClickReWriter() {
        return sDialogClickMatchMap
    }

    public static List<Map<String, Object>> getFragmentReWriter() {
        return sFragmentMatchMaps;
    }

}
