package com.bryansharp.gradle.hibeaver

import com.bryansharp.gradle.hibeaver.utils.MethodLogAdapter;
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

    private static List<Map<String, Object>> sClickMatchMaps = [
            ['methodName': 'onClick', 'methodDesc': '(Landroid/view/View;)V', 'adapter': {
                ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                    MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                        @Override
                        void visitCode() {
                            super.visitCode();
                            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, sAgentClassName, "onClick", "(Landroid/view/View;)V");
                        }
                    }
                    return adapter;
            }]
    ];

    private static List<Map<String, Object>> sFragmentMatchMaps = new ArrayList<>()

    static {
        HashMap<MethodCell, MethodCell> fragmentAddMethods = getFragmentAddMethods()
        fragmentAddMethods.each {
            MethodCell key = it.getKey()
            MethodCell value = it.getValue()
            Map<String, Object> map = new HashMap<>()
            map.put('methodName', key.name)
            map.put('methodDesc', key.desc)
            map.put('adapter', {
                ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                    MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                        @Override
                        void visitInsn(int opcode) {

                            // 确保super.onHiddenChanged(hidden);等先被调用
                            if (opcode == Opcodes.RETURN) { //在返回之前安插代码
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                                if (value.desc.contains('Z')) {
                                    // (this,bool)
                                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                                }
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, sAgentClassName, value.name, value.desc);
                            }
                            super.visitInsn(opcode);
                        }
                    }
                    return adapter;
            })
            sFragmentMatchMaps.add(map)
        }
    }

    public static List<Map<String, Object>> getClickReWriter() {
        return sClickMatchMaps
    }

    public static List<Map<String, Object>> getFragmentReWriter(String fragmntFullClassName) {
        return sFragmentMatchMaps;
    }

}
