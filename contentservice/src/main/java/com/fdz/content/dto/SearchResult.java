package com.fdz.content.dto;

import com.fdz.common.utils.Page;
import lombok.Data;

@Data
public class SearchResult<T> {

    private Page page;

    private T data;

    private Object searchCondition;
}
