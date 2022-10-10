package diarsid.support.model.versioning;

public interface VersionedByClassName extends Versioned {

    public static int versionNumberByNameOf(Class type) {
        String name = type.getSimpleName();
        char V = 'V';
        int indexOfVersionSign = name.indexOf(V);

        if ( indexOfVersionSign < 0 ) {
            throw new VersionNotSpecifiedException(type);
        }

        if ( indexOfVersionSign == name.length()-1 ) {
            throw new VersionWrongFormatException("Version string is missed!");
        }

        String versionString = name.substring(indexOfVersionSign + 1);

        try {
            int version = Integer.parseInt(versionString);
            return version;
        }
        catch (NumberFormatException e) {
            throw new VersionWrongFormatException(e.getMessage());
        }
    }

    public static Version versionOf(Object o) {
        return new Version(versionNumberByNameOf(o.getClass()));
    }

    default int versionNumberByClassName() {
        return versionNumberByNameOf(this.getClass());
    }

    @Override
    default Version version() {
        return new Version(this.versionNumberByClassName());
    }
}
