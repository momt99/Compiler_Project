package sut.momtsaber.clikecompiler.dfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sut.momtsaber.clikecompiler.cfg.CFGNonTerminal;
import sut.momtsaber.clikecompiler.cfg.CFGTerminal;

public class DFAState<T>
{
    public static final DFAState SELF = new DFAState();

    private List<DFAEdge<T>> exitingEdges;
    private CFGNonTerminal referencing;
    private CFGTerminal consumed;
    public DFAState()
    { }

    public DFAState(DFAEdge<T>... edges)
    {
        this.exitingEdges = Arrays.asList(edges);
    }

    public void setExitingEdges(DFAEdge<T>... exitingEdges)
    {
        this.exitingEdges = Arrays.asList(exitingEdges);
    }
    public void addExitEdge(DFAEdge<T> exitEdge)
    {
        if (exitingEdges == null)
            exitingEdges = new ArrayList<>();
        this.exitingEdges.add(exitEdge);
    }

    public NextStateResult<T> getNextState(T input)
    {
        return exitingEdges.stream()
                .filter(edge -> edge.getEntrance().canEnter(input))
                .findFirst()
                .map(edge -> new NextStateResult<>(
                        edge.getNextState() == SELF ? DFAState.this : edge.getNextState(),
                        edge.isConsuming()))
                .orElse(null);
    }


    public CFGNonTerminal getReferencing()
    {
        return referencing;
    }

    public void setReferencing(CFGNonTerminal referencing)
    {
        this.referencing = referencing;
    }

    public CFGTerminal getConsumed()
    {
        return consumed;
    }

    public void setConsumed(CFGTerminal consumed)
    {
        this.consumed = consumed;
    }

    public static class NextStateResult<T>
    {
        private final DFAState<T> nextState;
        private final boolean consuming;

        public NextStateResult(DFAState<T> nextState, boolean consuming)
        {
            this.nextState = nextState;
            this.consuming = consuming;
        }

        public DFAState<T> getNextState()
        {
            return nextState;
        }

        public boolean isConsuming()
        {
            return consuming;
        }

    }
}
