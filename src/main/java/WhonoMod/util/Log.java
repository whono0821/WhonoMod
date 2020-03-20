package WhonoMod.util;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    private static Logger logger = LogManager.getLogger("whonomod");

    public static void log( String s ) {

        logger.log( org.apache.logging.log4j.Level.OFF, s );
    }

    public static void log(Level lv, String s ) {

        logger.log( lv, s );
    }

    public static void log(Level lv, String s, Object... params ) {

        logger.log( lv, s, params );
    }

    public static void info( String s ) {

        logger.log(Level.INFO, s);
    }

    public static void info( String s, Object... params ) {

        logger.log(Level.INFO, s, params );
    }

    public static void warn( String s ) {

        logger.log(Level.WARN, s);
    }

    public static void warn( String s, Object... params ) {

        logger.log(Level.WARN, s, params);
    }

    public static void trace( String s ) {

        logger.log(Level.TRACE, s);
    }

    public static void trace( String s, Object... params ) {

        logger.log(Level.TRACE, s, params);
    }

    public static void fatal( String s ) {

        logger.log(Level.FATAL, s);
    }

    public static void fatal( String s, Object... params ) {

        logger.log(Level.FATAL, s, params);
    }

    public static void debug( String s ) {

        logger.log(Level.DEBUG, s);
    }

    public static void debug( String s, Object... params ) {

        logger.log(Level.DEBUG, s, params);
    }

    public static void error( String s ) {

        logger.log(Level.ERROR, s);
    }

    public static void error( String s, Object... params ) {

        logger.log(Level.ERROR, s, params);
    }
}
