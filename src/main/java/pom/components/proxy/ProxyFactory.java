package pom.components.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;


public class ProxyFactory {

	public static <T> T create(Class<T> type, MethodInterceptor interceptor) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(type);
		enhancer.setCallback(interceptor);
		T proxifiedObject = (T) enhancer.create();
		return proxifiedObject;
	}

}
