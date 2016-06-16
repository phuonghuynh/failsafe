package net.jodah.failsafe;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.jodah.failsafe.Callables.AsyncCallableWrapper;
import net.jodah.failsafe.function.AsyncCallable;
import net.jodah.failsafe.function.AsyncRunnable;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.ContextualCallable;
import net.jodah.failsafe.function.ContextualRunnable;
import net.jodah.failsafe.internal.util.Assert;
import net.jodah.failsafe.util.concurrent.Scheduler;

/**
 * Performs asynchronous executions according to a {@link RetryPolicy} and {@link CircuitBreaker}.
 * 
 * @author Jonathan Halterman
 */
public class AsyncFailsafe<S, R> extends AsyncListenerConfig<S, R> {
  RetryPolicy retryPolicy;
  CircuitBreaker circuitBreaker;
  Scheduler scheduler;
  Listeners<?> listeners;

  public static class UntypedAsyncFailsafe extends AsyncFailsafe<UntypedAsyncFailsafe, Object> {
    UntypedAsyncFailsafe(RetryPolicy retryPolicy, CircuitBreaker circuitBreaker, Scheduler scheduler) {
      this.retryPolicy = retryPolicy;
      this.circuitBreaker = circuitBreaker;
      this.scheduler = scheduler;
    }

