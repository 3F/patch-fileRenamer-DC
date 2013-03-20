/*
    * Copyright (C) 2013 Developed by reg <entry.reg@gmail.com>
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *      http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
 */

package reg.util.dc.flylink.renamer.exceptions;

/**
 * Cutom exception (with a exit code) for main operation.
 * @author reg
 */
public class IllegalOperationException extends Exception
{
    /**
     * Cannot read configuration
     */
    public final static int EXIT_CODE_CONFIG_READ_ERROR     = 100;
    /**
     * arguments is null
     */
    public final static int EXIT_CODE_CONTROLLER_ARGS_NULL  = 101;    
    /**
     * file not found or no read access
     */
    public final static int EXIT_CODE_DB_NOT_ACCESS         = 102;
    
    /**
     * Stores the exit code for a critical termination program
     */
    private int exitCode;
    
    public IllegalOperationException(String msg, int exitCode)
    {
        super(msg);
        this.exitCode = exitCode;
    }
    
    public IllegalOperationException(String msg, int exitCode, Throwable cause)
    {
        super(msg, cause);
        this.exitCode = exitCode;
    }
    
    public int getExitCode()
    {
        return exitCode;
    }
}
