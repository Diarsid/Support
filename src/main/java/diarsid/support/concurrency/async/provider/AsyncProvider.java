/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.concurrency.async.provider;

import java.util.function.Supplier;

/**
 *
 * @author Diarsid
 */
public interface AsyncProvider<T> {
    
    public static <T> AsyncProviderBuilder<T> asyncProvide(Supplier<T> tSupplier) {
        return new AsyncProviderBuilder<>(tSupplier);
    }

    Awaited<T> await();

    Awaited<T> stop();
    
    AsyncProviderState state();
    
}