    /**
     * Executes the {@code callable} asynchronously until a successful result is returned or the configured
     * {@link RetryPolicy} is exceeded.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public <T> FailsafeFuture<T> get(Callable<T> callable) {
      return call(Callables.asyncOf(callable), null);
    }

    /**
     * Executes the {@code callable} asynchronously until a successful result is returned or the configured
     * {@link RetryPolicy} is exceeded.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public <T> FailsafeFuture<T> get(ContextualCallable<T> callable) {
      return call(Callables.asyncOf(callable), null);
    }

    /**
     * Executes the {@code callable} asynchronously until a successful result is returned or the configured
     * {@link RetryPolicy} is exceeded. This method is intended for integration with asynchronous code. Retries must be
     * manually scheduled via one of the {@code AsyncExecution.retry} methods.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public <T> FailsafeFuture<T> getAsync(AsyncCallable<T> callable) {
      return call(Callables.asyncOf(callable), null);
    }

    /**
     * Executes the {@code callable} asynchronously until the resulting future is successfully completed or the
     * configured {@link RetryPolicy} is exceeded.
     * <p>
     * Supported on Java 8 and above.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public <T> java.util.concurrent.CompletableFuture<T> future(
        Callable<java.util.concurrent.CompletableFuture<T>> callable) {
      java.util.concurrent.CompletableFuture<T> response = new java.util.concurrent.CompletableFuture<T>();
      call(Callables.ofFuture(callable), FailsafeFuture.of(response, scheduler, null));
      return response;
    }

    /**
     * Executes the {@code callable} asynchronously until the resulting future is successfully completed or the
     * configured {@link RetryPolicy} is exceeded.
     * <p>
     * Supported on Java 8 and above.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public <T> java.util.concurrent.CompletableFuture<T> future(
        ContextualCallable<java.util.concurrent.CompletableFuture<T>> callable) {
      java.util.concurrent.CompletableFuture<T> response = new java.util.concurrent.CompletableFuture<T>();
      call(Callables.ofFuture(callable), FailsafeFuture.of(response, scheduler, null));
      return response;
    }

    /**
     * Executes the {@code callable} asynchronously until the resulting future is successfully completed or the
     * configured {@link RetryPolicy} is exceeded. This method is intended for integration with asynchronous code.
     * Retries must be manually scheduled via one of the {@code AsyncExecution.retry} methods.
     * <p>
     * Supported on Java 8 and above.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public <T> java.util.concurrent.CompletableFuture<T> futureAsync(
        AsyncCallable<java.util.concurrent.CompletableFuture<T>> callable) {
      java.util.concurrent.CompletableFuture<T> response = new java.util.concurrent.CompletableFuture<T>();
      call(Callables.ofFuture(callable), FailsafeFuture.of(response, scheduler, null));
      return response;
    }

    /**
     * Configures the {@code listeners} to be called as execution events occur.
     * 
     * @throws NullPointerException if {@code listeners} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public <T extends Listeners<T>> TypedAsyncFailsafe<T> with(T listeners) {
      return new TypedAsyncFailsafe<T>(retryPolicy, circuitBreaker, scheduler, Assert.notNull(listeners, "listeners"));
    }
  }

  public static class TypedAsyncFailsafe<T> extends AsyncFailsafe<TypedAsyncFailsafe<T>, T> {
    TypedAsyncFailsafe(RetryPolicy retryPolicy, CircuitBreaker circuitBreaker, Scheduler scheduler,
        Listeners<T> listeners) {
      this.retryPolicy = retryPolicy;
      this.circuitBreaker = circuitBreaker;
      this.scheduler = scheduler;
      this.listeners = listeners;
    }

    /**
     * Executes the {@code callable} asynchronously until a successful result is returned or the configured
     * {@link RetryPolicy} is exceeded.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public FailsafeFuture<T> get(Callable<T> callable) {
      return call(Callables.asyncOf(callable), null);
    }

    /**
     * Executes the {@code callable} asynchronously until a successful result is returned or the configured
     * {@link RetryPolicy} is exceeded.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public FailsafeFuture<T> get(ContextualCallable<T> callable) {
      return call(Callables.asyncOf(callable), null);
    }

    /**
     * Executes the {@code callable} asynchronously until a successful result is returned or the configured
     * {@link RetryPolicy} is exceeded. This method is intended for integration with asynchronous code. Retries must be
     * manually scheduled via one of the {@code AsyncExecution.retry} methods.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    public FailsafeFuture<T> getAsync(AsyncCallable<T> callable) {
      return call(Callables.asyncOf(callable), null);
    }

    /**
     * Executes the {@code callable} asynchronously until the resulting future is successfully completed or the
     * configured {@link RetryPolicy} is exceeded.
     * <p>
     * Supported on Java 8 and above.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    @SuppressWarnings("unchecked")
    public java.util.concurrent.CompletableFuture<T> future(
        Callable<java.util.concurrent.CompletableFuture<T>> callable) {
      java.util.concurrent.CompletableFuture<T> response = new java.util.concurrent.CompletableFuture<T>();
      call(Callables.ofFuture(callable), FailsafeFuture.of(response, scheduler, (Listeners<T>) listeners));
      return response;
    }

    /**
     * Executes the {@code callable} asynchronously until the resulting future is successfully completed or the
     * configured {@link RetryPolicy} is exceeded.
     * <p>
     * Supported on Java 8 and above.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    @SuppressWarnings("unchecked")
    public java.util.concurrent.CompletableFuture<T> future(
        ContextualCallable<java.util.concurrent.CompletableFuture<T>> callable) {
      java.util.concurrent.CompletableFuture<T> response = new java.util.concurrent.CompletableFuture<T>();
      call(Callables.ofFuture(callable), FailsafeFuture.of(response, scheduler, (Listeners<T>) listeners));
      return response;
    }

    /**
     * Executes the {@code callable} asynchronously until the resulting future is successfully completed or the
     * configured {@link RetryPolicy} is exceeded. This method is intended for integration with asynchronous code.
     * Retries must be manually scheduled via one of the {@code AsyncExecution.retry} methods.
     * <p>
     * Supported on Java 8 and above.
     * 
     * @throws NullPointerException if the {@code callable} is null
     * @throws CircuitBreakerOpenException if a configured circuit breaker is open
     */
    @SuppressWarnings("unchecked")
    public java.util.concurrent.CompletableFuture<T> futureAsync(
        AsyncCallable<java.util.concurrent.CompletableFuture<T>> callable) {
      java.util.concurrent.CompletableFuture<T> response = new java.util.concurrent.CompletableFuture<T>();
      call(Callables.ofFuture(callable), FailsafeFuture.of(response, scheduler, (Listeners<T>) listeners));
      return response;
    }
  }

