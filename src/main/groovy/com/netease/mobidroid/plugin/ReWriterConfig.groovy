package com.netease.mobidroid.plugin

/**
 * Created by zhangdan on 17/3/3.
 */

public class ReWriterConfig {

    public static String sAgentClassName = 'com/netease/mobidroid/PluginAgent'

//    String name
//    String desc
//    String parent
//    String agentName
//    String agentDesc
//    String paramsStart
//    String paramsCount

    /**
     * interface中的方法
     */
    public final static HashMap<String, MethodCell> sInterfaceMethods = new HashMap<>()
    static {
        sInterfaceMethods.put('onClick(Landroid/view/View;)V', new MethodCell(
                'onClick',
                '(Landroid/view/View;)V',
                'android/view/View$OnClickListener',
                'onClick',
                '(Landroid/view/View;)V',
                1, 1))
        sInterfaceMethods.put('onClick(Landroid/content/DialogInterface;I)V', new MethodCell(
                'onClick',
                '(Landroid/content/DialogInterface;I)V',
                'android/content/DialogInterface$OnClickListener',
                'onClick',
                '(Ljava/lang/Object;Landroid/content/DialogInterface;I)V',
                0, 3))
        // Todo: 扩展
    }

    /**
     * Fragment中的方法
     */
    public final static HashMap<String, MethodCell> sFragmentMethods = new HashMap<>()
    static {
        sFragmentMethods.put('onResume()V', new MethodCell(
                'onResume',
                '()V',
                '',// parent省略，均为 android/app/Fragment 或 android/support/v4/app/Fragment
                'onFragmentResume',
                '(Ljava/lang/Object;)V',
                0, 1))
        sFragmentMethods.put('onPause()V', new MethodCell(
                'onPause',
                '()V',
                '',
                'onFragmentPause',
                '(Ljava/lang/Object;)V',
                0, 1))
        sFragmentMethods.put('setUserVisibleHint(Z)V', new MethodCell(
                'setUserVisibleHint',
                '(Z)V',
                '',// parent省略，均为 android/app/Fragment 或 android/support/v4/app/Fragment
                'setFragmentUserVisibleHint',
                '(Ljava/lang/Object;Z)V',
                0, 2))
        sFragmentMethods.put('onHiddenChanged(Z)V', new MethodCell(
                'onHiddenChanged',
                '(Z)V',
                '',
                'onFragmentHiddenChanged',
                '(Ljava/lang/Object;)V',
                0, 2))
    }

}
