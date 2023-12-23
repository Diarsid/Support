//package diarsid.support.tests;
//
//import org.junit.jupiter.api.extension.InvocationInterceptor;
//
//public class AwareOfTestAnnotations implements InvocationInterceptor {
//
////    private static final ThreadLocal<List<Annotation>> ANNOTATIONS = new ThreadLocal<>();
////
////    @Override
////    public void interceptTestMethod(
////            Invocation<Void> invocation,
////            ReflectiveInvocationContext<Method> invocationContext,
////            ExtensionContext extensionContext) throws Throwable {
////        ANNOTATIONS.set(asList(invocationContext.getExecutable().getAnnotations()));
////        try {
////            invocation.proceed();
////        }
////        finally {
////            ANNOTATIONS.remove();
////        }
////    }
////
////    public static <T> T oneAnnotationOrThrow(Class<T> type) {
////        return ANNOTATIONS
////                .get()
////                .stream()
////                .filter(type::isInstance)
////                .map(type::cast)
////                .findFirst()
////                .orElseThrow();
////    }
////
////    public static <T> Optional<T> oneAnnotation(Class<T> type) {
////        return ANNOTATIONS
////                .get()
////                .stream()
////                .filter(type::isInstance)
////                .map(type::cast)
////                .findFirst();
////    }
////
////    public static <T> List<T> allAnnotations(Class<T> type) {
////        return ANNOTATIONS
////                .get()
////                .stream()
////                .filter(type::isInstance)
////                .map(type::cast)
////                .collect(toList());
////    }
//}
