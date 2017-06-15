/**
 * Copyright (C) 2016 Hurence (bailet.thomas@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hurence.logisland.expressionlanguage;

import javax.script.*;

/**
 * Created by mathieu on 31/05/17.
 */
public class Jsr223InterpreterEngine implements InterpreterEngine {

    private static final String defaultEngineName = "mvel";
    private ScriptEngine engine;

    /**
     * Default Constructor.
     * By default the interpretor is MVEL.
     */
    public Jsr223InterpreterEngine() {
        this(defaultEngineName);
    }

    /**
     * Constructor with interpretor name.
     * Allows to instantiate an alternative interpretor.
     *
     * @param engineName the name of the interpretor to load
     */
    public Jsr223InterpreterEngine(String engineName) {
        ScriptEngineManager factory = new ScriptEngineManager();
        engine = factory.getEngineByName(engineName);
    }

    /**
     * Executes code without context
     *
     * @param script the script to execute
     * @return the result as a string, or null if void.
     * @throws InterpreterEngineException if the code is not conform.
     */
    public String process(String script) throws InterpreterEngineException {
        try {
            Object res = engine.eval(script);
            return ((res == null) ? null : res.toString());
        } catch (ScriptException se) {
            throw new InterpreterEngineException(se);
        }
    }

    public Jsr223Context buildContext() {
        return new Jsr223Context();
    }

    /**
     * Executes code without with context.
     *
     * @param script the script to execute
     * @param context the context
     * @return the result as a string, or null if void.
     * @throws InterpreterEngineException if the code is not conform.
     */
    public String process(String script, ScriptContext context) throws InterpreterEngineException {
        try {
            Object res = engine.eval(script, context);
            return ((res == null) ? null : res.toString());
        } catch (ScriptException se) {
            throw new InterpreterEngineException(se);
        }
    }

}
