package org.cswteams.ms3.jpa_constraints.validant;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

@Aspect
public class ValidantAspect {

    @Autowired
    private Validator validator ;

    @Before("@annotation(Validant)")
    public void validant(JoinPoint joinPoint) throws ValidationException {

        Object[] args = joinPoint.getArgs() ;
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod() ;

        Parameter[] parameters = method.getParameters();

        for (int i = 0 ; i < parameters.length ; i++) {
            if(parameters[i].isAnnotationPresent(Valid.class)) {
                Set<ConstraintViolation<Object>> violations =  validator.validate(args[i]) ;
                if(!violations.isEmpty()) throw new ValidationException() ;
            }
        }
    }

}
