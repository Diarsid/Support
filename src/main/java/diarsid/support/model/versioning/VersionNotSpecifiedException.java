package diarsid.support.model.versioning;

public class VersionNotSpecifiedException extends VersionException {

    public VersionNotSpecifiedException(Class type) {
        super(type.getSimpleName() + " does not contains version marked with 'V' prefix");
    }
}
