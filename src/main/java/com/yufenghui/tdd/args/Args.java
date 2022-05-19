package com.yufenghui.tdd.args;

import com.yufenghui.tdd.args.annotation.Option;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author yufenghui
 * @date 2022/5/19 22:34
 */
public class Args {


    public static <T> T parse(Class<T> optionsClass, String... args) {
        try {
            Constructor<?> constructor = optionsClass.getConstructors()[0];

            Field logging = optionsClass.getDeclaredField("logging");
            Option option = logging.getAnnotation(Option.class);
            String optionValue = option.value();

            List<String> argsList = Arrays.asList(args);

            return (T) constructor.newInstance(argsList.contains("-" + optionValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
