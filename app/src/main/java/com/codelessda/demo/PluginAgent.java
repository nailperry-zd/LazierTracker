package com.codelessda.demo;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;

/**
 * Created by zhangdan on 17/3/3.
 *
 * todo 在该类中添加要注入的具体代码
 */

public class PluginAgent {

    private static final String TAG = PluginAgent.class.getSimpleName();

    public static void onClick(View view) {
        Log.d(TAG, "我被成功注入啦");
    }

    public static void onClick(Object object, DialogInterface dialogInterface, int which) {

    }

    public static void onItemClick(Object object, AdapterView parent, View view, int position, long id) {

    }

    public static void onItemSelected(Object object, AdapterView parent, View view, int position, long id) {
        onItemClick(object, parent, view, position, id);
    }

    public static void onGroupClick(Object thisObject, ExpandableListView parent, View v, int groupPosition, long id) {

    }

    public static void onChildClick(Object thisObject, ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

    }

    public static void onStopTrackingTouch(Object thisObj, SeekBar seekBar) {

    }

    public static void onRatingChanged(Object thisObj, RatingBar ratingBar, float rating, boolean fromUser) {

    }

    public static void onCheckedChanged(Object object, RadioGroup radioGroup, int checkedId) {

    }

    public static void onCheckedChanged(Object object, CompoundButton button, boolean isChecked) {

    }


    public static void onFragmentResume(Object obj) {

    }

    public static void onFragmentPause(Object obj) {

    }

    private static boolean checkFragment(android.support.v4.app.Fragment paramFragment) {
        return true;
    }

    private static boolean checkFragment(android.app.Fragment paramFragment) {
        return true;
    }

    public static void setFragmentUserVisibleHint(Object fragmentObject, boolean isUserVisibleHint) {

    }

    public static void onFragmentHiddenChanged(Object fragment, boolean hidden) {
        setFragmentUserVisibleHint(fragment, !hidden);
    }

}
