package com.yufenghui.tdd.springboot.service;

import com.vchangyi.boot.common.response.PageResponse;
import com.vchangyi.boot.data.mybatisplus.service.IService;
import com.yufenghui.tdd.springboot.model.User;
import com.yufenghui.tdd.springboot.request.UserPageRequest;

/**
 * TODO
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/23 17:39
 */
public interface UserService extends IService<User> {

    PageResponse<User> pageUser(UserPageRequest request);

}
