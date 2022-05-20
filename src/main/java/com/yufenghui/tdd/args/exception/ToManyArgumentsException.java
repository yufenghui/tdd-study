package com.yufenghui.tdd.args.exception;

import java.util.List;

/**
 * TODO
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/20 15:54
 */
public class ToManyArgumentsException extends RuntimeException {

    private final List<String> arguments;

    public ToManyArgumentsException(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " argument size: " + arguments.size();
    }

}
