package com.jukusoft.mmo.engine.applayer.script.lua;

import com.jukusoft.mmo.engine.applayer.time.GameTime;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class NowFunc extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
        context.getReturnBuffer().setTo(GameTime.getInstance().getTime());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState) throws ResolvedControlThrowable {
        throw new NonsuspendableFunctionException();
    }

}
