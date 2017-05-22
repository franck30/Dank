package me.saket.dank.utils;

import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Utility methods related to Rx.
 */
public class RxUtils {

  /**
   * A transformer that makes an Observable execute its computation (or emit items) inside an IO thread and the
   * operators that follow this method call (including the Subscriber) execute on the main thread. This should ideally
   * be called right before (or as close as possible) to the subscribe() call to ensure any other operator doesn't
   * accidentally get executed on the main thread.
   */
  public static <T> ObservableTransformer<T, T> applySchedulers() {
    return observable -> observable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public static <T> SingleTransformer<T, T> applySchedulersSingle() {
    return observable -> observable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public static CompletableTransformer applySchedulersCompletable() {
    return observable -> observable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public static <T> Consumer<T> doNothing() {
    return t -> {};
  }

  public static Action doNothingCompletable() {
    return () -> {};
  }

  public static Consumer<Throwable> logError(String errorMessage, Object... args) {
    return error -> Timber.e(error, errorMessage, args);
  }

  /**
   * Calls true on <var>consumer</var> when the stream is subscribed to and false
   * when the first event is emitted or the stream is terminated.
   */
  public static <T> ObservableTransformer<T, T> onStartAndFirstEvent(Consumer<Boolean> consumer) {
    return observable -> observable
        .doOnSubscribe(o -> consumer.accept(true))
        .doOnNext(o -> consumer.accept(false))
        .doOnTerminate(() -> consumer.accept(false));
  }

  /**
   * Calls true on <var>consumer</var> when the stream is subscribed to and false when it terminates (with success or failure).
   */
  public static <T> SingleTransformer<T, T> doOnSingleStartAndTerminate(Consumer<Boolean> consumer) {
    return observable -> observable
        .doOnSubscribe(o -> consumer.accept(true))
        .doAfterTerminate(() -> consumer.accept(false));
  }

  /**
   * Calls true on <var>consumer</var> when the stream is subscribed to and false when it succeeds.
   */
  public static <T> SingleTransformer<T, T> doOnSingleStartAndSuccess(Consumer<Boolean> consumer) {
    return observable -> observable
        .doOnSubscribe(o -> consumer.accept(true))
        .doOnSuccess(o -> consumer.accept(false));
  }

  /**
   * Calls true on <var>consumer</var> when the stream is subscribed to and false when it finishes.
   */
  public static CompletableTransformer doOnCompletableStartAndEnd(Consumer<Boolean> consumer) {
    return observable -> observable
        .doOnSubscribe(o -> consumer.accept(true))
        .doAfterTerminate(() -> consumer.accept(false));
  }

  /**
   * Run <var>oneShotConsumer</var> after the stream emits an item, exactly once.
   */
  public static <T> ObservableTransformer<T, T> doOnceAfterNext(Consumer<T> oneShotConsumer) {
    return observable -> observable.doAfterNext(new Consumer<T>() {
      boolean isFirstDoOnNext = true;

      @Override
      public void accept(@NonNull T t) throws Exception {
        if (isFirstDoOnNext) {
          oneShotConsumer.accept(t);
        }
        isFirstDoOnNext = false;
      }
    });
  }
}
