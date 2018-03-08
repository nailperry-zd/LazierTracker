package com.codeless.tracker.utils;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.codeless.tracker.ConfigConstants;
import com.codeless.tracker.PluginAgent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangdan on 2018/3/5.
 */

public class PathUtil {
    private static final int NO_POSITION = -1;
    public final static String EXCEPTION = "Exception";
    private static final int MAX_CLASS_NAME_CACHE_SIZE = 255;
    public static final ClassNameCache sClassnameCache = new ClassNameCache(MAX_CLASS_NAME_CACHE_SIZE);

    public static String getResIdName(Context context, View view) {
        final int viewId = view.getId();
        if (-1 == viewId) {
            return null;
        } else {
            String name = ResourceReader.Ids.getInstance(context).nameForId(viewId);
            return name;
        }
    }

    public static Object getDataObj(Object obj, String dataPath) {
        // 按照路径dataPath搜集数据
        String[] paths = dataPath.split("\\.");
        Object refer = obj;
        for (int j = 0; j < paths.length; j++) {
            String path = paths[j];
            switch (path) {
                case ConfigConstants.START_THIS:
                    refer = obj;
                    break;
                case ConfigConstants.START_ITEM:
                    if (obj instanceof View) {
                        // 路径的起点
                        View view = (View) obj;
                        Object viewParent = view.getParent();
                        if (ReflectorUtil.isInstanceOfV7RecyclerView(viewParent)) {
                            android.support.v7.widget.RecyclerView recyclerView = (android.support.v7.widget.RecyclerView) viewParent;
                            RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(view);
                            refer = vh;
                        }
                    }
                    break;
                case ConfigConstants.KEY_CONTEXT:
                    refer = ((View) refer).getContext();
                    break;
                case ConfigConstants.KEY_PARENT:

                    break;
                default:
                    // 反射获取变量值
                    refer = ReflectUtil.getObjAttr(path, refer);
                    break;
            }
        }

        return refer;
    }

    public static String getViewPath(View view) {
        HashMap<Integer, Pair<Integer, String>> aliveFragments = getAliveFragments();
        StringBuilder builder = new StringBuilder();
        ViewParent parent = view.getParent();
        View child = view;
        while (parent != null && parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            // 根据parent推算[index]部分
            String validIndexSegment = "";
            if (group instanceof AdapterView) {
                validIndexSegment = buildAdapterViewItemIndex(child, group);
            } else if (ReflectorUtil.isInstanceOfV7RecyclerView(group)) {
                validIndexSegment = buildRecyclerViewItemIndex(child, group);
            } else if (ReflectorUtil.isInstanceOfV4ViewPager(group)) {
                validIndexSegment = buildViewPagerItemIndex(aliveFragments, child, (ViewPager) group);
            } else {
                int index = getChildIndex(group, child);
                String indexStr = (index == NO_POSITION ? "-" : String.valueOf(index));
                validIndexSegment = "[" + indexStr + "]";
            }

            String elementFrag = buildFragmentSegment(aliveFragments, child, validIndexSegment);
            if (TextUtils.isEmpty(elementFrag)) {
                StringBuilder element = new StringBuilder();
                element.append("/").append(child.getClass().getSimpleName())
                        .append(validIndexSegment);
                String childDistinctId = getResIdName(group.getContext().getApplicationContext(), child);
                if (childDistinctId != null) {
                    element.append("#").append(childDistinctId);
                }
                builder.insert(0, element.toString());
                if ("android:content".equals(childDistinctId)) {
                    break;
                }
            } else {
                builder.insert(0, elementFrag);
            }

            child = group;
            parent = group.getParent();
        }
        return builder.insert(0, getMainWindowType()).toString();
    }

