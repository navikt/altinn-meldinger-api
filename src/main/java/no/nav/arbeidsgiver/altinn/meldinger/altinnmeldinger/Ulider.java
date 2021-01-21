package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import de.huxhorn.sulky.ulid.ULID;

public class Ulider {
    private static final ULID ulid = new ULID();

    public static String nextULID() {
        return ulid.nextULID();
    }

    public static boolean isValid(String ulidString) {
        return ulidString != null && ulidString.length() == 26;
    }
}
