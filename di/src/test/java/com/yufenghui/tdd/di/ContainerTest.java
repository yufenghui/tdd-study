package com.yufenghui.tdd.di;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContainerTest
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/23 12:53
 */
public class ContainerTest {

    interface Component {

    }

    static class ComponentWithDefaultConstructor implements Component {

        public ComponentWithDefaultConstructor() {
        }

    }


    @Nested
    public class ComponentConstruction {
        // TODO: instance
        @Test
        public void should_bind_type_to_specific_instance() {
            Context context = new Context();

            Component instance = new Component() {
            };
            context.bind(Component.class, instance);

            assertSame(instance, context.get(Component.class));
        }

        // TODO: abstract class
        // TODO: interface

        @Nested
        public class ConstructorInjection {
            // TODO: no args constructor
            @Test
            public void should_bind_type_to_default_constructor() {
                Context context = new Context();

                context.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component instance = context.get(Component.class);

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            // TODO: with dependencies
            // TODO: A -> B -> A

        }

        @Nested
        public class FieldInjection {

        }

        @Nested
        public class MethodInjection {

        }

    }

    @Nested
    public class DependencySelection {

    }

    @Nested
    public class LifecycleManagement {

    }

}
