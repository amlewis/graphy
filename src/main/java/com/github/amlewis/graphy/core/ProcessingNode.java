package com.github.amlewis.graphy.core;

import java.util.concurrent.ExecutorService;

/**
 * Created by amlewis on 7/13/15.
 */
abstract class ProcessingNode<ResultType> extends Node<ResultType> {
  private ExecutorService executorService = null;

  private final ProcessingNodeRunnable processingNodeRunnable = new ProcessingNodeRunnable();
  private class ProcessingNodeRunnable extends RefreshRunnable {
    @Override
    public void work() {
      process();
    }
  }

  private ExecutorService getExecutorService() {
    if (executorService == null) {
      return Graphy.getInstance().getDefaultProcessingExecutorService();
    } else {
      return executorService;
    }
  }

  final void update() {
    processingNodeRunnable.refresh(getExecutorService());
  }

  /**
   * onDependencyUpdated must call update() when it is valid to update the node.
   * @param dependency
   */
  @Override
  abstract void onDependencyUpdated(Node<?> dependency);

  /**
   * Process is guaranteed to run at least once after each invocation of update(). Process should call setResult() when
   * a result is ready. The process() method for a given node is guaranteed to be executing on at most one thread at any
   * given time.
   */
  abstract void process();


  public void cancel() {
    cancel(false);
  }

  public void cancel(boolean mayInterruptIfRunning) {
    processingNodeRunnable.cancel(mayInterruptIfRunning);
  }

  protected boolean shouldCancel() {
    return processingNodeRunnable.shouldCancel();
  }

  public void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }
}
