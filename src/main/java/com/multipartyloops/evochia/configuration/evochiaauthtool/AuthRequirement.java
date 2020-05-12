package com.multipartyloops.evochia.configuration.evochiaauthtool;

import com.multipartyloops.evochia.core.identity.user.entities.Roles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthRequirement {

    Roles[] allow() default {Roles.ADMIN, Roles.FINANCE, Roles.STAFF};
}
