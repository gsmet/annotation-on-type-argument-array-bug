package org.hibernate.jdk.annotationontypeargumentarraybug;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class WrongAnnotationsReturnedForArrayTypeArgumentTest {

	@SuppressWarnings("unused")
	private List<@MyAnnotation1 String> stringList;

	@SuppressWarnings("unused")
	private List<@MyAnnotation1 String @MyAnnotation2 []> stringArrayList;

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		// returns MyAnnotation1 which is correct
		System.out.println( "stringList - annotations found on type argument: "
				+ Arrays.toString( getFirstTypeArgumentOfListAnnotatedType( "stringList" ).getAnnotations() ) );

		// returns **MyAnnotation2** which is **incorrect** - it should be MyAnnotation1
		System.out.println( "stringArrayList - annotations found on type argument: "
				+ Arrays.toString( getFirstTypeArgumentOfListAnnotatedType( "stringArrayList" ).getAnnotations() ) );

		// returns **MyAnnotation1** which is **incorrect** - it should be MyAnnotation2
		System.out.println( "stringArrayList - annotations found on nested array type argument: "
				+ Arrays.toString( getComponentTypeOfStringArrayNestedInList().getAnnotations() ) );
	}

	private static AnnotatedType getFirstTypeArgumentOfListAnnotatedType(String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = WrongAnnotationsReturnedForArrayTypeArgumentTest.class.getDeclaredField( fieldName );
		AnnotatedParameterizedType fieldType = (AnnotatedParameterizedType) field.getAnnotatedType();
		return fieldType.getAnnotatedActualTypeArguments()[0];
	}

	private static AnnotatedType getComponentTypeOfStringArrayNestedInList() throws NoSuchFieldException, SecurityException {
		AnnotatedArrayType typeArgumentType = (AnnotatedArrayType) getFirstTypeArgumentOfListAnnotatedType( "stringArrayList" );
		return typeArgumentType.getAnnotatedGenericComponentType();
	}
}
