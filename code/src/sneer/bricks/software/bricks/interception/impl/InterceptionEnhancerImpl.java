package sneer.bricks.software.bricks.interception.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.environments.Environments;

class InterceptionEnhancerImpl implements InterceptionEnhancer {

	public static final String BRICK_METADATA_CLASS = "natures.runtime.BrickMetadata";
	private final ClassPool classPool;
	private int _continuations;

	public InterceptionEnhancerImpl() {
		classPool  = new ClassPool(false);
		classPool.appendClassPath(new LoaderClassPath(InterceptionEnhancer.class.getClassLoader()));
	}

	@Override
	public List<ClassDefinition> realize(Class<? extends Interceptor> interceptorClass, final ClassDefinition classDef) {
		final ArrayList<ClassDefinition> result = new ArrayList<ClassDefinition>();
		try {
			final CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classDef.bytes));
			CtClass metadata = null;
			try {
				metadata = defineBrickMetadata(ctClass);
				if (isBrickImplementation(ctClass)) {
					result.add(toClassDefinition(metadata));
					introduceMetadataInitializer(interceptorClass, ctClass);
				}
				enhanceMethods(ctClass, result);
				result.add(toClassDefinition(ctClass));
			} finally {
				ctClass.detach();
				if(metadata != null)
					metadata.detach();
			}
			return result;
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (CannotCompileException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (NotFoundException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	private void introduceMetadataInitializer(Class<? extends Interceptor> interceptorClass, CtClass brickClass) throws NotFoundException {
		try {
			CtConstructor initializer = brickClass.makeClassInitializer();
			initializer.insertAfter(BRICK_METADATA_CLASS + ".BRICK = " + brickInterface(brickClass).getName() + ".class;");
			initializer.insertAfter(BRICK_METADATA_CLASS + ".NATURE = " + Environments.class.getName() + ".my(" + interceptorClass.getName() + ".class);");
		} catch (CannotCompileException e) {
			throw new IllegalStateException(e);
		}
	}

	private CtClass brickInterface(CtClass implClass) throws NotFoundException {
		for (CtClass intrface : implClass.getInterfaces())
			if (isBrickInterface(intrface))
				return intrface;
		return null;
	}

	private CtClass defineBrickMetadata(@SuppressWarnings("unused") CtClass brickClass) {
		CtClass metadata = classPool.makeClass(BRICK_METADATA_CLASS);
		metadata.setModifiers(javassist.Modifier.PUBLIC);
		try {
			metadata.addField(CtField.make("public static " + Class.class.getName() + " BRICK;", metadata));
			metadata.addField(CtField.make("public static " + Interceptor.class.getName() + " NATURE;", metadata));
			
			return metadata;
		} catch (CannotCompileException e) {
			throw new IllegalStateException(e);
		}
	}

	private boolean isBrickImplementation(CtClass ctClass) throws NotFoundException {
		return brickInterface(ctClass) != null;
	}

	private boolean isBrickInterface(CtClass intrface) {
		for (Object annotation : intrface.getAvailableAnnotations())
			if (annotation instanceof Brick)
				return true;
		return false;
	}

	private void enhanceMethods(final CtClass ctClass,
			final ArrayList<ClassDefinition> result) {
		for (CtMethod m : ctClass.getDeclaredMethods()) {
			if (!isAccessibleInstanceMethod(m))
				continue;
			new MethodEnhancer(continuationNameFor(m), classPool, ctClass, m, result).run();
		}
	}

	private boolean isAccessibleInstanceMethod(CtMethod m) {
		int modifiers = m.getModifiers();
		if (Modifier.isStatic(modifiers)) return false;
		if (Modifier.isPublic(modifiers)) return true;
		if (Modifier.isProtected(modifiers)) return true;
		return false;
	}

	private String continuationNameFor(CtMethod m) {
		return m.getName() + "$" + (++_continuations);
	}

	public static ClassDefinition toClassDefinition(final CtClass ctClass)
			throws CannotCompileException {
		try {
			return new ClassDefinition(ctClass.getName(), ctClass.toBytecode());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}