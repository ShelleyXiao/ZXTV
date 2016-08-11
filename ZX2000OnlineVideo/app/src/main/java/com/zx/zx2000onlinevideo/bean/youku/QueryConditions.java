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

public class QueryConditions {

    private static final String[] PeiodStrArr = {
            "today", "week", "month", "history"
    };

    private static final String[] OrdeyByStrArr = {
            "published",  "view-count",  "comment-count","favorite-count", "reference-count", "favorite-time"
    };


    public enum PeiodCondition {
        Today, Week, Month, History, Empty
    }

    public enum OrdeyByCondition {
        Published, ViewCount,  ConmmentCount, FavoriteCount, ReferenceCount, FavoriteTime , Empty
    }

    private static HashMap<PeiodCondition, String> PeiodConditionSet = new HashMap<>();
    private static HashMap<OrdeyByCondition, String> OrderByCondtionSet = new HashMap<>();

    static {
        addPeiodConditionMap(PeiodCondition.Today, PeiodStrArr[0]);
        addPeiodConditionMap(PeiodCondition.Week, PeiodStrArr[1]);
        addPeiodConditionMap(PeiodCondition.Month, PeiodStrArr[2]);
        addPeiodConditionMap(PeiodCondition.History, PeiodStrArr[3]);
    }

    static {
        addOrdeyByConditionMap(OrdeyByCondition.ViewCount, OrdeyByStrArr[0]);
        addOrdeyByConditionMap(OrdeyByCondition.Published, OrdeyByStrArr[1]);
        addOrdeyByConditionMap(OrdeyByCondition.ConmmentCount, OrdeyByStrArr[2]);
        addOrdeyByConditionMap(OrdeyByCondition.ReferenceCount, OrdeyByStrArr[3]);
        addOrdeyByConditionMap(OrdeyByCondition.FavoriteCount, OrdeyByStrArr[4]);
        addOrdeyByConditionMap(OrdeyByCondition.FavoriteTime, OrdeyByStrArr[5]);
    }

    private PeiodCondition mPeiodCondition = PeiodCondition.Empty;
    private OrdeyByCondition mOrdeyByCondition = OrdeyByCondition.Empty;

    public QueryConditions() {
    }

    public QueryConditions(PeiodCondition period, OrdeyByCondition orderby) {
        this.mPeiodCondition = period;
        this.mOrdeyByCondition = orderby;
    }

    public void setPeiodCondition(PeiodCondition peiodCondition) {
        mPeiodCondition = peiodCondition;
    }

    public void setOrdeyByCondition(OrdeyByCondition ordeyByCondition) {
        mOrdeyByCondition = ordeyByCondition;
    }

    public String getPeiodCondition(PeiodCondition condition) {
        if(null != condition) {
            return PeiodConditionSet.get(condition);
        }

        return null;
    }

    public String getPeiodCondition() {
        if(mPeiodCondition == PeiodCondition.Empty ) {
            return null;
        }
        return PeiodConditionSet.get(mPeiodCondition);
    }

    public String getOrderbyCondition(PeiodCondition condition) {
        if(null != condition) {
            return OrderByCondtionSet.get(condition);
        }

        return null;
    }

    public String getOrderbyCondition() {
        if(mOrdeyByCondition == OrdeyByCondition.Empty) {
            return null;
        }
        return OrderByCondtionSet.get(mOrdeyByCondition);
    }

    private static void addPeiodConditionMap(PeiodCondition key, String value) {
        PeiodConditionSet.put(key, value);
    }

    private static void addOrdeyByConditionMap(OrdeyByCondition key, String value) {
        OrderByCondtionSet.put(key, value);
    }

}
