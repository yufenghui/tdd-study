package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.CyclicDependencyFoundException;
import com.yufenghui.tdd.di.exception.DependencyNotFoundException;
import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
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

    ContextConfig config;

    @BeforeEach
    public void setup() {
        config = new ContextConfig();
    }

    @Nested
    public class ComponentConstruction {
        // TODO: instance
        @Test
        public void should_bind_type_to_specific_instance() {
            Component instance = new Component() {
            };
            config.bind(Component.class, instance);

            Context context = config.getContext();
            assertSame(instance, context.get(Component.class).get());
        }

        // TODO: abstract class
        abstract class AbstractComponent implements Component {
            @Inject
            public AbstractComponent() {
            }
        }

        @Test
        public void should_throw_exception_if_component_is_abstract() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectProvider<>(AbstractComponent.class));
        }

        // TODO: interface
        @Test
        public void should_throw_exception_if_component_is_interface() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectProvider<>(Component.class));
        }

        // TODO: component not exist
        @Test
        public void should_return_empty_if_component_not_exist() {
            Optional<Component> component = config.getContext().get(Component.class);
            assertTrue(component.isEmpty());
        }

        @Nested
        public class ConstructorInjection {
            // TODO: no args constructor
            @Test
            public void should_bind_type_to_class_with_default_constructor() {

                config.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component instance = config.getContext().get(Component.class).get();

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            // TODO: with dependencies
            @Test
            public void should_bind_type_to_class_with_injected_constructor() {
                Dependency dependency = new Dependency() {
                };

                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, dependency);

                Component instance = config.getContext().get(Component.class).get();
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            // TODO: A -> B -> C
            @Test
            public void should_bind_type_to_class_with_transitive_dependency() {
                String stringDependency = "string dependency";
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyWithInjectConstructor.class);
                config.bind(String.class, stringDependency);

                Component instance = config.getContext().get(Component.class).get();
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
                    config.bind(Component.class, ComponentWithMultiInjectConstructor.class);
                });
            }

            // TODO: no default constructor and inject constructor - bind
            @Test
            public void should_throw_exception_if_no_inject_constructor_nor_default_constructor_exist() {
                assertThrows(IllegalComponentException.class, () -> {
                    config.bind(Component.class, ComponentWithNoInjectNorDefaultConstructor.class);
                });
            }

            // TODO: dependencies not exist
            @Test
            public void should_throw_exception_if_dependencies_not_exist() {
                config.bind(Component.class, ComponentWithConstructorNoDependencyExist.class);
                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.getContext());

                assertEquals(String.class, exception.getDependency());
                assertEquals(Component.class, exception.getComponent());
            }

            @Test
            public void should_throw_exception_if_transitive_dependencies_not_exist() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyWithInjectConstructor.class);
                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.getContext());

                assertEquals(String.class, exception.getDependency());
                assertEquals(Dependency.class, exception.getComponent());
            }

            // TODO: cyclic dependencies
            @Test
            public void should_throw_exception_if_cyclic_dependencies_found() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyDependOnComponent.class);

                CyclicDependencyFoundException exception = assertThrows(CyclicDependencyFoundException.class, () -> config.getContext());

                List<Class<?>> components = List.of(exception.getComponents());

                assertEquals(2, components.size());
                assertTrue(components.contains(Component.class));
                assertTrue(components.contains(Dependency.class));
            }

            @Test
            public void should_throw_exception_if_transitive_cyclic_dependencies_found() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyDependOnAnotherDependency.class);
                config.bind(AnotherDependency.class, AnotherDependencyDependOnComponent.class);

                CyclicDependencyFoundException exception = assertThrows(CyclicDependencyFoundException.class, () -> config.getContext());
                List<Class<?>> components = List.of(exception.getComponents());

                assertEquals(3, components.size());
                assertTrue(components.contains(Component.class));
                assertTrue(components.contains(Dependency.class));
                assertTrue(components.contains(AnotherDependency.class));
            }


        }

        @Nested
        public class FieldInjection {

            // TODO: inject field
            @Test
            public void should_inject_dependencies_via_field() {
                Dependency dependency = new Dependency() {
                };

                config.bind(Dependency.class, dependency);
                config.bind(Component.class, ComponentWithFieldInject.class);

                Component component = config.getContext().get(Component.class).get();

                assertNotNull(component);
                assertSame(dependency, ((ComponentWithFieldInject) component).getDependency());
            }

            // TODO: throw exception if dependency not found

            // TODO: throw exception if field is final
            static class FinalInjectFieldComponent {
                @Inject
                final Dependency dependency = null;
            }

            @Test
            public void should_throw_exception_if_field_is_final() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectProvider<>(FinalInjectFieldComponent.class));
            }

            // TODO: throw exception if cyclic dependency

        }

        @Nested
        public class MethodInjection {

            static class ComponentWithMethodInject implements Component {

                private Dependency dependency;

                public Dependency getDependency() {
                    return dependency;
                }

                @Inject
                public void setDependency(Dependency dependency) {
                    this.dependency = dependency;
                }

            }

            static class ComponentWithNoArgsMethodInject implements Component {

                @Inject
                public String getVersion() {
                    return "1.0";
                }

            }

            static class ComponentWithMultiArgsMethodInject implements Component {

                private Dependency dependency;

                private AnotherDependency anotherDependency;

                public Dependency getDependency() {
                    return dependency;
                }

                public AnotherDependency getAnotherDependency() {
                    return anotherDependency;
                }

                @Inject
                public void setDependencies(Dependency dependency, AnotherDependency anotherDependency) {
                    this.dependency = dependency;
                    this.anotherDependency = anotherDependency;
                }

            }

            // TODO: inject method with no dependency will be called
            @Test
            public void should_inject_method_with_no_dependency_called() {
                config.bind(Component.class, ComponentWithNoArgsMethodInject.class);

                Component component = config.getContext().get(Component.class).get();

                assertNotNull(component);
                assertSame("1.0", ((ComponentWithNoArgsMethodInject) component).getVersion());
            }

            // TODO: inject method with dependency will be injected
            @Test
            public void should_inject_dependency_via_method() {
                Dependency dependency = new Dependency() {
                };

                config.bind(Dependency.class, dependency);
                config.bind(Component.class, ComponentWithMethodInject.class);

                Component component = config.getContext().get(Component.class).get();

                assertNotNull(component);
                assertSame(dependency, ((ComponentWithMethodInject) component).getDependency());
            }

            // TODO: inject method with multi dependency will be injected
            @Test
            public void should_inject_multi_dependency_via_method() {
                Dependency dependency = new Dependency() {
                };
                AnotherDependency anotherDependency = new AnotherDependency() {
                };

                config.bind(Dependency.class, dependency);
                config.bind(AnotherDependency.class, anotherDependency);
                config.bind(Component.class, ComponentWithMultiArgsMethodInject.class);

                Component component = config.getContext().get(Component.class).get();

                assertNotNull(component);
                assertSame(dependency, ((ComponentWithMultiArgsMethodInject) component).getDependency());
                assertSame(anotherDependency, ((ComponentWithMultiArgsMethodInject) component).getAnotherDependency());
            }

            // TODO: override inject method from super class
            static class SuperClassWithInjectMethod {
                int superCalled = 0;

                @Inject
                void install() {
                    superCalled++;
                }
            }

            static class SubClassWithInjectMethod extends SuperClassWithInjectMethod {
                int subCalled = 0;

                @Inject
                void installAnother() {
                    subCalled = superCalled + 1;
                }
            }

            @Test
            public void should_override_inject_method_from_super_class() {
                config.bind(SubClassWithInjectMethod.class, SubClassWithInjectMethod.class);

                SubClassWithInjectMethod component = config.getContext().get(SubClassWithInjectMethod.class).get();
                assertEquals(1, component.superCalled);
                assertEquals(2, component.subCalled);
            }

            static class SubclassOverrideSuperclassWithInject extends SuperClassWithInjectMethod {
                @Inject
                @Override
                void install() {
                    super.install();
                }
            }

            @Test
            public void should_only_call_once_if_subclass_override_superclass_inject_method() {
                config.bind(SubclassOverrideSuperclassWithInject.class, SubclassOverrideSuperclassWithInject.class);

                SubclassOverrideSuperclassWithInject component = config.getContext().get(SubclassOverrideSuperclassWithInject.class).get();
                assertEquals(1, component.superCalled);
            }

            static class SubclassOverrideSuperclassWithNoInject extends SuperClassWithInjectMethod {
                @Override
                void install() {
                    super.install();
                }
            }

            @Test
            public void should_not_call_inject_method_if_subclass_with_no_inject() {
                config.bind(SubclassOverrideSuperclassWithNoInject.class, SubclassOverrideSuperclassWithNoInject.class);

                SubclassOverrideSuperclassWithNoInject component = config.getContext().get(SubclassOverrideSuperclassWithNoInject.class).get();
                assertEquals(0, component.superCalled);
            }

            // TODO: include dependency from inject method
            @Test
            public void should_include_dependency_from_inject_method() {
                ConstructorInjectProvider<ComponentWithMethodInject> provider = new ConstructorInjectProvider<>(ComponentWithMethodInject.class);

                assertArrayEquals(new Class<?>[] {Dependency.class}, provider.getDependencies().toArray(Class<?>[]::new));
            }

            // TODO: throw exception if type parameter defined
            static class InjectMethodWithTypeParameter {
                @Inject
                <T> void install() {

                }
            }

            @Test
            public void should_throw_exception_if_method_with_type_parameter() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectProvider<>(InjectMethodWithTypeParameter.class));
            }

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

interface AnotherDependency {

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

class ComponentWithFieldInject implements Component {

    @Inject
    private Dependency dependency;

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
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

class DependencyDependOnAnotherDependency implements Dependency {

    private AnotherDependency anotherDependency;

    @Inject
    public DependencyDependOnAnotherDependency(AnotherDependency anotherDependency) {
        this.anotherDependency = anotherDependency;
    }

    public AnotherDependency getAnotherDependency() {
        return anotherDependency;
    }
}

class AnotherDependencyDependOnComponent implements AnotherDependency {

    private Component component;

    @Inject
    public AnotherDependencyDependOnComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

}


