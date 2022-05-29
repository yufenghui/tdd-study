package com.yufenghui.tdd.di;

import java.util.Optional;

/**
 * Context
 *
 * @author yufenghui
 * @date 2022/5/29 18:09
 */
public interface Context {

    <Type> Optional<Type> get(Class<Type> type);

}
