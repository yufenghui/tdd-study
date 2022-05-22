package com.yufenghui.tdd.args;

import com.yufenghui.tdd.args.option.BooleanOption;
import com.yufenghui.tdd.args.option.ListOptions;
import com.yufenghui.tdd.args.option.Options;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO
 *
 * @author yufenghui
 * @date 2022/5/19 22:05
 */
public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    // [-l], [-p, 8080], [-d, /usr/logs]

    /**
     * Single Option:
     * TODO - Boolean: -l
     */
    @Test
    public void should_set_bool_option_to_true_if_args_present() {

        BooleanOption option = Args.parse(BooleanOption.class, "-l");

        assertNotNull(option);
        assertTrue(option.isLogging());

    }

    @Test
    public void should_set_bool_option_to_false_if_args_not_present() {

        BooleanOption option = Args.parse(BooleanOption.class);

        assertNotNull(option);
        assertFalse(option.isLogging());

    }

    /*
     * TODO - Integer: -p 8080
     * TODO - String: -d /usr/logs
     *
     * Multi Option:
     * TODO - -l -p 8080 -d /usr/logs
     *
     * Sad Path:
     * TODO - Boolean: -l p / -l f t
     * TODO - Integer: -p/ -p 8080 8081
     * TODO - String: -d/ -d /usr/logs /var/logs
     *
     * Default Value:
     * TODO - Boolean: false
     * TODO - Integer: 0
     * TODO - String: ""
     *
     */

    @Test
    @Disabled
    public void should_example_1() {

        Options options = Args.parse(Options.class, "-l", "-p", "8080", "-d", "/usr/logs");

        assertTrue(options.isLogging());
        assertEquals(8080, options.getPort());
        assertEquals("/usr/logs", options.getDirectory());
    }


    // -g this is a list -d 1 2 3 5
    @Test
    @Disabled
    public void should_example_2() {

        ListOptions options = Args.parse(ListOptions.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "3", "5");

        assertArrayEquals(new String[]{"this", "is", "a", "list"}, options.getGroup());
        assertArrayEquals(new int[]{1, 2, 3, 5}, options.getDecimals());
    }

}
