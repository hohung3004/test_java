package com.project.javatestfresher.repository;


import com.project.javatestfresher.dto.user.request.SearchAdminRequest;
import com.project.javatestfresher.entity.UserEntity;
import com.project.javatestfresher.util.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Repository
public class CommonRepositoryCustom {

    @Autowired
    private QueryUtil queryUtil;

    public Page<UserEntity> searchAdmin(SearchAdminRequest request) {
        String table = "users";

        List<Object> params = new LinkedList<>();
        String where = "1 = 1";

        if (StringUtils.isNotBlank(request.getKeyword())) {
            String keyword = "%" + request.getKeyword().toLowerCase() + "%";
            where += " AND LOWER(user_name) LIKE ?";
            params.add(keyword);
        } else {
            where += " AND is_valid = true";
        }

        return queryUtil.getResultPage(table, where, params, request, UserEntity.class);
    }

}
