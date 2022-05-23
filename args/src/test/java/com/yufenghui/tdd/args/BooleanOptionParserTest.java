package com.yufenghui.tdd.args;

import com.yufenghui.tdd.args.annotation.Option;
import com.yufenghui.tdd.args.exception.ToManyArgumentsException;
import com.yufenghui.tdd.args.option.parser.BooleanOptionParser;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TODO
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/20 15:41
 */
public class BooleanOptionParserTest {

    /*
     * Sad Path:
     * TODO - Boolean: -l p / -l f t
     *
     */
    @Test
    public void should_not_accept_extra_argument_for_boolean_option() {

        assertThrows(ToManyArgumentsException.class,
                () -> new BooleanOptionParser().parse(option("l"), Arrays.asList("-l", "t")));

    }

    /*
     * Default Value:
     * TODO - Boolean: false
     *
     */
    @Test
    public void should_set_default_value_false_for_boolean_option() {
        boolean logging = (boolean) new BooleanOptionParser().parse(option("l"), Arrays.asList());
        assertFalse(logging);
    }


    static Option option(String value) {

        return new Option() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Option.class;
            }

            @Override
            public String value() {
                return value;
            }
        };
    }

}
