package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ConstructorInjectProvider
 *
 * @author yufenghui
 * @date 2022/5/29 20:57
 */
class ConstructorInjectProvider<T> implements ComponentProvider<T> {

    private Constructor<T> constructor;
    private List<Field> fields;
    private List<Method> methods;

    public ConstructorInjectProvider(Class<T> component) {
        this.constructor = getInjectConstructor(component);
        this.fields = getInjectFields(component);
        this.methods = getInjectMethods(component);
    }

    private Constructor<T> getInjectConstructor(Class<T> component) {
        List<Constructor<?>> constructorList = Arrays.stream(component.getConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());

        if (constructorList.size() > 1) {
            throw new IllegalComponentException("more than one inject constructor exist.");
        }

        return (Constructor<T>) constructorList.stream().findFirst().orElseGet(() -> {
            try {
                return component.getDeclaredConstructor();
            } catch (Exception e) {
                throw new IllegalComponentException("cannot find default constructor.", e);
            }
        });
    }

    private <T> List<Field> getInjectFields(Class<T> component) {
        return Arrays.stream(component.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
    }

    private <T> List<Method> getInjectMethods(Class<T> component) {
        List<Method> injectMethods = new ArrayList<>();
        Class<?> current = component;

        while (current != Object.class) {
            List<Method> methods = Arrays.stream(current.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Inject.class))
                    .filter(
                            m -> injectMethods.stream().noneMatch(
                                    o -> o.getName().equals(m.getName()) && Arrays.equals(o.getParameterTypes(), m.getParameterTypes())
                            )
                    )
                    .filter(
                            m -> Arrays.stream(component.getDeclaredMethods()).filter(m1 -> !m1.isAnnotationPresent(Inject.class))
                                    .noneMatch(
                                            o -> o.getName().equals(m.getName()) && Arrays.equals(o.getParameterTypes(), m.getParameterTypes())
                                    )
                    )
                    .collect(Collectors.toList());
            injectMethods.addAll(methods);

            current = current.getSuperclass();
        }

        Collections.reverse(injectMethods);
        return injectMethods;
    }

    @Override
    public T get(Context context) {
        try {
            Object[] args = Arrays.stream(constructor.getParameters())
                    .map(p -> context.get(p.getType()).get())
                    .toArray(Object[]::new);
            T instance = constructor.newInstance(args);

            for (Field field : fields) {
                Object dependency = context.get(field.getType()).get();
                field.setAccessible(true);
                field.set(instance, dependency);
            }

            for (Method method : methods) {
                Parameter[] parameters = method.getParameters();
                if(parameters.length == 0) {
                    method.invoke(instance);
                } else {
                    Object[] dependencies = Arrays.stream(parameters).map(p -> context.get(p.getType()).get()).toArray();
                    method.setAccessible(true);
                    method.invoke(instance, dependencies);
                }
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("bind component failed.", e);
        }
    }

    @Override
    public List<Class<?>> getDependencies() {
        List<Class<?>> dependencies = new ArrayList<>();

        List<Class<?>> constructorDependencies = Arrays.stream(constructor.getParameters())
                .map(Parameter::getType)
                .collect(Collectors.toList());
        dependencies.addAll(constructorDependencies);

        List<Class<?>> fieldDependencies = fields.stream()
                .map(f -> f.getType())
                .collect(Collectors.toList());
        dependencies.addAll(fieldDependencies);

        List<Class<?>> methodDependencies = methods.stream().flatMap(m -> Arrays.stream(m.getParameterTypes())).collect(Collectors.toList());;
        dependencies.addAll(methodDependencies);

        return dependencies;
    }

}
