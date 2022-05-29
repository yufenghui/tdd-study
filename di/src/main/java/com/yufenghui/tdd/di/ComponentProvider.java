package com.yufenghui.tdd.di;

import java.util.List;

/**
 * ComponentProvider
 *
 * @author yufenghui
 * @date 2022/5/29 20:58
 */
interface ComponentProvider<T> {

    T get(Context context);

    List<Class<?>> getDependencies();

}