    private static String buildViewPagerItemIndex(HashMap<Integer, Pair<Integer, String>> aliveFragments, View child, ViewPager group) {
        int index = NO_POSITION;
        // ViewPager
        ViewPager _group = group;
        try {
            if (!ReflectorUtil.isV4ViewPagerCached) {
                ReflectorUtil.cacheV4ViewPager();// reflects fields and caches the result
            }
            if (ReflectorUtil.isV4ViewPagerCached) {
                List items = (List) ReflectorUtil.fieldmItems.get(_group);
                int position = _group.getCurrentItem();
                for (int i = 0; items != null && i < items.size(); i++) {
                    Object item = items.get(i);
                    int itemPosition = (int) ReflectorUtil.fieldPosition.get(item);
                    if (itemPosition == position) {
                        Object currPagerObject = ReflectorUtil.fieldObject.get(item);
                        boolean isViewFromObject = _group.getAdapter().isViewFromObject(child, currPagerObject);
                        Log.d("ViewPagerItemView", "@items.size = " + items.size() + ", @isViewFromObject = " + isViewFromObject + ", @position = " + position + ", @child = " + child.getClass().getSimpleName());
                        if (isViewFromObject) {
                            index = position;
                            if (currPagerObject instanceof Fragment || ReflectorUtil.isInstanceOfV4Fragment(currPagerObject)) {
                                int viewCode = (currPagerObject instanceof Fragment) ? ((Fragment) currPagerObject).getView().hashCode() : ((android.support.v4.app.Fragment) currPagerObject).getView().hashCode();
                                aliveFragments.put(currPagerObject.hashCode(), new Pair<Integer, String>(viewCode, currPagerObject.getClass().getSimpleName()));
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(EXCEPTION, e.getLocalizedMessage());
        }
        if (index == NO_POSITION) {
            index = getChildIndex(group, child);
        }
        return "[" + index + "]";
    }

    private static String buildRecyclerViewItemIndex(View child, ViewGroup group) {
        int index = getChildPositionForRecyclerView(child, group);
        return "[" + index + "]";
    }

    private static String buildAdapterViewItemIndex(View child, ViewGroup group) {
        int index = ((AdapterView) group).getPositionForView(child);
        // ExpandableListView
        if (group instanceof ExpandableListView) {
            StringBuilder element = new StringBuilder();
            String exListIndicator = "";
            ExpandableListView _group = (ExpandableListView) group;
            long l = _group.getExpandableListPosition(index);
            int groupIndex;
            if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_NULL) {
                if (index < _group.getHeaderViewsCount()) {
                    exListIndicator = "[header:" + index + "]";// header
                } else {
                    groupIndex = index - (_group.getCount() - _group.getFooterViewsCount());
                    exListIndicator = "[footer:" + groupIndex + "]";// footer
                }
            } else {
                groupIndex = ExpandableListView.getPackedPositionGroup(l);
                int childIndex = ExpandableListView.getPackedPositionChild(l);
                if (childIndex != -1) {
                    exListIndicator = "[group:" + groupIndex + ",child:" + childIndex + "]";// group/child
                } else {
                    exListIndicator = "[group:" + groupIndex + "]";// group
                }
            }
            Log.d("ExpandableListViewItem", "@index = " + index + ", @exListIndicator = " + exListIndicator);
            return exListIndicator;
        }

        return "[" + index + "]";
    }

    /**
     * 判断child是否是Fragment View实例
     *
     * @param aliveFragments
     * @param child
     * @return
     */
    private static String buildFragmentSegment(HashMap<Integer, Pair<Integer, String>> aliveFragments, View child, String validIndexSegment) {
        // deal with Fragment
        StringBuilder element = new StringBuilder();
        if (aliveFragments != null) {
            Iterator<Map.Entry<Integer, Pair<Integer, String>>> iterator = aliveFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Pair<Integer, String>> entry = iterator.next();
                Pair<Integer, String> pair = entry.getValue();
                int viewCode = pair.first;
                String fragName = pair.second;
                if (viewCode == child.hashCode()) {
                    element.append("/")
                            .append(fragName)
                            .append(validIndexSegment);
                    Log.d(fragName, "@fragIndex = " + validIndexSegment + "@view = " + child + ", @fragment = " + fragName);
                    break;
                }
            }
        }
        return element.toString();
    }

    /**
     * Gets info of currentActivity's Fragments.
     *
     * @return HashMap<fragmentHashCode, Pair<fragmentViewCode, simpleClassName></>>
     */
    private static HashMap<Integer, Pair<Integer, String>> getAliveFragments() {
        return PluginAgent.sAliveFragMap;
    }

    public static int getChildPositionForRecyclerView(View child, ViewGroup group) {
        if ((ReflectorUtil.isV7RecyclerViewLoaded) && ((group instanceof android.support.v7.widget.RecyclerView))) {
            android.support.v7.widget.RecyclerView localRecyclerView = (android.support.v7.widget.RecyclerView) group;
            if (ReflectorUtil.hasChildAdapterPosition) {
                return localRecyclerView.getChildAdapterPosition(child);
            }
            return localRecyclerView.getChildPosition(child);
        }
        if ((ReflectorUtil.isV7RecyclerViewCached) && (group.getClass().equals(ReflectorUtil.sClassRecyclerView))) {
            try {
                return ((Integer) ReflectorUtil.methodItemPosition.invoke(group, new Object[]{child})).intValue();
            } catch (Exception e) {
                Log.e(EXCEPTION, e.getLocalizedMessage());
            }
        }
        return NO_POSITION;
    }

    private static int getChildIndex(ViewGroup parent, View child) {
        if (parent == null) {
            return NO_POSITION;
        }

        final String childIdName = getResIdName(parent.getContext().getApplicationContext(), child);
        if (!TextUtils.isEmpty(childIdName)) {
            return 0;
        }

        String childClassName = sClassnameCache.get(child.getClass());
        int index = 0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View brother = parent.getChildAt(i);

            if (!hasClassName(brother, childClassName)) {
                continue;
            }

            String brotherIdName = getResIdName(parent.getContext().getApplicationContext(), brother);

            if (null != childIdName && !childIdName.equals(brotherIdName)) {
                continue;
            }

            if (brother == child) {
                return index;
            }

            index++;
        }

        return NO_POSITION;
    }

    public static boolean hasClassName(Object o, String className) {
        Class<?> klass = o.getClass();
        while (klass.getCanonicalName() != null) {
            if (klass.getCanonicalName().equals(className)) {
                return true;
            }

            if (klass == Object.class) {
                break;
            }

            klass = klass.getSuperclass();
        }
        return false;
    }

    public static String getMainWindowType() {
        return "/MainWindow";
    }

    /**
     * 当条件满足时，将返回true，否则返回false
     *
     * @param currViewPath
     * @param viewPath
     * @return
     */
    public static boolean match(String currViewPath, String viewPath) {
        if (TextUtils.isEmpty(currViewPath) || TextUtils.isEmpty(viewPath)) {
            return false;
        }
        try {
            Pattern pattern = Pattern.compile(viewPath);
            Matcher matcher = pattern.matcher(currViewPath);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
