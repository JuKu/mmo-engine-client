package com.jukusoft.mmo.engine.applayer.script.lua;

import com.jukusoft.mmo.engine.shared.logger.Log;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class LogInfoFunc extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) throws ResolvedControlThrowable {
        String tag = ((ByteString) arg1).toRawString();
        String message = ((ByteString) arg2).toRawString();

        Log.i(tag, message);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState) throws ResolvedControlThrowable {
        throw new NonsuspendableFunctionException();
    }

}
