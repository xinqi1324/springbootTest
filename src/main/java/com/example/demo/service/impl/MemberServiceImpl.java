package com.example.demo.service.impl;


import com.example.demo.entity.Member;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.service.MemberService;
import com.example.demo.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    RedisUtils redisUtils;
    @Override
    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    @Override
    public String redis(String param) {
        return null;
    }
}
