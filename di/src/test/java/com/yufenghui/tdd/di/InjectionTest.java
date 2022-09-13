package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

/**
 * InjectionTest
 *
 * @author yufenghui
 * @date 2022/9/13 21:38
 */
@Nested
public class InjectionTest {

    private Context context = Mockito.mock(Context.class);

    private Dependency dependency = Mockito.mock(Dependency.class);

    @BeforeEach
    public void setup() {
        Mockito.when(context.get(eq(Dependency.class))).thenReturn(Optional.of(dependency));
    }


    @Nested
    public class ConstructorInjection {


        @Nested
        class Injection {

            // TODO: no args constructor
            @Test
            public void should_call_default_constructor_if_no_inject_constructor() {
                ConstructorInjectProvider<ComponentWithDefaultConstructor> provider = new ConstructorInjectProvider<>(ComponentWithDefaultConstructor.class);
                Component instance = provider.get(context);

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            // TODO: with dependencies
            @Test
            public void should_inject_dependency_via_injected_constructor() {
                ConstructorInjectProvider<ComponentWithInjectConstructor> provider = new ConstructorInjectProvider<>(ComponentWithInjectConstructor.class);
                Component instance = provider.get(context);

                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            // TODO: A -> B -> C
            @Test
            public void should_inject_dependency_via_transitive_dependency() {
                String stringDependency = "string dependency";

                Mockito.when(context.get(Dependency.class)).thenReturn(
                        Optional.of(new DependencyWithInjectConstructor(stringDependency))
                );

                ConstructorInjectProvider<ComponentWithInjectConstructor> provider = new ConstructorInjectProvider<>(ComponentWithInjectConstructor.class);
                Component instance = provider.get(context);
                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);
                assertEquals(stringDependency, ((DependencyWithInjectConstructor) dependency).getDependency());
            }

        }

        @Nested
        class IllegalInjectConstructor {

            // TODO: sad path

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

            // TODO: multi inject constructors
            @Test
            public void should_throw_exception_if_multi_inject_constructors_exist() {
                assertThrows(IllegalComponentException.class, () -> {
                    new ConstructorInjectProvider<>(ComponentWithMultiInjectConstructor.class);
                });
            }

            // TODO: no default constructor and inject constructor - bind
            @Test
            public void should_throw_exception_if_no_inject_constructor_nor_default_constructor_exist() {
                assertThrows(IllegalComponentException.class, () -> {
                    new ConstructorInjectProvider<>(ComponentWithNoInjectNorDefaultConstructor.class);
                });
            }

        }

    }

    @Nested
    public class FieldInjection {

        @Nested
        class Injection {

            // TODO: inject field
            @Test
            public void should_inject_dependency_via_field() {
                ConstructorInjectProvider<ComponentWithFieldInject> provider = new ConstructorInjectProvider<>(ComponentWithFieldInject.class);
                Component component = provider.get(context);

                assertNotNull(component);
                assertSame(dependency, ((ComponentWithFieldInject) component).getDependency());
            }

        }

        @Nested
        class IllegalInjectField {

            // TODO: throw exception if dependency not found

            // TODO: throw exception if field is final
            static class FinalInjectFieldComponent {
                @Inject
                final Dependency dependency = null;
            }

            @Test
            public void should_throw_exception_if_field_is_final() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectProvider<>(IllegalInjectField.FinalInjectFieldComponent.class));
            }

