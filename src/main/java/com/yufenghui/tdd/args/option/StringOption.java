package com.yufenghui.tdd.args.option;

import com.yufenghui.tdd.args.annotation.Option;

/**
 * TODO
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/20 14:02
 */
public class StringOption {

    @Option("d")
    private String directory;


    public StringOption(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

}
