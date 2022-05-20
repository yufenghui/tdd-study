package com.yufenghui.tdd.args.option;

import com.yufenghui.tdd.args.annotation.Option;

/**
 * TODO
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/20 11:51
 */
public class IntOption {

    @Option("p")
    private int port;

    public IntOption(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
