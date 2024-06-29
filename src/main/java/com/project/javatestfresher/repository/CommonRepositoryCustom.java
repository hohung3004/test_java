package com.project.salebe.repository;

import com.project.salebe.dto.admin.request.SearchAdminRequest;
import com.project.salebe.dto.attendance.request.SearchAttendanceRequest;
import com.project.salebe.entity.AttendanceEntity;
import com.project.salebe.entity.UserEntity;
import com.project.salebe.util.QueryUtil;
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
        String count = "users.id";
        String columns = "users.*";
        String table = "users";

        List<Object> params = new LinkedList<>();
        String where = " 1 = 1 ";

        if (StringUtils.isNotBlank(request.getKeyword())) {
            table += " join roles on  users.role_id = roles.id";
            String keyword = "%" + request.getKeyword().toLowerCase() + "%";
            where += "AND roles.name = ? AND (LOWER(fullname) LIKE ? OR LOWER(phone_number) LIKE ? OR LOWER(email) LIKE ?)";
            params.add(request.getRole());
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        } else {
            table += " join roles on  users.role_id = roles.id";
            where += "AND roles.name = ? AND is_active != 0";
            params.add(request.getRole());
        }
        return queryUtil.getResultPage(count, columns, table, where, params, request, UserEntity.class);
    }

    public Page<AttendanceEntity> searchAttendance(SearchAttendanceRequest request) {
        String count = "attendance.id";
        String columns = "attendance.*";
        String table = "attendance";

        List<Object> params = new LinkedList<>();
        String where = " 1 = 1 ";
        if (StringUtils.isNotBlank(request.getKeyword()) && StringUtils.isNotBlank(request.getValue())) {
            if (request.getKeyword().equals("date")) {
                where += " and to_char(" + request.getKeyword() + ",'dd/mm/yyyy') = ? ";
                params.add(request.getValue());
            }
        }
        return queryUtil.getResultPage(count, columns, table, where, params, request, UserEntity.class);
    }

}
