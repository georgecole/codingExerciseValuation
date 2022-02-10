package com.company;

public class HelperFunctions {

    /* Rather than importing loads of libraries we'll make use of a
    couple of simple functions which for the most part of copied and pasted

    In the real-world I'd probably use the library, with the obvious justification that they're extensively tested
     */

    /* Rather than importing StringUtils or similar
    https://www.baeldung.com/java-blank-empty-strings
     */
    public static boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotEmptyString(String string) {
        return !isEmptyString(string); /* Readability - Completely pointless function to write Not instead of ! */
        /* Now I've made it I'm not sure I'm even going to use it */
    }

}