            // TODO: throw exception if cyclic dependency

        }

    }

    @Nested
    public class MethodInjection {

        @Nested
        class Injection {

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
                ConstructorInjectProvider<ComponentWithNoArgsMethodInject> provider = new ConstructorInjectProvider<>(ComponentWithNoArgsMethodInject.class);
                Component component = provider.get(context);

                assertNotNull(component);
                assertSame("1.0", ((Injection.ComponentWithNoArgsMethodInject) component).getVersion());
            }

            // TODO: inject method with dependency will be injected
            @Test
            public void should_inject_dependency_via_method() {
                ConstructorInjectProvider<ComponentWithMethodInject> provider = new ConstructorInjectProvider<>(ComponentWithMethodInject.class);
                Component component = provider.get(context);

                assertNotNull(component);
                assertSame(dependency, ((Injection.ComponentWithMethodInject) component).getDependency());
            }

            // TODO: inject method with multi dependency will be injected
            @Test
            public void should_inject_multi_dependency_via_method() {
                AnotherDependency anotherDependency = new AnotherDependency() {
                };
                Mockito.when(context.get(AnotherDependency.class)).thenReturn(Optional.of(anotherDependency));

                ConstructorInjectProvider<ComponentWithMultiArgsMethodInject> provider = new ConstructorInjectProvider<>(ComponentWithMultiArgsMethodInject.class);
                Component component = provider.get(context);

                assertNotNull(component);
                assertSame(dependency, ((Injection.ComponentWithMultiArgsMethodInject) component).getDependency());
                assertSame(anotherDependency, ((Injection.ComponentWithMultiArgsMethodInject) component).getAnotherDependency());
            }

            // TODO: override inject method from super class
            static class SuperClassWithInjectMethod {
                int superCalled = 0;

                @Inject
                void install() {
                    superCalled++;
                }
            }

            static class SubClassWithInjectMethod extends Injection.SuperClassWithInjectMethod {
                int subCalled = 0;

                @Inject
                void installAnother() {
                    subCalled = superCalled + 1;
                }
            }

            @Test
            public void should_override_inject_method_from_super_class() {
                ConstructorInjectProvider<SubClassWithInjectMethod> provider = new ConstructorInjectProvider<>(SubClassWithInjectMethod.class);
                Injection.SubClassWithInjectMethod component = provider.get(context);

                assertEquals(1, component.superCalled);
                assertEquals(2, component.subCalled);
            }

            static class SubclassOverrideSuperclassWithInject extends Injection.SuperClassWithInjectMethod {
                @Inject
                @Override
                void install() {
                    super.install();
                }
            }

            @Test
            public void should_only_call_once_if_subclass_override_superclass_inject_method() {
                ConstructorInjectProvider<SubclassOverrideSuperclassWithInject> provider = new ConstructorInjectProvider<>(SubclassOverrideSuperclassWithInject.class);
                Injection.SubclassOverrideSuperclassWithInject component = provider.get(context);

                assertEquals(1, component.superCalled);
            }

            static class SubclassOverrideSuperclassWithNoInject extends Injection.SuperClassWithInjectMethod {
                @Override
                void install() {
                    super.install();
                }
            }

            @Test
            public void should_not_call_inject_method_if_subclass_with_no_inject() {
                ConstructorInjectProvider<SubclassOverrideSuperclassWithNoInject> provider = new ConstructorInjectProvider<>(SubclassOverrideSuperclassWithNoInject.class);
                Injection.SubclassOverrideSuperclassWithNoInject component = provider.get(context);

                assertEquals(0, component.superCalled);
            }

            // TODO: include dependency from inject method
            @Test
            public void should_include_dependency_from_inject_method() {
                ConstructorInjectProvider<Injection.ComponentWithMethodInject> provider = new ConstructorInjectProvider<>(Injection.ComponentWithMethodInject.class);

                assertArrayEquals(new Class<?>[]{Dependency.class}, provider.getDependencies().toArray(Class<?>[]::new));
            }

        }


        @Nested
        class IllegalInjectMethod {

            // TODO: throw exception if type parameter defined
            static class InjectMethodWithTypeParameter {
                @Inject
                <T> void install() {

                }
            }

            @Test
            public void should_throw_exception_if_method_with_type_parameter() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectProvider<>(IllegalInjectMethod.InjectMethodWithTypeParameter.class));
            }

        }

    }

}
