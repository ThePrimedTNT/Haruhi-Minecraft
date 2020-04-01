package ai.haruhi.minecraft.networking

/**
 * All Protocol Versions for all currently playable Minecraft versions.
 * Any lost versions have not been included (ie, old Classic versions)
 *
 * We will not guarantee that all versions seen here will work as there has
 * been multiple fundamental differences between versions.
 *
 * All Alpha versions are versioned separately from their corresponding client.
 * Exceptions to this are 1.0.10, 1.0.12 and 1.0.14 as they did not have a public server.
 *
 * Any ambiguous versions (ie, Alpha Server 0.2.4 vs Release 1.7.2) have
 * been marked with warnings for your convenience.
 *
 * @author ThePrimedTNT
 * @author KJP12
 * @since 0.0.0
 * */
@Suppress("EnumEntryName")
enum class ProtocolVersion(val mcVersion: String, val protocolNum: Int) {
    /** Warning: Protocol conflict with [Beta 1.0][b1_0] & [Beta 1.1][b1_1] */
    c0_30("c0.30", 7),
    /**
     * Warning: Protocol conflict with [Beta 1.4][b1_4]
     * This version does *not* have a corresponding Alpha Server.
     * This version is also lost, but Alpha 1.0.11 isn't, we may support this version.
     * */
    a1_0_10("a1.0.10", 10),
    /**
     * Warning: Protocol conflict with [Beta 1.5][b1_4]
     * This version does *not* have a corresponding Alpha Server.
     * */
    @Deprecated(message = "Supporting client(s) are lost.") a1_0_12("a1.0.12", 11),
    /**
     * This version does *not* have a corresponding Alpha Server.
     * This version is also lost, but Alpha 1.0.14 isn't, we may support this version.
     * */
    a1_0_13("a1.0.13", 12),
    /**  Warning: Protocol conflict with [Beta 1.6][b1_6] */
    a0_1_0("a0.1.0", 13),
    /** Warning: Protocol conflict with [Beta 1.7][b1_7] */
    a0_1_2("a0.1.2", 14),
    // Alpha 1.0.17 resets server versioning
    a0_1_4("a0.1.4", 1),
    a0_2_0("a0.2.0", 2),
    a0_2_3("a0.2.3", 3),
    /** Warning: Protocol conflict with [Release 1.7.2-1.7.5][v1_7_2] */
    a0_2_4("a0.2.4", 4),
    /** Warning: Protocol conflict with [Release 1.7.6-1.7.10][v1_7_6] */
    a0_2_5("a0.2.5", 5),
    a0_2_6("a0.2.4", 6),
    /** Warning: Protocol conflict with [Classic 0.30][c0_30]; Conflict with [Beta 1.1][b1_1]? */
    b1_0("b1.0", 7),
    /** Warning: Protocol conflict with [Classic 0.30][c0_30]; Conflict with [Beta 1.0][b1_0]? */
    b1_1("b1.1", 7),
    b1_2("b1.2", 8),
    b1_3("b1.3", 9),
    /** Warning: Protocol conflict with [Alpha 1.0.10][a1_0_10] */
    b1_4("b1.4", 10),
    /** Warning: Protocol conflict with [Alpha 1.0.12][a1_0_12] (may drop warning) */
    b1_5("b1.5", 11),
    /** Warning: Protocol conflict with [Alpha Server 0.1.0][a0_1_0] */
    b1_6("b1.6", 13),
    /** Warning: Protocol conflict with [Alpha Server 0.1.2][a0_1_2] */
    b1_7("b1.7", 14),
    b1_8("b1.8", 17),
    v1_0_0("1.0.0", 22),
    v1_1("1.1", 23),
    v1_2_1("1.2.1", 28),
    v1_2_4("1.2.4", 29),
    v1_3_1("1.3.1", 39),
    /** Warning: Protocol conflict with [Release 1.8.x][v1_8] */
    v1_4_2("1.4.2", 47),
    v1_4_4("1.4.4", 49),
    v1_4_6("1.4.6", 51),
    v1_5("1.5", 60),
    v1_5_2("1.5.2", 61),
    v1_6_1("1.6.1", 73),
    v1_6_2("1.6.2", 74),
    v1_6_4("1.6.4", 78),
    // 13w41a resets the versioning.
    /** Warning: Protocol conflict with [Alpha Server 0.2.4][a0_2_4] */
    v1_7_2("1.7.2", 4),
    /** Warning: Protocol conflict with [Alpha Server 0.2.5][a0_2_5] */
    v1_7_6("1.7.6", 5),
    /** Warning: Protocol conflict with [Release 1.4.2][v1_4_2] */
    v1_8("1.8", 47),
    v1_9("1.9", 107),
    v1_9_1("1.9.1", 108),
    v1_9_2("1.9.2", 109),
    v1_9_3("1.9.3", 110),
    v1_10("1.10", 210),
    v1_11("1.11", 315),
    v1_11_1("1.11.1", 315),
    v1_12("1.12", 335),
    v1_12_1("1.12.1", 338),
    v1_12_2("1.12.1", 340),
    v1_13("1.13", 393),
    v1_13_1("1.13.1", 401),
    v1_13_2("1.13.2", 404),
    v1_14("1.14", 477),
    v1_14_1("1.14.1", 480),
    v1_14_2("1.14.2", 485),
    v1_14_3("1.14.3", 490),
    v1_14_4("1.14.4", 498),
    v1_15("1.15", 573),
    v1_15_1("1.15.1", 575),
    v1_15_2("1.15.2", 578);

    companion object {
        val CURRENT = v1_15_2
    }
}