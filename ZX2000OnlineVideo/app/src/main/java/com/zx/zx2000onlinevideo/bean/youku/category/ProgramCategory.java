package com.zx.zx2000onlinevideo.bean.youku.category;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:53
 * Company: zx
 * Description:节目分类信息
 * FIXME
 */

public class ProgramCategory {

    /**
     * term : Sports
     * label : 体育
     * lang : zh_CN
     * genre : [{"term":"world-cup","label":"世界杯","lang":"zh_CN"},{"term":"the-asian-games","label":"亚运会","lang":"zh_CN"}]
     */

    private List<CategoriesBean> categories;

    public List<CategoriesBean> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoriesBean> categories) {
        this.categories = categories;
    }

    public static class CategoriesBean {
        private String term;
        private String label;
        private String lang;
        /**
         * term : world-cup
         * label : 世界杯
         * lang : zh_CN
         */

        private List<Category> genre;

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public List<Category> getGenre() {
            return genre;
        }

        public void setGenre(List<Category> genre) {
            this.genre = genre;
        }

    }
}
