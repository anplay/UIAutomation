package pom.components.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


public abstract class AbstractMethodInterceptor implements MethodInterceptor {

	protected List<String> methodsToProcess = new ArrayList<>();

	@Override
	public Object intercept(Object object, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		setMethodsToIntercept(object);
		if (!isMethodAllowed(object, method)) {
			return skipIntercepting(object, objects, methodProxy);
		}
		before(object);
		Object result = null;
		try {
			result = executeOriginalMethod(object, objects, methodProxy);
		} finally {
			doFinally(object);
		}
		after(object);
		return result;
	}

	protected void setMethodsToIntercept(Object object) {
		methodsToProcess = extractMethodsToIntercept(object);
	}

	protected Object skipIntercepting(Object object, Object[] objects, MethodProxy methodProxy) throws Throwable {
		return methodProxy.invokeSuper(object, objects);
	}

	protected Object executeOriginalMethod(Object object, Object[] objects, MethodProxy methodProxy) throws Throwable {
		return methodProxy.invokeSuper(object, objects);
	}

	protected void doFinally(Object object) {
		after(object);
	}

	public abstract void before(Object object);

	public abstract void after(Object object);

	protected abstract boolean isMethodAllowed(Object object, Method method);

	public abstract List<String> extractMethodsToIntercept(Object object);

}
