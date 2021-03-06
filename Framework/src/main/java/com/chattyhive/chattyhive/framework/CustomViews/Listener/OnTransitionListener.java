package com.chattyhive.chattyhive.framework.CustomViews.Listener;

/**
 * Created by Jonathan on 05/09/2014.
 */
public interface OnTransitionListener {

    /**
     * Invoked at the begin of a transition between steps, this method allows to perform verification before advancing to next step.
     * If there's no need to verify any data, simply return true to allow the transition. If false is returned then the transition will abort and screen will remain in actual step.
     * @param actualStep Actual step shown in screen
     * @param nextStep Next step which will be shown after transition.
     * @return false to abort transition else true.
     */
    public boolean OnBeginTransition(int actualStep,int nextStep);

    /**
     * Invoked during a transition between steps, this method allows to perform visual changes based on transition. This method is invoked multiple times.
     * @param visibleSteps Actual steps shown in screen
     * @param visibilityAmount The amount of visibility for the step at the same index. This value is in the range 0..1 and sum of all values is 1.
     */
    public void OnDuringTransition(int[] visibleSteps, float[] visibilityAmount);

    /**
     * Invoked at the end of a transition between steps. This methods notifies that the transition was correctly performed.
     * Notice that if transition was canceled by returning false in the OnBeginTransition method, this method will NOT be invoked.
     * @param actualStep Actual step shown in screen
     * @param previousStep Previous step which was removed from screen.
     */
    public void OnEndTransition(int actualStep,int previousStep);
}
