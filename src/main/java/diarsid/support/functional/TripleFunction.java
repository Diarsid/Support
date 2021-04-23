package diarsid.support.functional;

@FunctionalInterface
public interface TripleFunction<ARG_1, ARG_2, ARG_3, RESULT> {

    RESULT apply(ARG_1 arg1, ARG_2 arg2, ARG_3 arg3);
}
