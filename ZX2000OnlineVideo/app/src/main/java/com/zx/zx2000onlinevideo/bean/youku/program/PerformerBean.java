package com.zx.zx2000onlinevideo.bean.youku.program;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:33
 * Company: zx
 * Description: 演员
 * FIXME
 */

public class PerformerBean {
    private String id;
    private String name;
    private String thumburl;
    private List<String> character;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumburl() {
        return thumburl;
    }

    public void setThumburl(String thumburl) {
        this.thumburl = thumburl;
    }

    public List<String> getCharacter() {
        return character;
    }

    public void setCharacter(List<String> character) {
        this.character = character;
    }
}
