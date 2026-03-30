package com.Jobstream.V0.enums;

public enum Provider {
    /** Registered with email + password only */
    LOCAL,
    /** Registered via Google OAuth only (no password set) */
    GOOGLE,
    /** Started as LOCAL and later linked a Google account (or vice-versa) */
    LOCAL_GOOGLE
}
