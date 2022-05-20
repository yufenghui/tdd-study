package com.yufenghui.tdd.args.option;

import com.yufenghui.tdd.args.annotation.Option;

/**
 * TODO
 *
 * @author yufenghui
 * @date 2022/5/19 22:05
 */
public class MultiOptions {

    @Option("l")
    private boolean logging;

    @Option("p")
    private int port;

    @Option("d")
    private String directory;


    public MultiOptions(boolean logging, int port, String directory) {
        this.logging = logging;
        this.port = port;
        this.directory = directory;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
