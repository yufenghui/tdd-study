package com.yufenghui.tdd.args.option.parser;

import com.yufenghui.tdd.args.annotation.Option;

import java.util.List;
import java.util.function.Function;

/**
 * StringOptionParser
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/20 14:30
 */
public class SingleValueOptionParser<T> implements OptionParser {

    Function<String, T> valueParser;

    public SingleValueOptionParser(Function<String, T> valueParser) {
        this.valueParser = valueParser;
    }

    @Override
    public Object parse(Option option, List<String> arguments) {
        int index = arguments.indexOf("-" + option.value());
        String value = arguments.get(index + 1);
        return valueParser.apply(value);
    }

}
