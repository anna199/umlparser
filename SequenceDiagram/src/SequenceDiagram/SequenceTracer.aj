package SequenceDiagram;


import java.io.IOException;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import SequenceDiagram.SequenceUMLparser;

public aspect SequenceTracer {

	pointcut traceMethods(): 
		(execution(public* *(..)));
	
	before(): traceMethods(){
		Signature s = thisJoinPointStaticPart.getSignature();
		String methodName = s.getName();
		if (thisJoinPoint.getTarget() != null && thisJoinPoint.getThis() != null) {
		String callerClass = thisJoinPoint.getThis().getClass().getSimpleName();
		String calleeClass = thisJoinPoint.getTarget().getClass().getSimpleName();
				
				String returnType = ((MethodSignature) thisJoinPoint.getSignature()).getReturnType().getSimpleName();
				
				Object[] signatureArgs = thisJoinPoint.getArgs();
				String args ="";
			    for (Object signatureArg : signatureArgs) {
			        args += signatureArg.getClass().getSimpleName();
			    }
				String event = callerClass + "->" + calleeClass + ":" + methodName + "(" + args + ") : " + returnType + "\n";
				System.out.print(event);
				SequenceUMLparser.addString(event);
				try {
					SequenceUMLparser.run();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}

//reference on http://blog.crazybob.org/2005/10/generating-sequence-diagrams-using.html