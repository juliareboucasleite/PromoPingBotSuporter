package com.promoping.bot.security;

import net.dv8tion.jda.api.entities.Member;

import java.util.Set;

public class AccessControl {

    private static final Set<Long> ALLOWED_ROLES = Set.of(
            1460657851562459168L,
            1442937735253065758L,
            1442655601682419722L,
            1454132938059813010L,
            1460656070832951449L,
            1442655668904398980L,
            1460655734600630354L,
            1454133429858730005L
    );

    public static boolean canUseBot(Member member) {
        if (member == null) return false;

        return member.getRoles().stream()
                .anyMatch(role -> ALLOWED_ROLES.contains(role.getIdLong()));
    }
}
