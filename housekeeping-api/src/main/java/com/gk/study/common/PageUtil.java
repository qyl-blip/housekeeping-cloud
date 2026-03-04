package com.gk.study.common;

import java.util.Collections;
import java.util.List;

public class PageUtil {
    private PageUtil() {}

    public static <T> PageResult<T> of(List<T> fullList, Integer page, Integer pageSize) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        if (fullList == null || fullList.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, safePage, safeSize);
        }
        int total = fullList.size();
        int from = Math.min((safePage - 1) * safeSize, total);
        int to = Math.min(from + safeSize, total);
        List<T> sub = fullList.subList(from, to);
        return new PageResult<>(sub, total, safePage, safeSize);
    }
}









