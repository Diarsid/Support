package diarsid.support.objects;

public enum TestEnum implements CommonEnum<TestEnum> {
    A, B;

    public static void main(String[] args) {
        A.equalTo(B);
        A.equalToAny(A, B);
    }
}
