package main.model;

import java.util.Collection;
import java.util.List;

import static main.model.GameStatusEnum.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus() {
        if (spaces.stream().flatMap(Collection::stream)
                .noneMatch(s -> !s.isFixo() && nonNull(s.getAtual()))) {
            return NON_STARTED;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> isNull(s.getAtual())) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErros() {
        if (getStatus() == NON_STARTED) {
            return false;
        }
        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getAtual()) && !s.getAtual().equals(s.getEsperado()));
    }

    public boolean changeValue(final int col, final int lin, final int value) {
        var space = spaces.get(col).get(lin);
        if (space.isFixo()) {
            return false;
        }

        space.setAtual(value);
        return true;
    }

    public boolean clearValue(final int col, final int lin) {
        var space = spaces.get(col).get(lin);
        if (space.isFixo()) {
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset() {
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished() {
        return hasErros() && getStatus().equals(COMPLETE);
    }

}
