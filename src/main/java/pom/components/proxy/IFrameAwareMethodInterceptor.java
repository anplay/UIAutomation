package pom.components.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import pom.components.AbstractWebComponent;
import pom.components.iframecomponents.DriverContext;
import pom.components.iframecomponents.IFrameAwareInterface;


public class IFrameAwareMethodInterceptor extends AbstractMethodInterceptor {

	private static List<String> forceIgnore = Arrays.asList("setRootElement", "getRootElement", "newInstance", "checkIFrameIsSet",
			"getCallback", "getCallbacks", "setCallback", "setCallbacks");

	@Override
	public void before(Object object) {
		IFrameAwareInterface fragment = (IFrameAwareInterface) object;
		fragment.switchToIFrame();
		DriverContext.setSwitchedToIFrame(true);
	}

	@Override
	public void after(Object object) {
		IFrameAwareInterface fragment = (IFrameAwareInterface) object;
		fragment.switchToDefaultContent();
		DriverContext.setSwitchedToIFrame(false);
	}

	@Override
	protected boolean isMethodAllowed(Object object, Method method) {
		if (!DriverContext.isSwitchedToIFrame()) {
			return isMethodEligible(method);
		}
		return false;
	}

	@Override
	public List<String> extractMethodsToIntercept(Object object) {
		HashSet<Method> methodSet = new HashSet<>();
		methodSet.addAll(Arrays.asList(getMethods(object)));
		filterMethods(methodSet);
		List<String> filteredMethodsList = getMethodNames(methodSet);
		return filteredMethodsList;
	}

	protected boolean isMethodEligible(Method method) {
		for (String filteredMethod : methodsToProcess) {
			if (filteredMethod.equals(method.getName())) {
				return true;
			}
		}
		return false;
	}

	protected HashSet<Method> filterMethods(HashSet<Method> methodList) {
		filterInterfaceMethods(Object.class, methodList);
		filterInterfaceMethods(AbstractWebComponent.class, methodList);
		filterInterfaceMethods(IFrameAwareInterface.class, methodList);
		filterMethodsByModifier(methodList);
		filterIgnoredMethods(methodList);
		return methodList;
	}

	protected HashSet<Method> filterInterfaceMethods(Class clazz, HashSet<Method> methodList) {
		HashSet<Method> methodsToRemove = new HashSet<>();
		for (Method method : methodList) {
			for (Method interfaceMethod : clazz.getMethods()) {
				if (method.getName().equals(interfaceMethod.getName())) {
					methodsToRemove.add(method);
				}
			}
		}
		methodList.removeAll(methodsToRemove);
		return methodList;
	}

	protected HashSet<Method> filterMethodsByModifier(HashSet<Method> methodList) {
		HashSet<Method> methodsToRemove = new HashSet<>();
		for (Method method : methodList) {
			if (getModifiers(method).contains("private") || getModifiers(method).contains("protected")
					|| getModifiers(method).contains("static")) {
				methodsToRemove.add(method);
			}
		}
		methodList.removeAll(methodsToRemove);
		return methodList;
	}

	protected HashSet<Method> filterIgnoredMethods(HashSet<Method> methodList) {
		HashSet<Method> methodsToRemove = new HashSet<>();
		for (Method method : methodList) {
			for (String ignoredMethodName : forceIgnore) {
				if (method.getName().equals(ignoredMethodName)) {
					methodsToRemove.add(method);
				}
			}
		}
		methodList.removeAll(methodsToRemove);
		return methodList;
	}

	protected List<String> getMethodNames(HashSet<Method> methodList) {
		List<String> methodNames = new ArrayList<>();
		for (Method method : methodList) {
			methodNames.add(method.getName());
		}
		return methodNames;
	}

	protected Method[] getMethods(Object fragment) {
		return fragment.getClass().getMethods();
	}

    protected String getModifiers(Method method) {
        return Modifier.toString(method.getModifiers());
    }
}
