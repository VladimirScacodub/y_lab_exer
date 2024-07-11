package org.coworking.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Аспект, который логирует выполнение методов и замеряет время их выполнения
 */
@Aspect
public class LoggableAspect {

    /**
     * Объект через которого происходит логирование
     */
    private static final Logger LOGGER = Logger.getLogger(LoggableAspect.class.getName());

    /**
     * PointCut который определяет в каком месте будет выполненно логирование
     */
    @Pointcut("within(@org.coworking.annotations.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable(){}

    /**
     * Набор инструкций который выолняет логирование с замером времени
     * @param proceedingJoinPoint объект, контролирующий точку наблюдения
     * @return Результат работы proceed метода
     * @throws Throwable в случае если возникнет проблема
     */
    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        String callingMethodMessage = "Calling " + proceedingJoinPoint.getSignature();
        LOGGER.log(Level.INFO, callingMethodMessage);
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        String executionInfoMessage = "Excution of " + proceedingJoinPoint.getSignature() +
                " finished. Execution time is " + end + "ms";
        LOGGER.log(Level.INFO, executionInfoMessage);
        return result;
    }
}
