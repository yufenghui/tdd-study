package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.CyclicDependencyFoundException;
import com.yufenghui.tdd.di.exception.DependencyNotFoundException;
import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContainerTest
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/23 12:53
 */
public class ContainerTest {

    Context context;

    @BeforeEach
    public void setup() {
        context = new Context();
    }

    @Nested
    public class ComponentConstruction {
        // TODO: instance
        @Test
        public void should_bind_type_to_specific_instance() {
            Component instance = new Component() {
            };
            context.bind(Component.class, instance);

            assertSame(instance, context.get(Component.class).get());
        }

        // TODO: abstract class
        // TODO: interface
        // TODO: component not exist
        @Test
        public void shoud_return_empty_if_component_not_exist() {
            Optional<Component> component = context.get(Component.class);
            assertTrue(component.isEmpty());
        }

        @Nested
        public class ConstructorInjection {
            // TODO: no args constructor
            @Test
            public void should_bind_type_to_class_with_default_constructor() {

                context.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component instance = context.get(Component.class).get();

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            // TODO: with dependencies
            @Test
            public void should_bind_type_to_class_with_injected_constructor() {
                Dependency dependency = new Dependency() {
                };

                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);

                Component instance = context.get(Component.class).get();
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            // TODO: A -> B -> C
            @Test
            public void should_bind_type_to_class_with_transitive_dependency() {
                String stringDependency = "string dependency";
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyWithInjectConstructor.class);
                context.bind(String.class, stringDependency);

                Component instance = context.get(Component.class).get();
                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);
                assertEquals(stringDependency, ((DependencyWithInjectConstructor) dependency).getDependency());
            }

            // TODO: sad path
            // TODO: multi inject constructors
            @Test
            public void should_throw_exception_if_multi_inject_constructors_exist() {
                assertThrows(IllegalComponentException.class, () -> {
                    context.bind(Component.class, ComponentWithMultiInjectConstructor.class);
                });
            }

            // TODO: no default constructor and inject constructor - bind
            @Test
            public void should_throw_exception_if_no_inject_constructor_nor_default_constructor_exist() {
                assertThrows(IllegalComponentException.class, () -> {
                    context.bind(Component.class, ComponentWithNoInjectNorDefaultConstructor.class);
                });
            }

            // TODO: dependencies not exist
            @Test
            public void should_throw_exception_if_dependencies_not_exist() {
                context.bind(Component.class, ComponentWithConstructorNoDependencyExist.class);
                assertThrows(DependencyNotFoundException.class, () -> {
                    context.get(Component.class);
                });
            }

            // TODO: cyclic dependencies
            @Test
            public void should_throw_exception_if_cyclic_dependencies_found() {
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyDependOnComponent.class);

                assertThrows(CyclicDependencyFoundException.class, () -> context.get(Component.class));
            }


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

interface Component {

}

interface Dependency {

}

class ComponentWithDefaultConstructor implements Component {

    public ComponentWithDefaultConstructor() {
    }

}

class ComponentWithInjectConstructor implements Component {

    private Dependency dependency;

    @Inject
    public ComponentWithInjectConstructor(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }

}

class ComponentWithMultiInjectConstructor implements Component {

    private String name;

    private Integer age;

    @Inject
    public ComponentWithMultiInjectConstructor(String name) {
        this.name = name;
    }

    @Inject
    public ComponentWithMultiInjectConstructor(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

}

class ComponentWithNoInjectNorDefaultConstructor implements Component {

    private String name;

    public ComponentWithNoInjectNorDefaultConstructor(String name) {
        this.name = name;
    }
}

class ComponentWithConstructorNoDependencyExist implements Component {

    private String name;

    @Inject
    public ComponentWithConstructorNoDependencyExist(String name) {
        this.name = name;
    }

}

class DependencyWithInjectConstructor implements Dependency {

    private String dependency;

    @Inject
    public DependencyWithInjectConstructor(String dependency) {
        this.dependency = dependency;
    }

    public String getDependency() {
        return dependency;
    }

}

class DependencyDependOnComponent implements Dependency {

    private Component component;

    @Inject
    public DependencyDependOnComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

}


