package com.yufenghui.tdd.args;

import com.yufenghui.tdd.args.option.*;
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
     */
    @Test
    public void should_parse_int_as_option_value() {

        IntOption option = Args.parse(IntOption.class, "-p", "8080");

        assertNotNull(option);
        assertEquals(8080, option.getPort());
    }


    /*
     * TODO - String: -d /usr/logs
     *
     */
    @Test
    public void should_parse_string_as_option_value() {

        StringOption option = Args.parse(StringOption.class, "-d", "/usr/logs");

        assertNotNull(option);
        assertEquals("/usr/logs", option.getDirectory());
    }


    /*
     * Multi Option:
     * TODO - -l -p 8080 -d /usr/logs
     *
     */
    @Test
    public void should_parse_multi_options() {

        MultiOptions multiOptions = Args.parse(MultiOptions.class, "-l", "-p", "8080", "-d", "/usr/logs");

        assertTrue(multiOptions.isLogging());
        assertEquals(8080, multiOptions.getPort());
        assertEquals("/usr/logs", multiOptions.getDirectory());
    }

    /*
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

    // -g this is a list -d 1 2 3 5
    @Test
    @Disabled
    public void should_example_2() {

        ListOptions options = Args.parse(ListOptions.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "3", "5");

        assertArrayEquals(new String[]{"this", "is", "a", "list"}, options.getGroup());
        assertArrayEquals(new int[]{1, 2, 3, 5}, options.getDecimals());
    }

}
