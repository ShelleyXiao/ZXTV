package com.zx.zx2000onlinevideo.bean.youku.category;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 10:57
 * Company: zx
 * Description: 视频分类实体
 * FIXME
 */
public class VideoCategorySet {

    /**
     * term : Sports
     * label : 体育
     * lang : zh_CN
     */

    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
