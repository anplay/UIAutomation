package pom.components.proxy;

import java.lang.reflect.Method;
import java.util.HashSet;

import pom.components.AbstractFragmentInterface;


public class ProxifiedFragmentMethodInterceptor extends IFrameAwareMethodInterceptor {

	@Override
	protected HashSet<Method> filterMethods(HashSet<Method> methodList) {
		super.filterMethods(methodList);
		filterInterfaceMethods(AbstractFragmentInterface.class, methodList);
		return methodList;
	}

}
