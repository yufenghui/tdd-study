package com.yufenghui.tdd.args;

import com.yufenghui.tdd.args.annotation.Option;
import com.yufenghui.tdd.args.option.parser.BooleanOptionParser;
import com.yufenghui.tdd.args.option.parser.OptionParser;
import com.yufenghui.tdd.args.option.parser.SingleValueOptionParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Args
 *
 * @author yufenghui
 * @date 2022/5/19 22:34
 */
public class Args {

    private static Map<Class<?>, OptionParser> PARSERS = new HashMap<>();

    static {
        PARSERS.put(boolean.class, new BooleanOptionParser());
        PARSERS.put(String.class, new SingleValueOptionParser<>(String::valueOf));
        PARSERS.put(int.class, new SingleValueOptionParser<>(Integer::parseInt));
    }


    public static <T> T parse(Class<T> optionsClass, String... args) {
        try {
            Constructor<?> constructor = optionsClass.getConstructors()[0];
            Field[] fields = optionsClass.getDeclaredFields();

            Object[] parameters = Arrays.stream(fields).map(f -> parseOption(f, args)).toArray();

            return (T) constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseOption(Field field, String[] args) {
        Option option = field.getAnnotation(Option.class);
        List<String> argsList = Arrays.asList(args);
        Class<?> type = field.getType();

        OptionParser parser = PARSERS.get(type);

        return parser.parse(option, argsList);
    }


}
