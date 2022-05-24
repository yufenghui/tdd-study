package com.yufenghui.tdd.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchangyi.boot.common.response.PageResponse;
import com.vchangyi.boot.data.mybatisplus.service.impl.ServiceImpl;
import com.yufenghui.tdd.springboot.mapper.UserMapper;
import com.yufenghui.tdd.springboot.model.User;
import com.yufenghui.tdd.springboot.request.UserPageRequest;
import com.yufenghui.tdd.springboot.service.UserService;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/23 17:53
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public PageResponse<User> pageUser(UserPageRequest request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("name");
        queryWrapper.like(StrUtil.isNotBlank(request.getName()), "name", request.getName());
        Page<User> userPage = this.page(page, queryWrapper);

        PageResponse<User> pageResponse = new PageResponse<>();
        BeanUtil.copyProperties(userPage, pageResponse);

        return pageResponse;
    }

}
