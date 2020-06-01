/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects.references.impl;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import diarsid.support.objects.references.Reference;

/**
 *
 * @author Diarsid
 */
public interface Possible<T> extends Reference<T> {

    void ifNotPresent(Runnable runnable);

    T ifNotPresentResetTo(T newT);

    void ifPresent(Consumer<T> consumer);

    T ifPresentResetTo(T newT);

    boolean isNotPresent();

    boolean isPresent();

    <R> Possible<R> map(Function<T, R> mapper);

    boolean notEquals(Possible<T> possibleT);

    T nullify();

    Optional<T> optional();

    T or(T otherT);

    T orOther(Possible<T> otherPossibleT);

    Possible<T> orDefault(T defaultT);

    T orThrow();
    
    T extractOrThrow();

    T orThrow(Supplier<? extends RuntimeException> exceptionCreator);

    T resetTo(Optional<T> optionalT);

    T resetTo(Possible<T> possibleT);
    
}