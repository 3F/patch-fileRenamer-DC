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

package reg.util.dc.flylink.renamer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author reg
 */
public class FilesGetting
{
    public static boolean verifyLine(String text)
    {
        // D:\path, E:/path : D:\path, E:/path ...
        Pattern pat = Pattern.compile("^\\s*\\S:[\\\\/][^>]+>\\s*\\S:[\\\\/][^$]+$", Pattern.UNICODE_CASE);
        return pat.matcher(text).find();
    }
    
    public static String[] get2Path(String text)
    {
        Pattern pat = Pattern.compile("[\\r\\n]", Pattern.UNICODE_CASE);
        text = pat.matcher(text).replaceAll("");
        String[] splited = text.split(">");
        return new String[]{splited[0].trim().toLowerCase(), splited[1].trim().toLowerCase()};
    }
}