package com.jukusoft.mmo.engine.applayer.script.lua;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptExecutionException;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

import java.io.File;

public class DoFileFunc extends AbstractFunction2 {

    protected final LuaScriptEngine engine;

    public DoFileFunc (LuaScriptEngine engine) {
        this.engine = engine;
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) throws ResolvedControlThrowable {
        String scriptFile = ((ByteString) arg1).toRawString();
        String relDir = ((ByteString) arg2).toRawString();

        try {
            this.engine.loadFile(new File(relDir + scriptFile));
        } catch (ScriptLoadException e) {
            Log.e("Scripts", "doFile(): ScriptLoadException while executing script '" + relDir + scriptFile + "': ", e);
            throw new ScriptExecutionException(e);
        }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState) throws ResolvedControlThrowable {
        throw new NonsuspendableFunctionException();
    }

}
