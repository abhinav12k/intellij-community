// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.containers;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Deprecated. Please use {@link java.util.function.Function} instead
 */
@FunctionalInterface
public interface Convertor<Src, Dst> extends Function<Src, Dst> {
  IntoSelf SELF = new IntoSelf();

  class IntoSelf<Src> implements Convertor<Src, Src> {
    @Override
    public Src convert(Src o) {
      return o;
    }
  }

  Dst convert(Src o);

  @Override
  default Dst apply(Src src) {
    return convert(src);
  }

  static @NotNull <T> Convertor<T,T> self() {
    //noinspection unchecked
    return SELF;
  }

  /**
   * @deprecated this isn't needed since this interface extends from {@link Function}
   */
  @Deprecated
  default @NotNull Function<Src,Dst> asFunction() {
    return this;
  }

}
