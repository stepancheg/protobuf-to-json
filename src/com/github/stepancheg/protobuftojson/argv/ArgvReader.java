package com.github.stepancheg.protobuftojson.argv;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author Stepan Koltsov
 */
public class ArgvReader {

    private final List<String> argv;
    private int pos = 0;

    public ArgvReader(List<String> argv) {
        this.argv = argv;
    }

    public int remaining() {
        return argv.size() - pos;
    }

    public boolean eof() {
        return remaining() == 0;
    }

    public Optional<String> lookahead() {
        return !eof() ? Optional.of(argv.get(pos)) : Optional.empty();
    }

    public String advance() {
        String r = argv.get(pos);
        ++pos;
        return r;
    }

    public boolean nextIsOptionWithParam(String option) {
        Optional<String> lookahead = lookahead();
        return remaining() >= 2 && lookahead.get().equals(option);
    }


}
