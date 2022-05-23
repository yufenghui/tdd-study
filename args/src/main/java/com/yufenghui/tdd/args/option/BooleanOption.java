package com.yufenghui.tdd.args.option;

import com.yufenghui.tdd.args.annotation.Option;

/**
 * TODO
 *
 * @author yufenghui
 * @date 2022/5/19 22:38
 */
public class BooleanOption {

    @Option("l")
    private boolean logging;

    public BooleanOption(boolean logging) {
        this.logging = logging;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

}
