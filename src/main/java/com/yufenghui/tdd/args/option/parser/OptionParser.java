package com.yufenghui.tdd.args.option.parser;

import com.yufenghui.tdd.args.annotation.Option;

import java.util.List;

/**
 * OptionParser
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/20 14:24
 */
public interface OptionParser {

    Object parse(Option option, List<String> arguments);

}
