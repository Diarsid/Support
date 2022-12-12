package diarsid.support.objects.references.impl;

import diarsid.support.objects.references.Result;

public class ResultVoid {

    public static class Success implements Result.Void {

        public static ResultVoid.Success SINGLETON = new Success();

        private Success() {}

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFail() {
            return false;
        }

        @Override
        public Result.Reason reason() {
            throw new IllegalStateException();
        }
    }

    public static class Fail implements Result.Void, Result.Reason {

        private final Object subject;

        public Fail(Object subject) {
            this.subject = subject;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFail() {
            return true;
        }

        @Override
        public Result.Reason reason() {
            return this;
        }

        @Override
        public Object subject() {
            return this.subject;
        }
    }
}
