package com.example.demo.mapper;

import org.apache.ibatis.annotations.Param;

public interface DocMapperCust {

    public void increasesViewCount(@Param("id") Long id);

    public void increasesVoteCount(@Param("id") Long id);
}
