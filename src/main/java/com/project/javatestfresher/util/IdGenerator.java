package com.project.javatestfresher.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IdGenerator implements IdentifierGenerator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyDDDHHmmssSSS");
    private static final char[] charset = "0123456789".toCharArray();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generateId();
    }

    public static String generateId() {
        return formatter.format(LocalDateTime.now()) + HashUtil.randomString(18, charset);
    }
}
