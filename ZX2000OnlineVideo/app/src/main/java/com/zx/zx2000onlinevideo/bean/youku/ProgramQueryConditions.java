package com.zx.zx2000onlinevideo.bean.youku;

import java.util.HashMap;


/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 10:57
 * Company: zx
 * Description: 查询条件
 * FIXME
 */

public class ProgramQueryConditions {

    public static final String[] AreaStrArr = {
            "大陆", "香港", "其他"
    };

    private static final String[] OrdeyByStrArr = {
            "updated", "score","view-count","view-today-count", "view-week-count", "comment-count",  "favorite-count", "reference-count", " release-date",
    };


    public enum AreaCondition {
        China, Hongkong, Other, Empty
    }

    public enum OrdeyByCondition {
        Updated, Score, ViewCount,ViewTodayCount, ViewWeekCount, ConmmentCount, FavoriteCount,ReferenceCount,  ReleaseDate, Empty
    }

    private static HashMap<AreaCondition, String> AreaConditionSet = new HashMap<>();
    private static HashMap<OrdeyByCondition, String> OrderByCondtionSet = new HashMap<>();

    static {
        addAreaConditionMap(AreaCondition.China, AreaStrArr[0]);
        addAreaConditionMap(AreaCondition.Hongkong, AreaStrArr[1]);
        addAreaConditionMap(AreaCondition.Other, AreaStrArr[2]);
    }

    static {
        addOrdeyByConditionMap(OrdeyByCondition.ViewCount, OrdeyByStrArr[0]);
        addOrdeyByConditionMap(OrdeyByCondition.ConmmentCount, OrdeyByStrArr[1]);
        addOrdeyByConditionMap(OrdeyByCondition.ReferenceCount, OrdeyByStrArr[2]);
        addOrdeyByConditionMap(OrdeyByCondition.FavoriteCount, OrdeyByStrArr[3]);
        addOrdeyByConditionMap(OrdeyByCondition.ViewTodayCount, OrdeyByStrArr[4]);
        addOrdeyByConditionMap(OrdeyByCondition.ViewWeekCount, OrdeyByStrArr[5]);
        addOrdeyByConditionMap(OrdeyByCondition.ReleaseDate, OrdeyByStrArr[6]);
        addOrdeyByConditionMap(OrdeyByCondition.Score, OrdeyByStrArr[7]);
        addOrdeyByConditionMap(OrdeyByCondition.Updated, OrdeyByStrArr[8]);
    }

    private AreaCondition mAreaCondition = AreaCondition.Empty;
    private OrdeyByCondition mOrdeyByCondition = OrdeyByCondition.Empty;

    public ProgramQueryConditions() {
    }

    public ProgramQueryConditions(AreaCondition area, OrdeyByCondition orderby) {
        this.mAreaCondition = area;
        this.mOrdeyByCondition = orderby;
    }

    public void setAreaCondition(AreaCondition area) {
        mAreaCondition = area;
    }

    public void setOrdeyByCondition(OrdeyByCondition ordeyByCondition) {
        mOrdeyByCondition = ordeyByCondition;
    }

    public String getAreaCondition(AreaCondition condition) {
        if (null != condition) {
            return AreaConditionSet.get(condition);
        }

        return null;
    }

    public String getAreaCondition() {
        if (mAreaCondition == AreaCondition.Empty) {
            return null;
        }
        return AreaConditionSet.get(mAreaCondition);
    }

    public String getOrderbyCondition(AreaCondition condition) {
        if (null != condition) {
            return OrderByCondtionSet.get(condition);
        }

        return null;
    }

    public String getOrderbyCondition() {
        if (mOrdeyByCondition == OrdeyByCondition.Empty) {
            return null;
        }
        return OrderByCondtionSet.get(mOrdeyByCondition);
    }

    private static void addAreaConditionMap(AreaCondition key, String value) {
        AreaConditionSet.put(key, value);
    }

    private static void addOrdeyByConditionMap(OrdeyByCondition key, String value) {
        OrderByCondtionSet.put(key, value);
    }
}

