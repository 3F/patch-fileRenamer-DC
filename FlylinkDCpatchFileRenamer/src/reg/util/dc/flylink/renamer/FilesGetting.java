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

public class FilesGetting
{
    /**
     * Проверка формата строки
     * @param text
     * @return 
     */
    public static boolean verifyLine(String text)
    {
        // D:\path, E:/path : D:\path, E:/path ...
        Pattern pat = Pattern.compile("^\\s*\\S:[\\\\/][^>]+>\\s*\\S:[\\\\/][^$]+$", Pattern.UNICODE_CASE);
        return pat.matcher(text).find();
    }
    
    /**
     * Получить пути из строки
     * @param text
     * @return 
     */
    public static String[] get2Path(String text)
    {
        Pattern pat = Pattern.compile("[\\r\\n]", Pattern.UNICODE_CASE);
        text = pat.matcher(text).replaceAll("");
        String[] splited = text.split(">");
        return new String[]{splited[0].trim().toLowerCase(), splited[1].trim().toLowerCase()};
    }
    
    /**
     * Получение частей из пути (Путь к директории/Имя файла)
     * @param path Абсолютный путь
     * @return [0]-> Путь [1] -> файл (пустое значение если передан только путь)
     * @throws Exception 
     */
    public static String[] splitPath(String path) throws Exception
    {
        Pattern pat = Pattern.compile("^(.*[\\\\/])(.*)$", Pattern.UNICODE_CASE);
        Matcher m   = pat.matcher(path.trim());
        
        if(m.matches()){
            return new String[]{m.group(1).replace("/", "\\"), m.group(2).trim()};
        }
        throw new Exception("parse of path error: " + path);
    }
}