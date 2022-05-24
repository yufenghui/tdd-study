package com.yufenghui.tdd.springboot.request;

import com.vchangyi.boot.common.request.PageRequest;

/**
 * UserPageRequest
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/24 10:42
 */
public class UserPageRequest extends PageRequest {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
