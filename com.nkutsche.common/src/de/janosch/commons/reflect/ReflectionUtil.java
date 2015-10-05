package de.janosch.commons.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Janos
 * @version 20.09.2011 | 00:30:38
 * 
 */
public class ReflectionUtil {

	public static <T> T getAttribute(final String attribName, final Object object) {
		if (object == null) {
			return null;
		}
		try {
			final Field attrib = object.getClass().getDeclaredField(attribName);
			attrib.setAccessible(true);
			@SuppressWarnings("unchecked")
			final T value = (T) attrib.get(object);
			return value;
		} catch (final NoSuchFieldException e) {
		} catch (final IllegalAccessException e) {
		}
		return null;
	}

	public static String toObjectString(final Object object) {
		final List<String> fields2 = getFieldList(object);
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (final String field : fields2) {
			if (!first) {
				sb.append(", ");
			}
			first = false;
			sb.append(field);
		}
		sb.append("}");
		return sb.toString();
	}

	private static List<String> getFieldList(final Object object) {
		final List<String> list = new ArrayList<String>();
		final List<Field> fields = getGetterFields(object.getClass());
		for (final Field field : fields) {
			field.setAccessible(true);
			final StringBuilder sb = new StringBuilder();
			sb.append(field.getName());
			sb.append(": ");
			try {
				sb.append(field.get(object));
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
			list.add(sb.toString());
		}
		return list;
	}

	private static List<Field> getGetterFields(final Class<?> clazz) {
		final List<Field> list = new ArrayList<Field>();
		final Field[] fields = clazz.getDeclaredFields();
		for (final Field field : fields) {
			if (fieldHasGetter(field, clazz)) {
				list.add(field);
			}
		}
		return list;
	}

	/**
	 * Determines if the specified class 'clazz' has a getter method for the specified field
	 * 'field'.
	 * 
	 * An associated getter method for a field with name 'foo' is a method with the folliwing
	 * properties:
	 * <ol>
	 * <li>The name is 'getFoo'.</li>
	 * <li>The method has no formal parameters.</li>
	 * <li>The return type is the same as the fields return type.</li>
	 * <li>The publicicity modifier is 'public'.</li>
	 * </ol>
	 * In case of a field of boolean type, the getter methods name may also be 'isFoo'.
	 * 
	 * @param field
	 *            A field of the specified class.
	 * @param clazz
	 *            A class, in which a getter method is searched for the field.
	 * @return If the specified class has a method with all the four getter properties for the
	 *         specified field.
	 */
	private static boolean fieldHasGetter(final Field field, final Class<?> clazz) {
		if (isBooleanType(field.getType())) {
			// If field type is boolean, check if there is a method called "isName"
			final Method getterMethod;
			try {
				getterMethod = clazz.getDeclaredMethod("is" + toFirstUpper(field.getName()));
				if (methodIsGetterForField(getterMethod, field)) {
					return true;
				}
			} catch (final NoSuchMethodException e) {
			}
		}
		// else and anyway, check if there is a method named "getName"
		try {
			final Method getterMethod = clazz.getDeclaredMethod("get" + toFirstUpper(field.getName()));
			return methodIsGetterForField(getterMethod, field);
		} catch (final NoSuchMethodException e) {
		}
		return false;
	}

	private static boolean methodIsGetterForField(final Method aMethod, final Field aField) {
		if (aMethod == null || aField == null) {
			return false;
		}
		return aField.getType().equals(aMethod.getReturnType()) && aMethod.getParameterTypes().length == 0
				&& ((aMethod.getModifiers() & Modifier.PUBLIC) > 0);
	}

	private static boolean isBooleanType(final Class<?> type) {
		return Boolean.TYPE.equals(type) || Boolean.class.equals(type);
	}

	/**
	 * Changes the input string with it's first character as a lower-case-character. If the string
	 * has no first character, or the first character is already in lower-case, then the input
	 * string itself is returned.
	 * 
	 * @param aString
	 *            A string.
	 * @return The same string, but with a lower-case first character.
	 */
	public static String toFirstLower(final String aString) {
		if (aString == null) {
			return null;
		}
		if ("".equals(aString)) {
			return aString;
		}
		final char charAt0 = aString.charAt(0);
		if (Character.isLowerCase(charAt0)) {
			return aString;
		}
		final StringBuilder sb = new StringBuilder(aString);
		sb.setCharAt(0, Character.toLowerCase(charAt0));
		return sb.toString();
	}


	/**
	 * Changes the input string with it's first character as a upper-case-character. If the string
	 * has no first character, or the first character is already in upper-case, then the input
	 * string itself is returned.
	 * 
	 * @param aString
	 *            A string.
	 * @return The same string, but with a upper-case first character.
	 */
	public static String toFirstUpper(final String aString) {
		if (aString == null) {
			return null;
		}
		if ("".equals(aString)) {
			return "";
		}
		final char charAt0 = aString.charAt(0);
		if (Character.isUpperCase(charAt0)) {
			return aString;
		}
		final StringBuilder sb = new StringBuilder(aString);
		sb.setCharAt(0, Character.toUpperCase(charAt0));
		return sb.toString();
	}

	public static <T> T createInstance(final Class<T> type, final Object... arguments) {
		final Class<?>[] signature = getSignature(arguments);
		final Constructor<T> constructor = findConstructor(type, signature);
		if (constructor == null) {
			return null;
		}
		try {
			return constructor.newInstance(arguments);
		} catch (final Exception e) {
		}
		return null;
	}

	private static Class<?>[] getSignature(final Object... arguments) {
		if (arguments == null) {
			return null;
		}
		final Class<?>[] signature = new Class<?>[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			signature[i] = arguments[i].getClass();
		}
		return signature;
	}

	/**
	 * Searches the type 'type' for the best matching constructor, for which the signature
	 * 'signature' is aplicable. <br>
	 * <br>
	 * For the resulting "best matching" constructor C, the following is true: <br>
	 * 1. The signature is asignable to C's signature. <br>
	 * 2. There exists no other constructor C2, whos signature is assignable to C's signature. <br>
	 * <br>
	 * 'A signature X is assignable to a signature Y' means: <br>
	 * For every type in X, Xi, the following is true: <br>
	 * Xi is asignable to the type Yi from the signature Y, i.e Yi is a supertype or superinterface
	 * for Xi. <br>
	 * <br>
	 * If the type contains more than one matching constructor, the best matching constructor is
	 * choosen and returned. If in the matching constructors at least two constructors exist, of
	 * which neither signature is assignable to the other one, no constructor is returned. This
	 * situation is called ambigiosity.
	 * 
	 * @param type
	 *            The type, in which to search for a constructor.
	 * @param signature
	 *            The signature, to which a constructor is searched that is assignable by the
	 *            signature.
	 * @param <T>
	 *            Derived from the base class type parameter.
	 * @return The constructor with best assignable signature. <code>null</code> is returned, if no
	 *         constructor or ambigious constructors exist.
	 */
	public static <T> Constructor<T> findConstructor(final Class<T> type, final Class<?>[] signature) {
		if (type == null || signature == null) {
			return null;
		}

		try {
			final Constructor<T> constructor = type.getDeclaredConstructor(signature);
			if (constructor != null) {
				return constructor;
			}
		} catch (final Exception e1) {
			// No constructor found -> search all the constructors.
			// continue.
		}

		final Constructor<?>[] constructors = type.getDeclaredConstructors();
		if (constructors == null) {
			return null;
		}

		Constructor<T> tempMatch = null;

		Label: for (final Constructor<?> constructor : constructors) {
			final Class<?>[] formalParameters = constructor.getParameterTypes();

			if (formalParameters.length != signature.length) {
				continue;
			}

			for (int i = 0; i < formalParameters.length; i++) {
				final Class<?> param_i = formalParameters[i];
				final Class<?> arg_i = signature[i];

				if (!param_i.isAssignableFrom(arg_i)) {
					continue Label;
				}
			}

			try {
				final Constructor<T> suitableConstructor = type.getDeclaredConstructor(formalParameters);
				if (tempMatch == null) {
					tempMatch = suitableConstructor;
				} else {
					tempMatch = getMoreGeneralConstructor(tempMatch, suitableConstructor);
					if (tempMatch == null) {
						// Ambigious constructors found!
						return null;
					}
				}
			} catch (final Exception e) {
			}
		}

		return tempMatch;
	}

	private static <T> Constructor<T> getMoreGeneralConstructor(final Constructor<T> c1, final Constructor<T> c2) {
		if (c1 == null) {
			return c2;
		}
		if (c2 == null) {
			return c1;
		}

		final Class<?>[] s1 = c1.getParameterTypes();
		final Class<?>[] s2 = c2.getParameterTypes();
		if (signatureIsAssignableFrom(s1, s2)) {
			return c1;
		}
		if (signatureIsAssignableFrom(s2, s1)) {
			return c2;
		}
		return null;
	}

	private static boolean signatureIsAssignableFrom(final Class<?>[] s1, final Class<?>[] s2) {
		if (s1 == null || s2 == null || s1.length != s2.length) {
			return false;
		}
		for (int i = 0; i < s1.length; i++) {
			if (s1[i] == null || s2[i] == null) {
				return false;
			}
			if (!s1[i].isAssignableFrom(s2[i])) {
				return false;
			}
		}
		return true;
	}

}
