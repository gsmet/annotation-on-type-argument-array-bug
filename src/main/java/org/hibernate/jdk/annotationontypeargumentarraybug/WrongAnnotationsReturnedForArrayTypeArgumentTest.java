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

	@MyAnnotation3
	private @MyAnnotation1 String @MyAnnotation2 [] stringArray;

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		// prints MyAnnotation1
		System.out.println( "stringList - annotations found on type argument: "
				+ Arrays.toString( getFirstTypeArgumentOfListAnnotatedType( "stringList" ).getAnnotations() ) );

		// prints MyAnnotation2 as according to the JLS, it's the annotation targeting the whole array
		// see https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7.4
		System.out.println( "stringArrayList - annotations found on type argument: "
				+ Arrays.toString( getFirstTypeArgumentOfListAnnotatedType( "stringArrayList" ).getAnnotations() ) );

		// prints MyAnnotation1 as according to the JLS, it's the annotation targeting the inner type
		System.out.println( "stringArrayList - annotations found on nested array type argument: "
				+ Arrays.toString( getComponentTypeOfStringArrayNestedInList().getAnnotations() ) );

		// This is where the fun begins...

		Field field = WrongAnnotationsReturnedForArrayTypeArgumentTest.class.getDeclaredField( "stringArray" );

		// prints MyAnnotation3, MyAnnotation1 (so both annotations are considered to be on the field)
		System.out.println( Arrays.toString( field.getAnnotations() ) );

		// prints MyAnnotation2 as expected
		AnnotatedArrayType fieldType = (AnnotatedArrayType) field.getAnnotatedType();
		System.out.println( Arrays.toString( fieldType.getAnnotations() ) );

		// prints MyAnnotation3, MyAnnotation1: MyAnnotation3 also applies to the inner type
		System.out.println( Arrays.toString( fieldType.getAnnotatedGenericComponentType().getAnnotations() ) );

		// With this in mind, I don't see how we would be able to support the following use case (where @Size would target the size of the array):
		// @Size(min = 2)
		// private @Email String[] emails;
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
