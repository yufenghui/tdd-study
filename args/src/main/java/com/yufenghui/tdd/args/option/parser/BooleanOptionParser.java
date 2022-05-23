package com.yufenghui.tdd.args.option.parser;

import com.yufenghui.tdd.args.annotation.Option;
import com.yufenghui.tdd.args.exception.ToManyArgumentsException;

import java.util.List;

/**
 * TODO
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/20 14:25
 */
public class BooleanOptionParser implements OptionParser {

    @Override
    public Object parse(Option option, List<String> arguments) {
        int index = arguments.indexOf("-" + option.value());
        if(index + 1 < arguments.size() && !arguments.get(index + 1).startsWith("-")) {
            throw new ToManyArgumentsException(arguments);
        }

        return index != -1;
    }

}
