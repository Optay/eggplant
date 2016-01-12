package org.seattlego.eggplant.io;

/**
 * This enum is used as a return value from the decode portion of XMLEncoderAGA.
 * It is an enum, rather than simple success/fail boolean because of the special
 * case of unversioned saves which may have players flipped black/white in all
 * games. This bug affected saves made with v1.0.45 and earlier. So, the encoder
 * returns a result value, so the user can be prompted to fix an inverted save.
 * 
 * 
 * @author Topsy
 */
public enum DecodeResult {
    Success, Failure, Unversioned;
}
