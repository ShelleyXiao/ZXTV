package com.zx.zx2000onlinevideo.ui.fragment.showCategory;

import com.zx.zx2000onlinevideo.ui.fragment.LazyFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ShaudXiao
 * Date: 2016-08-09
 * Time: 10:55
 * Company: zx
 * Description:
 * FIXME
 */

public class FragmentFactory {

    public static final int NEWS = 0;
    public static final int MOVIVE = 1;
    public static final int TV = 2;
    public static final int VARITEY = 3;
    public static final int SPORTS = 4;
    public static final int ANIMS = 5;
    public static final int DOCUMENTARY = 6;


    private static Map<Integer, LazyFragment> mFragmentCache = new HashMap<>();

    public static LazyFragment createFragment(int postion) {
        LazyFragment fragment = mFragmentCache.get(postion);
        if(null == fragment) {
            switch (postion) {
                case NEWS:
                    fragment = new NewsFragments();
                    break;
                case MOVIVE:
                    fragment = new MovieFragment();
                    break;
                case TV:
                    fragment = new TVFragment();
                    break;
                case VARITEY:
                    fragment = new VarietyFragment();
                    break;
                case SPORTS:
                    fragment = new SportFragment();
                    break;
                case ANIMS:
                    fragment = new AnimFragments();
                    break;
                case DOCUMENTARY:
                    fragment = new DocumentaryFragment();
                    break;
                default:
                    fragment = new NewsFragments();
                    break;
            }
        }

        return fragment;

    }

    public static void clear(){
        mFragmentCache.clear();
    }
}
