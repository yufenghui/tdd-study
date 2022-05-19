package com.yufenghui.tdd.args.option;

import com.yufenghui.tdd.args.annotation.Option;

/**
 * TODO
 *
 * @author yufenghui
 * @date 2022/5/19 22:05
 */
public class ListOptions {

    @Option("g")
    private String[] group;

    @Option("d")
    private int[] decimals;


    public ListOptions(String[] group, int[] decimals) {
        this.group = group;
        this.decimals = decimals;
    }

    public String[] getGroup() {
        return group;
    }

    public void setGroup(String[] group) {
        this.group = group;
    }

    public int[] getDecimals() {
        return decimals;
    }

    public void setDecimals(int[] decimals) {
        this.decimals = decimals;
    }

}
