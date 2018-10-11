package com.jukusoft.mmo.engine.applayer.script.lua;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class LogInfoFunc extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) throws ResolvedControlThrowable {
        String tag = (String) arg1;
        String message = (String) arg2;

        Log.i(tag, message);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState) throws ResolvedControlThrowable {
        throw new NonsuspendableFunctionException();
    }

}
