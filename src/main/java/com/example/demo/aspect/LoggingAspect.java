package com.example.demo.aspect;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    private HttpServletRequest getRequest() throws ServletException {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        throw new ServletException("Cannot get the request");
    }

    @Pointcut("within(com.example.demo.services..*)")
    public void inServiceLayer(){}

    @Pointcut("""
    execution(* org.springframework.data.repository.CrudRepository+.save(..)) ||
    execution(* org.springframework.data.repository.CrudRepository+.saveAll(..))
    """)
    public void repositorySaveOperations() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethods() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)||" +
            "    @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void getMappingMethods() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) ||" +
            "   @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMappingMethods() {}


    @Before("inServiceLayer() && publicMethods()")
    public void logBefore(JoinPoint joinPoint){
        String type = joinPoint.getSignature().getDeclaringType().getSimpleName();
        log.debug("Service called: " + type + "." + joinPoint.getSignature().getName());
    }

    @Around("postMappingMethods() || getMappingMethods()")
    public void logOperationDuration(ProceedingJoinPoint joinPoint) throws Throwable {
            long start = System.currentTimeMillis();
            // get request url
            String url = "Could not get URL";
            try{
                url = getRequest().getRequestURL().toString();
            }catch(Exception e){}
            // process request
            try {
            joinPoint.proceed();
            }catch (Throwable e){
                log.error("Exception occurred while processing " + url +": ", e);
                throw e;
            }
            long end = System.currentTimeMillis();
            log.info(url + " took " +  (end - start) + " ms");
    }
}