  /**
   * Executes the {@code runnable} asynchronously until successful or until the configured {@link RetryPolicy} is
   * exceeded.
   * 
   * @throws NullPointerException if the {@code runnable} is null
   * @throws CircuitBreakerOpenException if a configured circuit breaker is open
   */
  public FailsafeFuture<Void> run(CheckedRunnable runnable) {
    return call(Callables.<Void>asyncOf(runnable), null);
  }

  /**
   * Executes the {@code runnable} asynchronously until successful or until the configured {@link RetryPolicy} is
   * exceeded.
   * 
   * @throws NullPointerException if the {@code runnable} is null
   * @throws CircuitBreakerOpenException if a configured circuit breaker is open
   */
  public FailsafeFuture<Void> run(ContextualRunnable runnable) {
    return call(Callables.<Void>asyncOf(runnable), null);
  }

  /**
   * Executes the {@code runnable} asynchronously until successful or until the configured {@link RetryPolicy} is
   * exceeded. This method is intended for integration with asynchronous code. Retries must be manually scheduled via
   * one of the {@code AsyncExecution.retry} methods.
   * 
   * @throws NullPointerException if the {@code runnable} is null
   * @throws CircuitBreakerOpenException if a configured circuit breaker is open
   */
  public FailsafeFuture<Void> runAsync(AsyncRunnable runnable) {
    return call(Callables.<Void>asyncOf(runnable), null);
  }

  /**
   * Configures the {@code circuitBreaker} to be used to control the rate of event execution.
   * 
   * @throws NullPointerException if {@code circuitBreaker} is null
   * @throws IllegalStateException if a circuit breaker is already configured
   */
  @SuppressWarnings("unchecked")
  public S with(CircuitBreaker circuitBreaker) {
    Assert.state(this.circuitBreaker == null, "A circuit breaker has already been configurd");
    this.circuitBreaker = Assert.notNull(circuitBreaker, "circuitBreaker");
    return (S) this;
  }

  /**
   * Configures the {@code retryPolicy} to be used for retrying failed executions.
   * 
   * @throws NullPointerException if {@code retryPolicy} is null
   * @throws IllegalStateException if a retry policy is already configured
   */
  @SuppressWarnings("unchecked")
  public S with(RetryPolicy retryPolicy) {
    Assert.state(this.retryPolicy == RetryPolicy.NEVER, "A retry policy has already been configurd");
    this.retryPolicy = Assert.notNull(retryPolicy, "retryPolicy");
    return (S) this;
  }

  /**
   * Calls the asynchronous {@code callable} via the {@code executor}, performing retries according to the
   * {@code retryPolicy}.
   * 
   * @throws NullPointerException if any argument is null
   * @throws CircuitBreakerOpenException if a configured circuit breaker is open
   */
  @SuppressWarnings("unchecked")
  <T> FailsafeFuture<T> call(AsyncCallableWrapper<T> callable, FailsafeFuture<T> future) {
    if (circuitBreaker != null) {
      circuitBreaker.initialize();
      if (!circuitBreaker.allowsExecution())
        throw new CircuitBreakerOpenException();
    }

    if (future == null)
      future = new FailsafeFuture<T>(scheduler, (Listeners<T>) listeners);
    AsyncExecution execution = new AsyncExecution(callable, retryPolicy, circuitBreaker, scheduler, future, listeners);
    callable.inject(execution);
    future.inject(execution);

    try {
      future.setFuture((Future<T>) scheduler.schedule(callable, 0, TimeUnit.MILLISECONDS));
    } catch (Throwable t) {
      future.complete(null, t, false);
    }

    return future;
  }
}