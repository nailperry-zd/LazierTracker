package com.codelessda.plugin

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
        sInterfaceMethods.put('onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V', new MethodCell(
                'onItemClick',
                '(Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                'android/widget/AdapterView$OnItemClickListener',
                'onItemClick',
                '(Ljava/lang/Object;Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                0, 5))
        sInterfaceMethods.put('onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V', new MethodCell(
                'onItemSelected',
                '(Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                'android/widget/AdapterView$OnItemSelectedListener',
                'onItemSelected',
                '(Ljava/lang/Object;Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                0, 5))
        sInterfaceMethods.put('onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z', new MethodCell(
                'onGroupClick',
                '(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z',
                'android/widget/ExpandableListView$OnGroupClickListener',
                'onGroupClick',
                '(Ljava/lang/Object;Landroid/widget/ExpandableListView;Landroid/view/View;IJ)V',
                0, 5))
        sInterfaceMethods.put('onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z', new MethodCell(
                'onChildClick',
                '(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z',
                'android/widget/ExpandableListView$OnChildClickListener',
                'onChildClick',
                '(Ljava/lang/Object;Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)V',
                0, 6))
        sInterfaceMethods.put('onRatingChanged(Landroid/widget/RatingBar;FZ)V', new MethodCell(
                'onRatingChanged',
                '(Landroid/widget/RatingBar;FZ)V',
                'android/widget/RatingBar$OnRatingBarChangeListener',
                'onRatingChanged',
                '(Ljava/lang/Object;Landroid/widget/RatingBar;FZ)V',
                0, 4))
        sInterfaceMethods.put('onStopTrackingTouch(Landroid/widget/SeekBar;)V', new MethodCell(
                'onStopTrackingTouch',
                '(Landroid/widget/SeekBar;)V',
                'android/widget/SeekBar$OnSeekBarChangeListener',
                'onStopTrackingTouch',
                '(Ljava/lang/Object;Landroid/widget/SeekBar;)V',
                0, 2))
        sInterfaceMethods.put('onCheckedChanged(Landroid/widget/CompoundButton;Z)V', new MethodCell(
                'onCheckedChanged',
                '(Landroid/widget/CompoundButton;Z)V',
                'android/widget/CompoundButton$OnCheckedChangeListener',
                'onCheckedChanged',
                '(Ljava/lang/Object;Landroid/widget/CompoundButton;Z)V',
                0, 3))
        sInterfaceMethods.put('onCheckedChanged(Landroid/widget/RadioGroup;I)V', new MethodCell(
                'onCheckedChanged',
                '(Landroid/widget/RadioGroup;I)V',
                'android/widget/RadioGroup$OnCheckedChangeListener',
                'onCheckedChanged',
                '(Ljava/lang/Object;Landroid/widget/RadioGroup;I)V',
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
